package mynghn.persistenceaop.aop.aspect;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mynghn.persistenceaop.aop.annotations.Id;
import mynghn.persistenceaop.aop.annotations.Payload;
import mynghn.persistenceaop.aop.annotations.RecordHistory;
import mynghn.persistenceaop.aop.annotations.Specification;
import mynghn.persistenceaop.entity.base.Entity;
import mynghn.persistenceaop.mapper.base.GenericMapper;
import mynghn.persistenceaop.mapper.base.HistoryMapper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class PersistenceAspect {

    private final ApplicationContext applicationContext;

    private static <E, ID> GenericMapper<E, ID> getTargetGenericMapper(JoinPoint joinPoint) {
        Object joinPointTarget = joinPoint.getTarget();
        if (!(joinPointTarget instanceof GenericMapper<?, ?>)) {
            throw new IllegalStateException(
                    "Join point target of non GenericMapper interface encountered."
            );
        }
        @SuppressWarnings("unchecked")
        GenericMapper<E, ID> joinPointMapper = (GenericMapper<E, ID>) joinPointTarget;
        return joinPointMapper;
    }

    private static <A extends Annotation> int[] getAnnotatedArgsIndexArr(
            JoinPoint joinPoint,
            Class<A> annotationClass
    ) {
        Annotation[][] paramAnnotationsArr = ((MethodSignature) joinPoint.getSignature())
                .getMethod().getParameterAnnotations();

        return IntStream.range(0, paramAnnotationsArr.length)
                .filter(idx -> Arrays.stream(paramAnnotationsArr[idx])
                        .anyMatch(annotation -> annotation.annotationType() == annotationClass))
                .toArray();
    }

    private static <ID> ID getEntityId(JoinPoint joinPoint) {
        Object[] joinPointArgs = joinPoint.getArgs();

        // 1. Use @Id annotated arg
        int[] idArgsIndexArr = getAnnotatedArgsIndexArr(joinPoint, Id.class);
        if (idArgsIndexArr.length > 1) {
            throw new IllegalStateException("Too many @Id annotated args found.");
        }
        if (idArgsIndexArr.length == 1) {
            @SuppressWarnings("unchecked")
            ID entityId = (ID) joinPointArgs[idArgsIndexArr[0]];
            return entityId;
        }

        // 2. Use @Payload annotated Entity<ID> type arg
        int[] payloadArgsIndexArr = getAnnotatedArgsIndexArr(joinPoint, Payload.class);
        if (payloadArgsIndexArr.length > 1) {
            throw new IllegalStateException("Too many @Payload annotated args found.");
        }
        if (payloadArgsIndexArr.length == 0) {
            throw new IllegalStateException("Both @Id and @Payload annotated args not found.");
        }
        Object payloadArg = joinPointArgs[payloadArgsIndexArr[0]];
        if (!(payloadArg instanceof Entity<?>)) {
            throw new IllegalStateException(
                    "@Payload arg (not along with @Id arg) must be Entity<ID> type for history recording."
            );
        }

        @SuppressWarnings("unchecked")
        Entity<ID> entity = ((Entity<ID>) payloadArg);

        return entity.getId();
    }

    private static <S> S getEntitySpecification(JoinPoint joinPoint) {
        int[] specArgsIndexArr = getAnnotatedArgsIndexArr(joinPoint, Specification.class);
        if (specArgsIndexArr.length > 1) {
            throw new IllegalStateException("Too many @Specification annotated args found.");
        }
        if (specArgsIndexArr.length == 0) {
            throw new IllegalStateException("@Specification annotated arg not found.");
        }
        @SuppressWarnings("unchecked") S entitySpec = (S) joinPoint.getArgs()[specArgsIndexArr[0]];
        return entitySpec;
    }

    @Pointcut("target(mynghn.persistenceaop.mapper.base.GenericMapper)")
    private void targetGenericMapper() {
    }

    @AfterReturning(
            value = "targetGenericMapper() && @annotation(mynghn.persistenceaop.aop.annotations.RecordHistory)",
            returning = "entitiesUpdated"
    )
    public <S, E extends Entity<ID>, ID> void recordEntityHistory(
            JoinPoint joinPoint,
            int entitiesUpdated
    ) {
        log.debug("Advice starting on Join point: {}", joinPoint);
        GenericMapper<E, ID> joinPointMapper = getTargetGenericMapper(joinPoint);

        HistoryMapper<E, ID> historyMapper = getHistoryMapperBean(
                joinPointMapper.getEntityType(), joinPointMapper.getEntityIdType()
        );

        if (entitiesUpdated == 0) {
            log.debug("0 rows updated. Skipping advice...");
            return;
        }

        MethodSignature joinPointSignature = (MethodSignature) joinPoint.getSignature();
        RecordHistory annotation = joinPointSignature.getMethod().getAnnotation(RecordHistory.class);

        int historiesRecorded;
        if (annotation.many()) {
            S entitySpec = getEntitySpecification(joinPoint);
            historiesRecorded = historyMapper.recordHistories(entitySpec);
        } else {
            ID entityId = getEntityId(joinPoint);
            historiesRecorded = historyMapper.recordHistory(entityId);
        }

        if (historiesRecorded != entitiesUpdated) {
            throw new IllegalStateException(String.format(
                    "Updated entities(%d) and recorded histories(%d) count do not match.",
                    entitiesUpdated, historiesRecorded
            ));
        }
    }

    private <E extends Entity<ID>, ID> HistoryMapper<E, ID> getHistoryMapperBean(
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
