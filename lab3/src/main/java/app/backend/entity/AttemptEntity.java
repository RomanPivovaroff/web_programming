package app.backend.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "attempts")
public class AttemptEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "x", nullable = false, precision = 10, scale = 6)
    private BigDecimal x;

    @Column(name = "y", nullable = false, precision = 10, scale = 6)
    private BigDecimal y;

    @Column(name = "r", nullable = false, precision = 10, scale = 6)
    private BigDecimal r;

    @Column(name = "hit", nullable = false)
    private boolean hit;

    @Column(name = "duration", nullable = false)
    private long duration;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public AttemptEntity() {
    }

    public AttemptEntity(BigDecimal x, BigDecimal y, BigDecimal r,
                         boolean hit, long duration) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.hit = hit;
        this.duration = duration;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BigDecimal getX() { return x; }
    public void setX(BigDecimal x) { this.x = x; }

    public BigDecimal getY() { return y; }
    public void setY(BigDecimal y) { this.y = y; }

    public BigDecimal getR() { return r; }
    public void setR(BigDecimal r) { this.r = r; }

    public boolean isHit() { return hit; }
    public void setHit(boolean hit) { this.hit = hit; }

    public long getDuration() { return duration; }
    public void setDuration(long duration) { this.duration = duration; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}