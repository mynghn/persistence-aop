package mynghn.persistenceaop.mapper.base;

import java.util.List;
import mynghn.persistenceaop.aop.auditing.annotation.Audit;
import mynghn.persistenceaop.aop.auditing.annotation.InjectStamp;
import mynghn.persistenceaop.aop.history.annotation.RecordHistory;
import mynghn.persistenceaop.entity.base.CreateStamp;
import mynghn.persistenceaop.entity.base.SoftDeleteEntity;
import mynghn.persistenceaop.entity.base.UpdateStamp;
import org.apache.ibatis.annotations.Param;

public interface CrudWithHistoryMapper<E, ID> extends CrudMapper<E, ID> {

    @Override
    @Audit
    @RecordHistory
    ID insert(@InjectStamp(stampTypes={CreateStamp.class, UpdateStamp.class, SoftDeleteEntity.class}) E payload);

    @Override
    @Audit
    @RecordHistory
    <P> ID update(
            @Param("id") ID id,
            @Param("payload") @InjectStamp(UpdateStamp.class) P payload
    );

    @Override
    @Audit
    @RecordHistory(many = true)
    <P, S> List<ID> updateAll(
            @Param("specification") S specification,
            @Param("payload") @InjectStamp(UpdateStamp.class) P payload
    );
}
