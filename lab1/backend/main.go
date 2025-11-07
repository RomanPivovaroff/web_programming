package main

import (
	"encoding/json"
	"fmt"
	"log"
	"net/http"
	"net/url"
	"math"
	"time"
	"os"
	"strconv"
	"context"
	"github.com/jackc/pgx/v5/pgxpool"
)

type Point struct {
    ID              int       `db:"id"`
    X               float64   `db:"x"`
    Y               float64   `db:"y"`
    R               float64   `db:"z"`
    Hit             bool      `db:"hit"`
    Date            time.Time `db:"date"`
    ProgramWorkTime int64   `db:"program_work_time"`
}

type RequestData struct {
	X float64 `json:"x"`
	Y float64 `json:"y"`
	R float64 `json:"r"`
}

type ResponseData struct {
	Result       bool      `json:"result,omitempty"`
	CurrentTime  string    `json:"current_time,omitempty"`
	Uptime       string    `json:"uptime,omitempty"`
	Error        string    `json:"error,omitempty"`
}

func main() {
    port := "8080"
    if len(os.Args) > 1 {
        port = os.Args[1]
    }

    db, err := connectDB()
        if err != nil {
            log.Fatalf("Ошибка подключения к БД: %v", err)
        }
    defer db.Close()

	http.HandleFunc("/fcgi-bin/", FcgHandler(db))
	http.HandleFunc("/points/get", getPointsHandler(db))
	http.HandleFunc("/points/clear", clearPointsHandler(db))
	http.Handle("/", http.FileServer(http.Dir("front")))

	log.Println("Server running on :" + port)
	log.Fatal(http.ListenAndServe(":" + port, nil))
}

func getPointsHandler(db *pgxpool.Pool) http.HandlerFunc {
    return func(w http.ResponseWriter, r *http.Request) {
        ctx := r.Context()
        points, err := getPoints(ctx, db)
        if err != nil {
            SendError(w, err.Error(), http.StatusInternalServerError)
            return
        }

        w.Header().Set("Content-Type", "application/json")
        json.NewEncoder(w).Encode(points)
    }
}

func clearPointsHandler(db *pgxpool.Pool) http.HandlerFunc {
    return func(w http.ResponseWriter, r *http.Request) {
        ctx := r.Context()

        _, err := db.Exec(ctx, "TRUNCATE TABLE points RESTART IDENTITY;")
        if err != nil {
            SendError(w, "Ошибка при очистке базы данных", http.StatusInternalServerError)
            return
        }

        response := ResponseData{
            Result: true,
            CurrentTime: time.Now().Format("2006-01-02 15:04:05"),
        }
        w.Header().Set("Content-Type", "application/json")
        w.WriteHeader(http.StatusOK)
        json.NewEncoder(w).Encode(response)
    }
}


func FcgHandler(db *pgxpool.Pool) http.HandlerFunc {
    return func(w http.ResponseWriter, r *http.Request) {
        currentTime := time.Now()

        if r.Method == "OPTIONS" {
            w.WriteHeader(http.StatusOK)
            return
        }
        if r.Method != "GET" {
            SendError(w, "Only GET method allowed", http.StatusMethodNotAllowed)
            return
        }

        query := r.URL.Query()
        radius, err := strconv.ParseFloat(query.Get("r"), 64)
        if err != nil {
            SendError(w, err.Error(), http.StatusBadRequest)
            return
        }
        x, err := strconv.ParseFloat(query.Get("x"), 64)
        if err != nil {
            SendError(w, err.Error(), http.StatusBadRequest)
            return
        }
        y, err := strconv.ParseFloat(query.Get("y"), 64)
        if err != nil {
            SendError(w, err.Error(), http.StatusBadRequest)
            return
        }
        if  !(y > -3 && y < 3) {
            SendError(w, "значение y должно быть от -3 до 3", http.StatusBadRequest)
            return
        }
        result, err := PointIsHit(x, y, radius)
        if err != nil {
            SendError(w, err.Error(), http.StatusBadRequest)
            return
        }
        var uptime = time.Now().Sub(currentTime).Nanoseconds()
        ctx := context.Background()
        p := Point{X: x, Y: y, R: radius, Hit: result, Date: time.Now(), ProgramWorkTime: uptime}
        if err := insertPoint(ctx, db, p); err != nil {
            SendError(w, err.Error(), http.StatusBadRequest)
            return
        }

        response := ResponseData{
            Result:      result,
            CurrentTime: currentTime.Format("2006-01-02 15:04:05"),
            Uptime: fmt.Sprintf("%d", uptime),
        }
        log.Println("ответ отправлен")
        json.NewEncoder(w).Encode(response)
    }
}

func PointIsHit(x, y, r float64) (bool, error) {
	result := ((x >= 0) && (x <= (r/2))) && ((y >= 0) && (y <= r))
	result = result || ((x <= 0 && y >= 0) && (math.Pow(x, 2) + math.Pow(y, 2) <= math.Pow(r/2, 2)))
	result = result || ((x >= 0 && y <= 0) && (y >= (x - r)))

	return result, nil
}

func SendError(w http.ResponseWriter, message string, statusCode int) {
	currentTime := time.Now()
	uptime := "0"

	response := ResponseData{
		Error:       message,
		CurrentTime: currentTime.Format("2006-01-02 15:04:05"),
		Uptime:      uptime,
	}

	w.WriteHeader(statusCode)
	json.NewEncoder(w).Encode(response)
}

func connectDB() (*pgxpool.Pool, error) {
    user := os.Getenv("USERNAME")
    password := os.Getenv("PASSWORD")
    dbURL := fmt.Sprintf("postgres://%s:%s@db:5432/studs", url.QueryEscape(user), url.QueryEscape(password))
    ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
    defer cancel()

    pool, err := pgxpool.New(ctx, dbURL)
    if err != nil {
        return nil, fmt.Errorf("failed to connect: %w", err)
    }

    return pool, nil
}

func insertPoint(ctx context.Context, db *pgxpool.Pool, p Point) error {
    query := `
        INSERT INTO points (x, y, r, hit, date, program_work_time)
        VALUES ($1, $2, $3, $4, $5, $6)
    `
    _, err := db.Exec(ctx, query, p.X, p.Y, p.R, p.Hit, p.Date, p.ProgramWorkTime)
    return err
}

func getPoints(ctx context.Context, db *pgxpool.Pool) ([]Point, error) {
    rows, err := db.Query(ctx, "SELECT id, x, y, r, hit, date, program_work_time FROM points")
    if err != nil {
        return nil, err
    }
    defer rows.Close()

    var points []Point
    for rows.Next() {
        var p Point
        err := rows.Scan(&p.ID, &p.X, &p.Y, &p.R, &p.Hit, &p.Date, &p.ProgramWorkTime)
        if err != nil {
            return nil, err
        }
        points = append(points, p)
    }

    return points, nil
}
