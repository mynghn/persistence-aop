package mynghn.persistenceaop.aop.injection.injector;

import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import mynghn.persistenceaop.aop.context.aspect.RequestSessionProviderAspect;
import mynghn.persistenceaop.aop.context.contexts.RequestSession;
import mynghn.persistenceaop.aop.injection.injector.base.StampInjectorWithContext;
import mynghn.persistenceaop.entity.base.UpdateStamp;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UpdateStampInjector extends StampInjectorWithContext {

    public UpdateStampInjector(RequestSessionProviderAspect sessionProvider) {
        super(sessionProvider);
    }

    @Override
    public boolean supports(Class<?> stampType) {
        return stampType == UpdateStamp.class;
    }

    @Override
    public void injectStamp(Object payload) {
        if (!(payload instanceof UpdateStamp stampPayload)) {
            throw new IllegalArgumentException(String.format(
                    "Update stamp injected payload type '%s' is not assignable to UpdateStamp",
                    payload.getClass().getName()
            ));
        }

        RequestSession currSession = getSession();
        if (stampPayload.getLastModifiedAt() == null) {
            LocalDateTime currSessionTime = currSession.time();
            stampPayload.setLastModifiedAt(currSessionTime);
            log.debug("Last modified time '{}' injected to payload: {}",
                    currSessionTime, payload);
        }
        if (stampPayload.getLastModifiedBy() == null) {
            String currSessionUsername = currSession.username();
            stampPayload.setLastModifiedBy(currSessionUsername);
            log.debug("Last modified username '{}' injected to payload: {}",
                    currSessionUsername, payload);
        }
    }
}
