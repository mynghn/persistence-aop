## Workflow of DB revision history recording AOP advice

1. Annotate insert/update Mapper methods w/ [`@RecordHistory`](./annotation/RecordHistory.java) to mark as point cut target.
2. Advice [`HistoryAspect.recordEntityHistory()`](./aspect/HistoryAspect.java) is called _@AfterReturning_ `@RecordHistory` annotated method execution result.
3. Extract AOP target instance from `JoinPoint` object. It should be an instance of [`EntityMapper<E, ID>`](../../mapper/base/EntityMapper.java). This is required for further [`HistoryMapper<E, ID>`](../../mapper/base/HistoryMapper.java) search by entity(table) type.
   - Each entity(table) type `E` has corresponding `EntityMapper<E, ID>` and `HistoryMapper<E, ID>`.
4. Find corresponding `HistoryMapper` bean from Spring `ApplicationContext` using generic types(`E`, `ID`) of previously seen `EntityMapper<E, ID>` which is current join point target.
   - Reflective approach w/ `ApplicationContext.getBeanNamesForType()` and `ResolvableType` is applied.
5. If join point method is annotated `@RecordHistory(many = true)`, cast join point method's returned object to `List<ID>` and pass it to `HitoryMapper.recordHistories(List<ID>)`. 
6. Else, cast returned object to `ID` type and pass it to `HitoryMapper.recordHistory(ID)`


### About [`CrudMapper`](../../mapper/base/CrudMapper.java) and [`HistoryMapper`](../../mapper/base/HistoryMapper.java) interface design

> `CrudMapper<E, ID>` is a generic base interface for MyBatis mappers designed for simple CRUD operations of single entity. It is a one desired form of `EntityMapper<E, ID>` extension, so it is assumed that join point targets are going to have similar interface with `CrudMapper`.  
> 
> Therefore `HistoryMapper` is also designed with `CrudMapper` in mind. In particular, `HistoryMapper.recordHistories()` receives list of entity ids to record multiple histories, and this interface is for batch update methods like `CrudMapper.updateAll(S spec, P payload)`.
> ```java
> recordHistories(List<ID> entityIds);
> ```
> 
> Such methods may receive specification parameter which determines(filters) updating rows. And the problem here is, if a column appeared in specification gets updated, `REPEATABLE READ` w/ same specification after update is not guaranteed. 
> 
> So, innately immutable identifiers of updated rows are needed when writing history record after a batch update. (when using `INSERT INTO SELECT FROM` SQL clause instead of explicitly provided history record data to write)
> 
> For that reason, batch update methods like `CrudMapper.updateAll()` should return ids of updated rows.
> ```java
> List<ID> updateAll(...);
> ```
> 
> This can be achieved by using `RETURNING ...` clause of PostgreSQL right after insert/update statement and placing the SQL script inside `<select>` tag in MyBatis xml mapper file.
> ```xml
> <select id="updateAll" resultType="string">
>     UPDATE ...
>     SET ...
>     WHERE ...
>     RETURNING id
> </select>
> ```
> (In MyBatis, `<insert>` or `<update>` tagged methods can only return updated rows count(`int`) or none(`void`).)
