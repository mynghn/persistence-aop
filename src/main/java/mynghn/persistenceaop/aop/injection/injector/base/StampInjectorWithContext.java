package mynghn.persistenceaop.aop.injection.injector.base;

import mynghn.persistenceaop.aop.injection.session.AdviceSession;

public abstract class StampInjectorWithContext implements StampInjector {

    private final ThreadLocal<AdviceSession> session;

    protected StampInjectorWithContext(ThreadLocal<AdviceSession> session) {
        this.session = session;
    }

    protected AdviceSession getSession() {
        AdviceSession currSession = session.get();
        if (currSession == null) {
            throw new IllegalStateException("Advice session not found.");
        }
        return currSession;
    }
}
