# persistence-aop
Demo project for Spring AOP features (especially within persistence layer)

## Requirements

- For some DB tables, revision history needs to be recorded. (when insert/update occurs) &rarr; [AOP feature 1](#1-aop-advice-for-recording-revision-histories)
  - Each table has its own history table.
  - History tables include full snapshot of original table columns and some additive columns.
    - e.g. sequence numbering of history by each table row

- Some columns are shared across tables and have identical value assigning strategy. &rarr; [AOP feature 2](#2-aop-advice-for-injecting-repeated-data-to-method-arguments)

- Persistence Framework: _MyBatis 3_
- DBMS: _PostgreSQL_

## Features

#### 1. [AOP advice for recording revision histories](./src/main/java/mynghn/persistenceaop/aop/history)
#### 2. [AOP advice for injecting repeated data to method arguments](./src/main/java/mynghn/persistenceaop/aop/injection)

## Examples

Sample application is implemented as a integrated test context.

Go to [test sources root](./src/test) to explore sample application codes and test cases for implemented AOP features.  
