package mynghn.persistenceaop.aop.injection.injector.base;

public interface StampInjector {

    boolean supports(Class<?> stampType);

    void injectStamp(Object payload);
}
