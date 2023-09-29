package mynghn.persistenceaop.aop.context.contexts;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record RequestSession(LocalDateTime time, String username) implements ExecutionScopeContext {

}
