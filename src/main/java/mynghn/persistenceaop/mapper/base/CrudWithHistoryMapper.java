package mynghn.persistenceaop.mapper.base;

import java.util.List;
import mynghn.persistenceaop.aop.annotations.RecordHistory;
import org.apache.ibatis.annotations.Param;

public interface CrudWithHistoryMapper<E, ID> extends CrudMapper<E, ID> {

    @Override
    @RecordHistory
    ID insert(E payload);

    @Override
    @RecordHistory
    <P> ID update(@Param("id") ID id, @Param("payload") P payload);

    @Override
    @RecordHistory(many = true)
    <P, S> List<ID> updateAll(@Param("specification") S specification, @Param("payload") P payload);
}
