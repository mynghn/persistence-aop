package mynghn.persistenceaop.aop.context.aspect;

import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import mynghn.persistenceaop.aop.context.contexts.RequestSession;
import mynghn.persistenceaop.aop.context.contexts.ExecutionScopeContext;
import org.apache.commons.lang3.RandomStringUtils;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class RequestSessionProviderAspect extends ExecutionScopeContextProvider<RequestSession> {

    @Override
    protected boolean supports(Class<? extends ExecutionScopeContext> contextClass) {
        return RequestSession.class.equals(contextClass);
    }

    @Override
    public RequestSession buildContext() {
        return RequestSession.builder()
                .time(LocalDateTime.now())
                // FIXME: replace w/ real data in practice
                // e.g. get user info from current HttpSession obj
                .username(RandomStringUtils.random(10, true, true))
                .build();
    }

    @Override
    protected Logger getLogger() {
        return log;
    }
}
