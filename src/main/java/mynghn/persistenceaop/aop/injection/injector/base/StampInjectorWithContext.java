package mynghn.persistenceaop.aop.injection.injector.base;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import mynghn.persistenceaop.aop.context.aspect.RequestSessionProviderAspect;
import mynghn.persistenceaop.aop.context.contexts.RequestSession;

@RequiredArgsConstructor
public abstract class StampInjectorWithContext implements StampInjector {

    private final RequestSessionProviderAspect sessionProvider;

    protected RequestSession getSession() {
        Optional<RequestSession> currSessionOptional = sessionProvider.get();
        if (currSessionOptional.isEmpty()) {
            throw new IllegalStateException("Advice session not found.");
        }
        return currSessionOptional.get();
    }
}
