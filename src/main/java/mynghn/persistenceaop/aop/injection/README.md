## Workflow of repeated data injection AOP advice

1. Annotate methods w/ [`@InjectStamp`](./annotation/InjectStamp.java) to mark as point cut target.
2. Annotate method parameters w/ [`@Injected`](./annotation/Injected.java) to mark as injection target.
3. Declare injecting data(a.k.a. _Stamp_) types in `@InjectStamp` annotation with stamp classes.
   - e.g. `@InjectStamp(UpdateStamp.class)`, `@InjectStamp(stampTypes={CreateStamp.class, UpdateStamp.class})`
4. Advice [`InjectionAspect.auditBefore()`](./aspect/InjectionAspect.java) is called _@Before_ executing `@Audit` annotated method.
5. Extract annotation and object pairs of `@InjectStamp` annotated arguments from join point method.
6. Start advice execution scope session and set session info (e.g. current timestamp, username) for consistent usage within advice runtime.
7. For all `@InjectStamp` annotated arguments, corresponding data for each stamp types declared in `@InjectStamp` annotation is injected(set) to argument object.
   - Responsible [`StampInjector`](./injector/base/StampInjector.java) instance for each stamp type is selected and called for data injection.   
8. Terminate session and finish advice execution.
9. Data injected argument gets passed to original method. 

### About [`StampInjector`](./injector/base/StampInjector.java) interface and _Chain of Responsibility_ pattern

> Each stamp type has its own `StampInjector` implementation and all implementations (in use) should be registered in `InjectionAspect` class.
>
> ```java
> private final List<StampInjector> injectors;
> public InjectionAspect() {
>     injectors = List.of(
>             new CreateStampInjector(this),
>             new UpdateStampInjector(this),
>             new SoftDeleteStampInjector()
>     );
> }
> ```
>
> For flexible future extensions with additional stamp types coming in, _Chain of Responsibility_ design pattern is introduced w/ `StampInjector` interface.
>
> For each stamp types specified in `@InjectStamp` annotation, responsible `StampInjector` instance is determined with `supports()` test and corresponding data is injected to argument object with `injectStamp()` method.
> ```java
> private void injectStamps(Stream<Class<?>> stampTypes, Object payload) {
>     stampTypes.forEach(stampType -> {
>             ...
>             Optional<StampInjector> injectorOptional = injectors.stream()
>                     .filter(injector -> injector.supports(stampType))
>                     .findFirst();
>             if (injectorOptional.isPresent()) {
>                 injectorOptional.get().injectStamp(payload);
>             } else {
>                 ...
>             }
>      });
> }
> ```
### About advice session implementation

> For some data, injected value should be same across different injection times from each `StampInjector` call.
> 
> e.g. `createdAt` timestamp of [`CreateStamp`](../../entity/base/CreateStamp.java) and `lastModifiedAt` timestamp of [`UpdateStamp`](../../entity/base/UpdateStamp.java) should have same value when injected before initial record insert
>
> If `StampInjector.injectStamp(Object payload)` method received another parameter for data, it could be a way to pass same data across differenct `StampInjector` calls. But, it could be rather inappropriate approach in a sense of retaining concrete and universal method interface. 
> 
> In that aspect, session with advice execution scope is introduced to resolve this issue.
> 
> `InjectionAspect` instance stores [`AdviceSession`](./session/AdviceSession.java) object as a member variable `session`.
> ```java
> private AdviceSession session;
> ```
> 
> And inside every advice, 
> 1. Execution scope session gets started before data injection, by assigning a new `AdviceSession` instance to empty `session` variable.
>    ```java
>    private void startSession() {
>        if (session != null) {
>            throw new IllegalCallerException(...);
>        }
>        session = AdviceSession.builder()
>                .time(LocalDateTime.now())
>                .username(...)
>                .build();
>    }
>    ```
> 2. During injection step, handler instances which extended [`StampInjectorWithContext`](./injector/base/StampInjectorWithContext.java) can access advice session through constructor injected `InjectionAspect` object.
>    ```java
>    // StampInjectorWithContext.java
>    public abstract class StampInjectorWithContext implements StampInjector {
>        protected InjectionAspect context;
>        protected StampInjectorWithContext(InjectionAspect context) {
>            this.context = context;
>        }
>    }
>    
>    // CreateStampInjector.java
>    public class CreateStampInjector extends StampInjectorWithContext {
>        ...
>        public void injectStamp(Object payload) {
>            ...
>            AdviceSession currSession = context.getSession();
>            ...
>        }
>    }
>    ```
> 3. After data injection, session is terminated by assigning `null` to `session` member variable
>    ```java
>    private void endSession() {
>        if (session == null) {
>            throw new IllegalCallerException(...);
>        }
>        session = null;
>    }
>    ```
> So in effect, `session` variable has innate lifecycle of `InjectionAspect` instance scope, but it's managed to have lifecycle of each advice execution scope.
> 
> Another AOP advice providing `AdviceSession` instance to other advices might be a solution to this `session` variable lifecycle inconsistency issue.
