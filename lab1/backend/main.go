package main

import (
	"encoding/json"
	"fmt"
	"log"
	"net/http"
	"math"
	"time"
	"os"
)


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
	http.HandleFunc("/fcgi-bin/", fcgHandler)
	http.Handle("/", http.FileServer(http.Dir("front")))

	log.Println("Server running on :" + port)
	log.Fatal(http.ListenAndServe(":" + port, nil))
}

func fcgHandler(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "application/json")
	w.Header().Set("Access-Control-Allow-Origin", "*")
	w.Header().Set("Access-Control-Allow-Methods", "POST, OPTIONS")
	w.Header().Set("Access-Control-Allow-Headers", "Content-Type")

	currentTime := time.Now()

	if r.Method == "OPTIONS" {
		w.WriteHeader(http.StatusOK)
		return
	}

	if r.Method != "POST" {
		sendError(w, "Only POST method allowed", http.StatusMethodNotAllowed)
		return
	}

	var data RequestData
	if err := json.NewDecoder(r.Body).Decode(&data); err != nil {
		sendError(w, "Invalid JSON: " + err.Error(), http.StatusBadRequest)
		return
	}
	result, err := Point_is_hit(data.X, data.Y, data.R)
	if err != nil {
		sendError(w, err.Error(), http.StatusBadRequest)
		return
	}
    var uptime = time.Now().Sub(currentTime)

	response := ResponseData{
		Result:      result,
		CurrentTime: currentTime.Format("2006-01-02 15:04:05"),
		Uptime: fmt.Sprintf("%d", uptime.Nanoseconds()),
	}
    log.Println("ответ отправлен")
	json.NewEncoder(w).Encode(response)
}

func Point_is_hit(x, y, r float64) (bool, error) {
	result := ((x >= 0) && (x <= (r/2))) && ((y >= 0) && (y <= r))
	result = result || ((x <= 0 && y >= 0) && (math.Pow(x, 2) + math.Pow(y, 2) <= math.Pow(r, 2)))
	result = result || ((x >= 0 && y <= 0) && (y >= (x - r)))

	return result, nil
}

func sendError(w http.ResponseWriter, message string, statusCode int) {
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