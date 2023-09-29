package mynghn.persistenceaop.aop.context.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import mynghn.persistenceaop.aop.context.contexts.ExecutionScopeContext;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UseExecutionScopeContext {

    Class<? extends ExecutionScopeContext>[] value();
}
