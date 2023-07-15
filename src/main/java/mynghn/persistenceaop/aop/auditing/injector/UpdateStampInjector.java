package mynghn.persistenceaop.aop.auditing.injector;

import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import mynghn.persistenceaop.aop.auditing.aspect.AuditingAspect;
import mynghn.persistenceaop.aop.auditing.injector.base.StampInjectorWithContext;
import mynghn.persistenceaop.aop.auditing.session.AdviceSession;
import mynghn.persistenceaop.entity.base.UpdateStamp;

@Slf4j
public class UpdateStampInjector extends StampInjectorWithContext {

    public UpdateStampInjector(AuditingAspect context) {
        super(context);
    }

    @Override
    public boolean supports(Class<?> stampType) {
        return stampType == UpdateStamp.class;
    }

    @Override
    public void injectStamp(Object payload) {
        if (!(payload instanceof UpdateStamp stampPayload)) {
            throw new IllegalStateException(String.format(
                    "Update stamp injected payload type '%s' is not assignable to UpdateStamp",
                    payload.getClass().getName()
            ));
        }

        AdviceSession currSession = context.getSession();
        if (currSession == null) {
            throw new IllegalStateException("Audit advice session is missing.");
        }

        if (stampPayload.getLastModifiedAt() == null) {
            LocalDateTime currSessionTime = currSession.getTime();
            stampPayload.setLastModifiedAt(currSessionTime);
            log.debug("Last modified time '{}' injected to payload: {}",
                    currSessionTime, payload);
        }
        if (stampPayload.getLastModifiedBy() == null) {
            String currSessionUsername = currSession.getUsername();
            stampPayload.setLastModifiedBy(currSessionUsername);
            log.debug("Last modified username '{}' injected to payload: {}",
                    currSessionUsername, payload);
        }
    }
}
