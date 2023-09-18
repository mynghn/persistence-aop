package mynghn.persistenceaop.aop.injection.injector.base;

import mynghn.persistenceaop.aop.injection.session.AdviceSessionManager;
import mynghn.persistenceaop.aop.injection.session.AdviceSession;

public abstract class StampInjectorWithContext implements StampInjector {

    protected AdviceSession getSession() {
        AdviceSession currSession = AdviceSessionManager.currSession();
        if (currSession == null) {
            throw new IllegalStateException("Advice session not found.");
        }
        return currSession;
    }
}
