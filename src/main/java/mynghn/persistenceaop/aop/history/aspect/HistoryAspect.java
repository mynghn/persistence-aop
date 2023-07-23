package mynghn.persistenceaop.aop.history.aspect;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mynghn.persistenceaop.aop.history.annotation.RecordHistory;
import mynghn.persistenceaop.mapper.base.EntityMapper;
import mynghn.persistenceaop.mapper.base.HistoryMapper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class HistoryAspect {

    private final ApplicationContext applicationContext;

    private static <E> EntityMapper<E> getTargetEntityMapper(JoinPoint joinPoint) {
        Object joinPointTarget = joinPoint.getTarget();
        if (!(joinPointTarget instanceof EntityMapper<?>)) {
            throw new IllegalStateException(
                    "Join point target of non EntityMapper interface encountered."
            );
        }
        @SuppressWarnings("unchecked")
        EntityMapper<E> joinPointMapper = (EntityMapper<E>) joinPointTarget;
        return joinPointMapper;
    }

    private static <E> void recordOneHistory(
            HistoryMapper<E> historyMapper, E updatedEntity) {
        if (updatedEntity == null) {
            log.debug("0 entities updated. Skipping advice...");
            return;
        }

        int historiesRecorded = historyMapper.recordHistory(updatedEntity);

        if (historiesRecorded == 0) {
            throw new IllegalStateException("No histories recorded.");
        }
        if (historiesRecorded > 1) {
            throw new IllegalStateException(String.format(
                    "Too many histories recorded: %d. Should have recorded only 1.",
                    historiesRecorded
            ));
        }
    }

    private static <E> void recordManyHistories(
            HistoryMapper<E> historyMapper, List<E> updatedEntities
    ) {
        if (updatedEntities.size() == 0) {
            log.debug("0 entities updated. Skipping advice...");
            return;
        }

        int historiesRecorded = historyMapper.recordHistories(updatedEntities);

        if (historiesRecorded != updatedEntities.size()) {
            throw new IllegalStateException(String.format(
                    "Updated entities(%d) and recorded histories(%d) count do not match.",
                    updatedEntities.size(), historiesRecorded
            ));
        }
    }

    @AfterReturning(pointcut = "@annotation(annotation)", returning = "updated")
    public <E> void recordEntityHistory(
            JoinPoint joinPoint,
            RecordHistory annotation,
            Object updated
    ) {
        log.debug("Advice starting on join point: {}", joinPoint);
        EntityMapper<E> joinPointMapper = getTargetEntityMapper(joinPoint);

        HistoryMapper<E> historyMapper = getHistoryMapperByType(joinPointMapper.getEntityType());

        if (annotation.many()) {
            if (!(updated instanceof List<?>)) {
                throw new IllegalStateException(
                        "@RecordHistory(many=true) annotated method should return List<ID> type."
                );
            }
            @SuppressWarnings("unchecked") List<E> updatedEntities = (List<E>) updated;
            recordManyHistories(historyMapper, updatedEntities);
        } else {
            @SuppressWarnings("unchecked") E updatedEntity = (E) updated;
            recordOneHistory(historyMapper, updatedEntity);
        }
    }

    private <E> HistoryMapper<E> getHistoryMapperByType(Class<E> entityType) {
        String[] beanNames = applicationContext.getBeanNamesForType(
                ResolvableType.forClassWithGenerics(HistoryMapper.class, entityType));
        if (beanNames.length == 0) {
            throw new IllegalStateException(String.format(
                    "HistoryMapper bean for Entity '%s' not found.",
                    entityType.getName()
            ));
        }
        if (beanNames.length > 1) {
            throw new IllegalStateException(String.format(
                    "Too many HistoryMapper beans for Entity '%s' found: %d",
                    entityType.getName(), beanNames.length
            ));
        }
        String beanName = beanNames[0];

        @SuppressWarnings("unchecked")
        HistoryMapper<E> historyMapper = applicationContext.getBean(beanName,
                HistoryMapper.class);

        return historyMapper;
    }
}
