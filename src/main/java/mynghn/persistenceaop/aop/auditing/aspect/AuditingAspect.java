package mynghn.persistenceaop.aop.auditing.aspect;

import java.lang.annotation.Annotation;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.lang.model.type.NullType;
import lombok.extern.slf4j.Slf4j;
import mynghn.persistenceaop.aop.auditing.annotation.InjectStamp;
import mynghn.persistenceaop.aop.auditing.injector.CreateStampInjector;
import mynghn.persistenceaop.aop.auditing.injector.SoftDeleteStampInjector;
import mynghn.persistenceaop.aop.auditing.injector.UpdateStampInjector;
import mynghn.persistenceaop.aop.auditing.injector.base.StampInjector;
import mynghn.persistenceaop.aop.auditing.session.AdviceSession;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class AuditingAspect {

    private final List<StampInjector> injectors;
    private AdviceSession session;

    public AuditingAspect() {
        injectors = List.of(
                new CreateStampInjector(this),
                new UpdateStampInjector(this),
                new SoftDeleteStampInjector()
        );
    }

    private static Stream<Pair<InjectStamp, Object>> getAnnotatedTargetArgs(JoinPoint joinPoint) {
        Annotation[][] paramAnnotationsArr = ((MethodSignature) joinPoint.getSignature()).getMethod()
                .getParameterAnnotations();
        Object[] joinPointArgs = joinPoint.getArgs();

        return IntStream.range(0, paramAnnotationsArr.length)
                .mapToObj(idx -> Pair.of(Arrays.stream(paramAnnotationsArr[idx])
                        .filter(annotation -> annotation.annotationType() == InjectStamp.class)
                        .findFirst(), joinPointArgs[idx]))
                .filter(optionalArgPair -> optionalArgPair.getLeft().isPresent())
                .map(optionalArgPair -> Pair.of(
                        (InjectStamp) optionalArgPair.getLeft().orElseThrow(),
                        optionalArgPair.getRight()
                ));
    }

    private static Stream<Class<?>> getInjectingStampTypes(InjectStamp annotation) {
        if (annotation.value() != NullType.class) {
            return Stream.of(annotation.value());
        }
        return Arrays.stream(annotation.stampTypes());
    }

    public AdviceSession getSession() {
        return session;
    }

    private void startSession() {
        if (session != null) {
            throw new IllegalCallerException("Instance scope advice session is already in use.");
        }
        session = AdviceSession.builder()
                .time(LocalDateTime.now())
                // FIXME: replace w/ real data in practice
                // e.g. get user info from current HttpSession obj
                .username(RandomStringUtils.random(10, true, true))
                .build();
    }

    private void endSession() {
        if (session == null) {
            throw new IllegalStateException(
                    "Advice session does not exist. Start a session first, or illegal session termination has occurred."
            );
        }
        session = null;
    }

    @Before("@annotation(mynghn.persistenceaop.aop.auditing.annotation.Audit)")
    public void auditBefore(JoinPoint joinPoint) {
        log.debug("Advice starting on join point: {}", joinPoint);

        Stream<Pair<InjectStamp, Object>> annotatedArgTargets = getAnnotatedTargetArgs(joinPoint);

        // Start session
        startSession();
        log.debug("Started advice session: {}", session);

        annotatedArgTargets.forEach(
                pair -> {
                    InjectStamp annotation = pair.getLeft();
                    Object arg = pair.getRight();

                    injectStamps(getInjectingStampTypes(annotation), arg);
                }
        );

        // End session
        log.debug("Advice execution finished. Terminating advice session...");
        endSession();
    }


    private void injectStamps(Stream<Class<?>> stampTypes, Object payload) {
        stampTypes.forEach(stampType -> {
            if (!stampType.isAssignableFrom(payload.getClass())) {
                throw new IllegalArgumentException(String.format(
                        "Payload with type '%s' is not assignable to stamp type '%s",
                        payload.getClass().getName(), stampType.getName()
                ));
            }

            Optional<StampInjector> injectorOptional = injectors.stream()
                    .filter(injector -> injector.supports(stampType))
                    .findFirst();

            if (injectorOptional.isPresent()) {
                injectorOptional.get().injectStamp(payload);
            } else {
                log.warn(
                        "Skipping data injection of stamp type '{}'... Responsible StampInjector class not registered.",
                        stampType.getName()
                );
            }
        });
    }
}
