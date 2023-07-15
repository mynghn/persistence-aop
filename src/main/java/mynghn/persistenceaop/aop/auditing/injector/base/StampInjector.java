package mynghn.persistenceaop.aop.auditing.injector.base;

public interface StampInjector {

    boolean supports(Class<?> stampType);

    void injectStamp(Object payload);
}
