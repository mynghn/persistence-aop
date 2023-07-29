package mynghn.persistenceaop.aop.injection.session;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class AdviceSessionContainer extends ThreadLocal<AdviceSession> {

    private static final AdviceSessionContainer instance = new AdviceSessionContainer();

    public static AdviceSessionContainer getInstance() {
        return instance;
    }

    private AdviceSessionContainer() {
    }
}
