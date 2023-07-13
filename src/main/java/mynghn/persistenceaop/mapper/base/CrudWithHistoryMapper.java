package mynghn.persistenceaop.mapper.base;

import mynghn.persistenceaop.aop.annotations.Id;
import mynghn.persistenceaop.aop.annotations.Payload;
import mynghn.persistenceaop.aop.annotations.RecordHistory;
import mynghn.persistenceaop.aop.annotations.Specification;
import mynghn.persistenceaop.entity.base.Entity;
import org.apache.ibatis.annotations.Param;

public interface CrudWithHistoryMapper<E extends Entity<ID>, ID> extends CrudMapper<E, ID> {

    @Override
    @RecordHistory
    <P extends E> int insert(@Payload P payload);

    @Override
    @RecordHistory
    <P> int update(@Param("entityId") @Id ID entityId, @Param("payload") @Payload P payload);

    @Override
    @RecordHistory(many = true)
    <P, S> int updateAll(@Param("spec") @Specification S spec, @Param("payload") @Payload P payload);
}
