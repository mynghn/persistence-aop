package mynghn.persistenceaop.mapper.base;

import java.util.List;
import mynghn.persistenceaop.aop.injection.annotation.InjectStamp;
import mynghn.persistenceaop.aop.injection.annotation.Injected;
import mynghn.persistenceaop.entity.base.CreateStamp;
import mynghn.persistenceaop.entity.base.SoftDeleteEntity;
import mynghn.persistenceaop.entity.base.UpdateStamp;
import org.apache.ibatis.annotations.Param;

public interface CrudMapper<E, ID> extends EntityMapper<E, ID> {

    @InjectStamp
    ID insert(@Injected({CreateStamp.class, UpdateStamp.class, SoftDeleteEntity.class}) E payload);

    E select(ID id);

    <S> List<E> selectAll(S specification);

    @InjectStamp
    <P> ID update(
            @Param("id") ID id,
            @Param("payload") @Injected(UpdateStamp.class) P payload
    );

    @InjectStamp
    <P, S> List<ID> updateAll(
            @Param("specification") S specification,
            @Param("payload") @Injected(UpdateStamp.class) P payload
    );

    int delete(ID id);

    <S> int deleteAll(S specification);
}
