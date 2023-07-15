package mynghn.persistenceaop.aop.auditing.injector.base;

import mynghn.persistenceaop.aop.auditing.aspect.AuditingAspect;

public abstract class StampInjectorWithContext implements StampInjector {

    protected AuditingAspect context;

    protected StampInjectorWithContext(AuditingAspect context) {
        this.context = context;
    }
}
