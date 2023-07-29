package mynghn.persistenceaop.aop.injection.session;

public final class AdviceSessionContainer {

    private static final ThreadLocal<AdviceSession> threadLocalSessionStorage = new ThreadLocal<>();

    public static AdviceSession getSession() {
        return threadLocalSessionStorage.get();
    }

    public static void setSession(AdviceSession session) {
        threadLocalSessionStorage.set(session);
    }

    public static void removeSession() {
        threadLocalSessionStorage.remove();
    }
}
