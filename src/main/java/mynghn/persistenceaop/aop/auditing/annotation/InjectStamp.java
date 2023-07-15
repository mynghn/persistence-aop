package mynghn.persistenceaop.aop.auditing.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.lang.model.type.NullType;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface InjectStamp {

    Class<?> value() default NullType.class;

    Class<?>[] stampTypes() default {};
}
