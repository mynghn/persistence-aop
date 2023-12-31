package mynghn.persistenceaop.aop.injection.injector;

import lombok.extern.slf4j.Slf4j;
import mynghn.persistenceaop.aop.injection.injector.base.StampInjector;
import mynghn.persistenceaop.entity.base.SoftDeleteEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SoftDeleteStampInjector implements StampInjector {

    @Override
    public boolean supports(Class<?> stampType) {
        return stampType == SoftDeleteEntity.class;
    }

    @Override
    public void injectStamp(Object payload) {
        if (!(payload instanceof SoftDeleteEntity stampPayload)) {
            throw new IllegalArgumentException(String.format(
                    "Soft delete stamp injected payload type '%s' is not assignable to SoftDeleteEntity",
                    payload.getClass().getName()
            ));
        }

        if (stampPayload.getIsDeleted() == null) {
            Boolean initIsDeleted = false; // isDeleted is always false when first creating an entity
            stampPayload.setIsDeleted(initIsDeleted);
            log.debug("Initial value '{}' for isDeleted injected to payload: {}",
                    initIsDeleted, payload);
        }
    }
}
