package mynghn.persistenceaop.aop.injection.injector.base;

import mynghn.persistenceaop.aop.injection.aspect.InjectionAspect;

public abstract class StampInjectorWithContext implements StampInjector {

    protected InjectionAspect context;

    protected StampInjectorWithContext(InjectionAspect context) {
        this.context = context;
    }
}
