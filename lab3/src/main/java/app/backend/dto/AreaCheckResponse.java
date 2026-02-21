package app.backend.dto;

import app.backend.entity.AttemptEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record AreaCheckResponse(BigDecimal x, BigDecimal y, BigDecimal r, boolean hit, long duration, LocalDateTime date) {

    public AreaCheckResponse(BigDecimal x, BigDecimal y, BigDecimal r, boolean hit, long duration) {
        this(x, y, r, hit, duration, LocalDateTime.parse(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))));
    }

    public static AreaCheckResponse fromEntity(AttemptEntity entity) {
        return new AreaCheckResponse(
                entity.getX(),
                entity.getY(),
                entity.getR(),
                entity.isHit(),
                entity.getDuration(),
                entity.getCreatedAt()
        );
    }

    public BigDecimal getX() { return x; }
    public BigDecimal getY() { return y; }
    public BigDecimal getR() { return r; }
    public boolean isHit() { return hit; }
    public long getDuration() { return duration; }
    public LocalDateTime getDate() { return date; }
}
