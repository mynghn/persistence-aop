package mynghn.persistenceaop.aop.auditing.session;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class AdviceSession {

    private final LocalDateTime time;

    private final String username;
}
