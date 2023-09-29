package mynghn.persistenceaop.aop.injection.aspect;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class InjectionAdviceDelegatorAspect {

    private final InjectionAdviceCore injectionAdviceCore;

    @Before("@annotation(mynghn.persistenceaop.aop.injection.annotation.InjectStamp)")
    public void delegateAuditing(JoinPoint joinPoint) {
        injectionAdviceCore.auditBefore(joinPoint);
    }
}
