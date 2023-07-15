package mynghn.persistenceaop.mapper.base;

import java.util.List;
import mynghn.persistenceaop.aop.auditing.annotation.Audit;
import mynghn.persistenceaop.aop.auditing.annotation.InjectStamp;
import mynghn.persistenceaop.entity.base.CreateStamp;
import mynghn.persistenceaop.entity.base.SoftDeleteEntity;
import mynghn.persistenceaop.entity.base.UpdateStamp;
import org.apache.ibatis.annotations.Param;

public interface CrudMapper<E, ID> extends EntityMapper<E, ID> {

    @Audit
    ID insert(@InjectStamp(stampTypes={CreateStamp.class, UpdateStamp.class, SoftDeleteEntity.class}) E payload);

    E select(ID id);

    <S> List<E> selectAll(S specification);

    @Audit
    <P> ID update(
            @Param("id") ID id,
            @Param("payload") @InjectStamp(UpdateStamp.class) P payload
    );

    @Audit
    <P, S> List<ID> updateAll(
            @Param("specification") S specification,
            @Param("payload") @InjectStamp(UpdateStamp.class) P payload
    );

    int delete(ID id);

    <S> int deleteAll(S specification);
}
