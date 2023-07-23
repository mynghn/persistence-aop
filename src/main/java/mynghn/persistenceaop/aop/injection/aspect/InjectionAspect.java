package mynghn.persistenceaop.aop.injection.aspect;

import java.lang.annotation.Annotation;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import mynghn.persistenceaop.aop.injection.annotation.Injected;
import mynghn.persistenceaop.aop.injection.injector.CreateStampInjector;
import mynghn.persistenceaop.aop.injection.injector.SoftDeleteStampInjector;
import mynghn.persistenceaop.aop.injection.injector.UpdateStampInjector;
import mynghn.persistenceaop.aop.injection.injector.base.StampInjector;
import mynghn.persistenceaop.aop.injection.session.AdviceSession;
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
public class InjectionAspect {

    private static final ThreadLocal<AdviceSession> session = new ThreadLocal<>();

    private static final List<StampInjector> injectors = List.of(
            new CreateStampInjector(session),
            new UpdateStampInjector(session),
            new SoftDeleteStampInjector()
    );

    private static Stream<Pair<Injected, Object>> getAnnotatedTargetArgs(JoinPoint joinPoint) {
        Annotation[][] paramAnnotationsArr = ((MethodSignature) joinPoint.getSignature()).getMethod()
                .getParameterAnnotations();
        Object[] joinPointArgs = joinPoint.getArgs();

        return IntStream.range(0, paramAnnotationsArr.length)
                .mapToObj(idx -> Pair.of(Arrays.stream(paramAnnotationsArr[idx])
                        .filter(annotation -> annotation.annotationType() == Injected.class)
                        .findFirst(), joinPointArgs[idx]))
                .filter(optionalArgPair -> optionalArgPair.getLeft().isPresent())
                .map(optionalArgPair -> Pair.of(
                        (Injected) optionalArgPair.getLeft().orElseThrow(),
                        optionalArgPair.getRight()
                ));
    }

    private void startSession() {
        if (session.get() != null) {
            throw new IllegalStateException("Instance scope advice session is already in use.");
        }
        AdviceSession newSession = AdviceSession.builder()
                .time(LocalDateTime.now())
                // FIXME: replace w/ real data in practice
                // e.g. get user info from current HttpSession obj
                .username(RandomStringUtils.random(10, true, true))
                .build();
        session.set(newSession);
        log.debug("Advice session started: '{}'", newSession);
    }

    private void endSession() {
        AdviceSession currSession = session.get();
        if (currSession == null) {
            throw new IllegalStateException(
                    "Advice session does not exist. Start a session first, or illegal session termination has occurred."
            );
        }
        session.remove();
        log.debug("Advice session terminated. ({})", currSession);
    }

    @Before("@annotation(mynghn.persistenceaop.aop.injection.annotation.InjectStamp)")
    public void auditBefore(JoinPoint joinPoint) {
        log.debug("Advice starting on join point: {}", joinPoint);

        Stream<Pair<Injected, Object>> annotatedArgTargets = getAnnotatedTargetArgs(joinPoint);

        // Start session
        startSession();

        annotatedArgTargets.forEach(
                pair -> {
                    Injected annotation = pair.getLeft();
                    Object arg = pair.getRight();

                    injectStamps(annotation.value(), arg);
                }
        );

        // End session
        endSession();
    }


    private void injectStamps(Class<?>[] stampTypes, Object payload) {
        Arrays.stream(stampTypes).forEach(stampType -> {
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
