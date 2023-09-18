package mynghn.persistenceaop.aop.injection.session;

public final class AdviceSessionContainer {

    private static final ThreadLocal<AdviceSession> threadLocalSessionStorage = new ThreadLocal<>();

    public static AdviceSession getSession() {
        return threadLocalSessionStorage.get();
    }

    public static AdviceSession startSession() {
        AdviceSession currSession = AdviceSessionContainer.getSession();
        if (currSession != null) {
            throw new IllegalStateException("Advice scope session is already in use.");
        }
        AdviceSession newSession = AdviceSessionFactory.newSession();
        threadLocalSessionStorage.set(newSession);
        return newSession;
    }

    public static void endSession() {
        AdviceSession currSession = AdviceSessionContainer.getSession();
        if (currSession == null) {
            throw new IllegalStateException(
                    "Advice session does not exist. Start a session first, or an illegal session termination might have occurred."
            );
        }
        threadLocalSessionStorage.remove();
    }
}
