package mynghn.persistenceaop.aop.context.aspect;

import java.util.Arrays;
import java.util.Optional;
import java.util.Stack;
import mynghn.persistenceaop.aop.context.annotation.UseExecutionScopeContext;
import mynghn.persistenceaop.aop.context.contexts.ExecutionScopeContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;

@Aspect
abstract class ExecutionScopeContextProvider<CTX extends ExecutionScopeContext> {

    private final ThreadLocal<Optional<CTX>> threadLocalContextContainer = ThreadLocal.withInitial(
            Optional::empty);

    private final ThreadLocal<Stack<Signature>> threadLocalAdviceCallStack = ThreadLocal.withInitial(
            Stack::new);

    /**
     * Check if {@link ExecutionScopeContextProvider ExecutionScopeContextProvider} extended class
     * supports encountered <i>context</i> class.
     *
     * @param contextClass {@link ExecutionScopeContext ExecutionScopeContext} extended class
     *                     annotated on some method w/
     *                     {@link UseExecutionScopeContext @UseExecutionScopeContext}
     * @return If this aspect class supports provided <i>context</i> class
     */
    protected abstract boolean supports(Class<? extends ExecutionScopeContext> contextClass);

    /**
     * Instantiate new <i>context</i> for new AOP advisory
     *
     * @return Newly created <i>context</i> instance
     */
    protected abstract CTX buildContext();

    protected abstract Logger getLogger(); // for precise logging in regard to implementation class

    /**
     * Return optional <i>context</i> which is present only when current retrieval is under any
     * <i>context</i> providing advisory
     *
     * @return Optional <i>context</i> object
     */
    public Optional<CTX> get() {
        return threadLocalContextContainer.get();
    }

    @Around("@annotation(annotation)")
    public Object advice(ProceedingJoinPoint pjp, UseExecutionScopeContext annotation)
            throws Throwable {
        Class<? extends ExecutionScopeContext>[] annotatedCtxClasses = annotation.value();

        if (supportsAny(annotatedCtxClasses)) {
            setUp(pjp.getSignature());
        }

        Object returned = null;
        Throwable thrown = null;

        try {
            returned = pjp.proceed();
        } catch (Throwable t) {
            thrown = t;
        }

        if (supportsAny(annotatedCtxClasses)) {
            tearDown();
        }

        if (thrown != null) {
            throw thrown;
        }
        return returned;
    }

    private boolean supportsAny(Class<? extends ExecutionScopeContext>[] contextClasses) {
        return Arrays.stream(contextClasses).anyMatch(this::supports);
    }

    private void setUp(Signature joinPointSignature) {
        Stack<Signature> adviceCallStack = threadLocalAdviceCallStack.get();

        if (adviceCallStack.isEmpty()) {
            if (threadLocalContextContainer.get().isPresent()) {
                throw new IllegalStateException("Execution scope context already exists.");
            }
            threadLocalContextContainer.set(Optional.of(buildContext()));
            getLogger().debug("==================== Now providing context... ====================");
        }

        adviceCallStack.push(joinPointSignature);
    }

    private void tearDown() {
        Stack<Signature> adviceCallStack = threadLocalAdviceCallStack.get();
        adviceCallStack.pop();

        if (adviceCallStack.isEmpty()) {
            if (threadLocalContextContainer.get().isEmpty()) {
                throw new IllegalStateException("Execution scope context does not exist.");
            }
            threadLocalContextContainer.remove();
            getLogger().debug("==================== Context closed ====================");
        }
    }
}
