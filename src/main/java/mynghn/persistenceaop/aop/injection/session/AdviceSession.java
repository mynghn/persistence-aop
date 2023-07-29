package mynghn.persistenceaop.aop.injection.session;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record AdviceSession(LocalDateTime time, String username) {

}
