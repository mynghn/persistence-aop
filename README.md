# Persistence AOP
Demo project for some AOP features implementation tackling common issues in persistence layer.

## Some common concerns...

1. For some database tables, revision history needs to be recorded. &rarr; [**Feature 1**](./src/main/java/mynghn/persistenceaop/aop/history)
   - _Specific strategies may vary, but in this project..._
   - Each table has its own history table.
   - History tables consists of full snapshot of original columns with some additional ones (e.g. sequence numbering of recorded histories by each original entity).

2. Some columns are repeated across tables and have identical value assigning strategy. &rarr; [**Feature 2**](./src/main/java/mynghn/persistenceaop/aop/injection)
   - e.g. _first inserted time_, _last modified time_, etc...

3. Some records need to be fetched from database and remain cached for repetitive usage. &rarr; [**Feature 3**](./src/main/java/mynghn/persistenceaop/aop/context)
   - e.g. _system level enum codes referenced across the whole application_

## Implemented features

1. [Record histories **after** database revision](./src/main/java/mynghn/persistenceaop/aop/history)
2. [Inject common data(_stamp_) into method arguments **before** execution](./src/main/java/mynghn/persistenceaop/aop/injection)
3. [Provide temporary _context_ **around** method execution](./src/main/java/mynghn/persistenceaop/aop/context)

## Technical requirements
- Persistence Framework: **MyBatis 3**
- DBMS: **PostgreSQL**

