package mynghn.persistenceaop.aop.injection.aspect;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import mynghn.persistenceaop.aop.context.annotation.UseExecutionScopeContext;
import mynghn.persistenceaop.aop.context.aspect.RequestSessionProviderAspect;
import mynghn.persistenceaop.aop.context.contexts.RequestSession;
import mynghn.persistenceaop.aop.injection.annotation.Injected;
import mynghn.persistenceaop.aop.injection.injector.CreateStampInjector;
import mynghn.persistenceaop.aop.injection.injector.SoftDeleteStampInjector;
import mynghn.persistenceaop.aop.injection.injector.UpdateStampInjector;
import mynghn.persistenceaop.aop.injection.injector.base.StampInjector;
import org.apache.commons.lang3.tuple.Pair;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class InjectionAdviceCore {

    private final List<StampInjector> injectors;

    public InjectionAdviceCore(RequestSessionProviderAspect sessionProvider) {
        injectors = List.of(new CreateStampInjector(sessionProvider),
                new UpdateStampInjector(sessionProvider),
                new SoftDeleteStampInjector());
    }

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

    @UseExecutionScopeContext(RequestSession.class)
    public void auditBefore(JoinPoint joinPoint) {
        log.debug("Advice starting on join point: {}", joinPoint);

        Stream<Pair<Injected, Object>> annotatedArgTargets = getAnnotatedTargetArgs(joinPoint);

        annotatedArgTargets.forEach(
                pair -> {
                    Injected annotation = pair.getLeft();
                    Object arg = pair.getRight();

                    injectStamps(annotation.value(), arg);
                }
        );
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
