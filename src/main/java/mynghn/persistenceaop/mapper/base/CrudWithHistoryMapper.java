package mynghn.persistenceaop.mapper.base;

import java.util.List;
import mynghn.persistenceaop.aop.injection.annotation.InjectStamp;
import mynghn.persistenceaop.aop.injection.annotation.Injected;
import mynghn.persistenceaop.aop.history.annotation.RecordHistory;
import mynghn.persistenceaop.entity.base.CreateStamp;
import mynghn.persistenceaop.entity.base.SoftDeleteEntity;
import mynghn.persistenceaop.entity.base.UpdateStamp;
import org.apache.ibatis.annotations.Param;

public interface CrudWithHistoryMapper<E, ID> extends CrudMapper<E, ID> {

    @Override
    @InjectStamp
    @RecordHistory
    E insert(@Injected({CreateStamp.class, UpdateStamp.class, SoftDeleteEntity.class}) E payload);

    @Override
    @InjectStamp
    @RecordHistory
    <P> E update(
            @Param("id") ID id,
            @Param("payload") @Injected(UpdateStamp.class) P payload
    );

    @Override
    @InjectStamp
    @RecordHistory(many = true)
    <P, S> List<E> updateAll(
            @Param("specification") S specification,
            @Param("payload") @Injected(UpdateStamp.class) P payload
    );
}
