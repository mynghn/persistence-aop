package mynghn.persistenceaop.aop.injection.session;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class AdviceSessionContainer extends ThreadLocal<AdviceSession> {

    private static final AdviceSessionContainer instance = new AdviceSessionContainer();

    private AdviceSessionContainer() {
    }

    public static AdviceSession getSession() {
        return instance.get();
    }

    public static void setSession(AdviceSession session) {
        instance.set(session);
    }

    public static void removeSession() {
        instance.remove();
    }
}
