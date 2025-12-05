package app.backend;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record AreaCheckResponse(BigDecimal x, BigDecimal y, BigDecimal r, boolean hit, long duration, LocalDateTime date) {

    public AreaCheckResponse(BigDecimal x, BigDecimal y, BigDecimal r, boolean hit, long duration) {
        this(x, y, r, hit, duration, LocalDateTime.parse(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))));
    }
}
