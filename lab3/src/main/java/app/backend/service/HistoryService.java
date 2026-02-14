package app.backend.service;

import app.backend.dto.AreaCheckResponse;
import app.backend.entity.AttemptEntity;
import app.backend.exception.InvalidPointDataException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Named
@ApplicationScoped
@Transactional
public class HistoryService {

    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    private PointLogicService pointLogicService;


    public AreaCheckResponse saveAttempt(BigDecimal x, BigDecimal y, BigDecimal r,
                                         boolean isCanvas) {
        try {
            AreaCheckResponse response = pointLogicService.createResponse(x, y, r, isCanvas);

            AttemptEntity entity = convertToEntity(response);
            entityManager.persist(entity);
            entityManager.flush();

            return response;

        } catch (InvalidPointDataException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    public List<AreaCheckResponse> getAllAttempts() {
        List<AttemptEntity> entities = entityManager
                .createQuery("SELECT a FROM AttemptEntity a ORDER BY a.createdAt DESC",
                        AttemptEntity.class)
                .getResultList();

        return entities.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<AreaCheckResponse> getAttemptsPaginated(int page, int pageSize) {
        List<AttemptEntity> entities = entityManager
                .createQuery("SELECT a FROM AttemptEntity a ORDER BY a.createdAt DESC",
                        AttemptEntity.class)
                .setFirstResult(page * pageSize)
                .setMaxResults(pageSize)
                .getResultList();

        return entities.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }


    public void clearHistory() {
        entityManager.createQuery("DELETE FROM AttemptEntity").executeUpdate();
    }

    private AttemptEntity convertToEntity(AreaCheckResponse dto) {
        return new AttemptEntity(
                dto.x(),
                dto.y(),
                dto.r(),
                dto.hit(),
                dto.duration()
        );
    }

    private AreaCheckResponse convertToDto(AttemptEntity entity) {
        return new AreaCheckResponse(
                entity.getX(),
                entity.getY(),
                entity.getR(),
                entity.isHit(),
                entity.getDuration(),
                entity.getCreatedAt()
        );
    }
}