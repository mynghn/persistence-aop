package mynghn.persistenceaop.aop.auditing.injector;

import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import mynghn.persistenceaop.aop.auditing.aspect.AuditingAspect;
import mynghn.persistenceaop.aop.auditing.injector.base.StampInjectorWithContext;
import mynghn.persistenceaop.aop.auditing.session.AdviceSession;
import mynghn.persistenceaop.entity.base.CreateStamp;

@Slf4j
public class CreateStampInjector extends StampInjectorWithContext {

    public CreateStampInjector(AuditingAspect context) {
        super(context);
    }

    @Override
    public boolean supports(Class<?> stampType) {
        return stampType == CreateStamp.class;
    }

    @Override
    public void injectStamp(Object payload) {
        if (!(payload instanceof CreateStamp stampPayload)) {
            throw new IllegalStateException(String.format(
                    "Create stamp injected payload type '%s' is not assignable to CreateStamp",
                    payload.getClass().getName()
            ));
        }

        AdviceSession currSession = context.getSession();
        if (currSession == null) {
            throw new IllegalStateException("Audit advice session is missing.");
        }

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
