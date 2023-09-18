package mynghn.persistenceaop.aop.injection.session;

import java.time.LocalDateTime;
import org.apache.commons.lang3.RandomStringUtils;

public class AdviceSessionBuilder {

    public static AdviceSession newSession() {
        return AdviceSession.builder()
                .time(LocalDateTime.now())
                // FIXME: replace w/ real data in practice
                // e.g. get user info from current HttpSession obj
                .username(RandomStringUtils.random(10, true, true))
                .build();
    }
}
