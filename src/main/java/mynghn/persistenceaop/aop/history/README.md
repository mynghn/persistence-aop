## Workflow of DB revision history recording AOP advice
#### (*_This document is currently out of date. Future update will be done._)

1. Annotate insert/update Mapper methods w/ [`@RecordHistory`](./annotation/RecordHistory.java) to mark as point cut target.
2. Advice [`HistoryAspect.recordEntityHistory()`](./aspect/HistoryAspect.java) is called _@AfterReturning_ `@RecordHistory` annotated method execution result.
3. Extract AOP target instance from `JoinPoint` object. It should be an instance of [`EntityMapper<E>`](../../mapper/base/EntityMapper.java). This is required for further [`HistoryMapper<E>`](../../mapper/base/HistoryMapper.java) search by entity(table) type.
   - Each entity(table) type `E` has corresponding `EntityMapper<E>` and `HistoryMapper<E>`.
4. Find corresponding `HistoryMapper` bean from Spring `ApplicationContext` using generic type(`E`) of previously seen `EntityMapper<E>` which is current join point target.
   - Reflective approach w/ `ApplicationContext.getBeanNamesForType()` and `ResolvableType` is applied.
5. If join point method is annotated `@RecordHistory(many = true)`, cast join point method's returned object to `List<E>` and pass it to `HitoryMapper.recordHistories(List<E>)`. 
6. Else, cast returned object to `E` type and pass it to `HitoryMapper.recordHistory(E)`


### About [`CrudMapper`](../../mapper/base/CrudMapper.java) and [`HistoryMapper`](../../mapper/base/HistoryMapper.java) interface design

> `CrudMapper<E, ID>` is a generic base interface for MyBatis mappers designed for simple CRUD operations of single table of entity. It is a one desired form of `EntityMapper<E>` extension, so in this project, it is assumed that join point targets are going to have similar interface with the `CrudMapper`.  
> 
> Therefore `HistoryMapper`, which is the core interface of this AOP feature, is also designed with `CrudMapper` in mind. 
> 
> In particular, `HistoryMapper.recordHistories()` receives list of entities to record multiple histories, and this interface is for batch update methods like `CrudMapper.updateAll(S spec, P payload)`.
> ```java
> recordHistories(List<E> entities);
> ```
> 
> Such methods may receive parameter `spec` which determines(filters) updating rows. And the problem here is, if a column appeared in the specification gets updated, `REPEATABLE READ` with the same specification after that update is not guaranteed. 
> 
> So, `HistoryMapper` should be able to answer 2 questions for exact multiple histories recording.
> 
> 1. which entity records have been updated
> 2. exact payload of that update
> 
> This is not the only solution for this problem, but I found that the one with returning the exact snapshot of the original entities right after update/insert is the simplest one.
> (in regard to the current project requirement of MyBatis & PostgreSQL)
> 
> For that reason, batch update methods like `CrudMapper.updateAll()` should return list of entities just been updated.
> ```java
> List<E> updateAll(...);
> ```
> 
> In particular stack of MyBatis & PostgreSQL, this can be achieved by using `RETURNING *` clause of PostgreSQL right after insert/update statement and placing the SQL script inside `<select>` tag in MyBatis xml mapper file.
> ```xml
> <select id="updateAll" resultType="entityType">
>     UPDATE ...
>     SET ...
>     WHERE ...
>     RETURNING *
> </select>
> ```
> (cf. In MyBatis, `<insert>` or `<update>` tagged methods can only return updated rows count(`int`) or none(`void`).)
