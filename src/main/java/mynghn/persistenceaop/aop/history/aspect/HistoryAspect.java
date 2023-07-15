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

    private static <E, ID> EntityMapper<E, ID> getTargetEntityMapper(JoinPoint joinPoint) {
        Object joinPointTarget = joinPoint.getTarget();
        if (!(joinPointTarget instanceof EntityMapper<?, ?>)) {
            throw new IllegalStateException(
                    "Join point target of non EntityMapper interface encountered."
            );
        }
        @SuppressWarnings("unchecked")
        EntityMapper<E, ID> joinPointMapper = (EntityMapper<E, ID>) joinPointTarget;
        return joinPointMapper;
    }

    private static <E, ID> void recordOneHistory(
            HistoryMapper<E, ID> historyMapper, ID updatedEntityId) {
        if (updatedEntityId == null) {
            log.debug("0 entities updated. Skipping advice...");
            return;
        }

        int historiesRecorded = historyMapper.recordHistory(updatedEntityId);

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

    private static <E, ID> void recordManyHistories(
            HistoryMapper<E, ID> historyMapper, List<ID> updatedEntityIds
    ) {
        if (updatedEntityIds.size() == 0) {
            log.debug("0 entities updated. Skipping advice...");
            return;
        }

        int historiesRecorded = historyMapper.recordHistories(updatedEntityIds);

        if (historiesRecorded != updatedEntityIds.size()) {
            throw new IllegalStateException(String.format(
                    "Updated entities(%d) and recorded histories(%d) count do not match.",
                    updatedEntityIds.size(), historiesRecorded
            ));
        }
    }

    @AfterReturning(pointcut = "@annotation(annotation)", returning = "updated")
    public <E, ID> void recordEntityHistory(
            JoinPoint joinPoint,
            RecordHistory annotation,
            Object updated
    ) {
        log.debug("Advice starting on join point: {}", joinPoint);
        EntityMapper<E, ID> joinPointMapper = getTargetEntityMapper(joinPoint);

        HistoryMapper<E, ID> historyMapper = getHistoryMapperByType(
                joinPointMapper.getEntityType(), joinPointMapper.getEntityIdType()
        );

        if (annotation.many()) {
            if (!(updated instanceof List<?>)) {
                throw new IllegalStateException(
                        "@RecordHistory(many=true) annotated method should return List<ID> type."
                );
            }
            @SuppressWarnings("unchecked") List<ID> updatedEntityIds = (List<ID>) updated;
            recordManyHistories(historyMapper, updatedEntityIds);
        } else {
            @SuppressWarnings("unchecked") ID updatedEntityId = (ID) updated;
            recordOneHistory(historyMapper, updatedEntityId);
        }
    }

    private <E, ID> HistoryMapper<E, ID> getHistoryMapperByType(
            Class<E> entityType,
            Class<ID> entityIdType
    ) {
        String[] beanNames = applicationContext.getBeanNamesForType(
                ResolvableType.forClassWithGenerics(HistoryMapper.class, entityType, entityIdType));
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
        HistoryMapper<E, ID> historyMapper = applicationContext.getBean(beanName,
                HistoryMapper.class);

        return historyMapper;
    }
}
