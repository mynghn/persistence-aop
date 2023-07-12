package mynghn.persistenceaop.mapper.base;

import java.util.List;
import mynghn.persistenceaop.aop.annotations.Id;
import mynghn.persistenceaop.aop.annotations.Payload;
import mynghn.persistenceaop.aop.annotations.Specification;
import mynghn.persistenceaop.entity.base.Entity;
import org.apache.ibatis.annotations.Param;

public interface CrudMapper<E extends Entity<ID>, ID> extends GenericMapper<E, ID> {

    <P extends E> int insert(@Payload P payload);

    E select(@Id ID entityId);

    <S> List<E> selectAll(@Specification S spec);

    <P> int update(@Param("entityId") @Id ID entityId, @Param("payload") @Payload P payload);

    <P, S> int updateAll(@Param("spec") @Specification S spec, @Param("payload") @Payload P payload);

    int delete(@Id ID entityId);
    <S> int deleteAll(@Specification S spec);
}
