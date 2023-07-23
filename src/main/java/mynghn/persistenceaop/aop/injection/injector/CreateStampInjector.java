package mynghn.persistenceaop.aop.injection.injector;

import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import mynghn.persistenceaop.aop.injection.injector.base.StampInjectorWithContext;
import mynghn.persistenceaop.aop.injection.session.AdviceSession;
import mynghn.persistenceaop.entity.base.CreateStamp;

@Slf4j
public class CreateStampInjector extends StampInjectorWithContext {

    public CreateStampInjector(ThreadLocal<AdviceSession> sessionContainer) {
        super(sessionContainer);
    }

    @Override
    public boolean supports(Class<?> stampType) {
        return stampType == CreateStamp.class;
    }

    @Override
    public void injectStamp(Object payload) {
        if (!(payload instanceof CreateStamp stampPayload)) {
            throw new IllegalArgumentException(String.format(
                    "Create stamp injected payload type '%s' is not assignable to CreateStamp",
                    payload.getClass().getName()
            ));
        }

        AdviceSession currSession = getSession();
        if (stampPayload.getCreatedAt() == null) {
            LocalDateTime currSessionTime = currSession.getTime();
            stampPayload.setCreatedAt(currSessionTime);
            log.debug("Created time '{}' injected to payload: {}",
                    currSessionTime, payload);
        }
        if (stampPayload.getCreatedBy() == null) {
            String currSessionUsername = currSession.getUsername();
            stampPayload.setCreatedBy(currSessionUsername);
            log.debug("Created username '{}' injected to payload: {}",
                    currSessionUsername, payload);
        }
    }
}
