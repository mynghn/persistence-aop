package mynghn.persistenceaop.mapper.base;

import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface CrudMapper<E, ID> extends GenericMapper<E, ID> {

    ID insert(E payload);

    E select(ID id);

    <S> List<E> selectAll(S specification);

    <P> ID update(@Param("id") ID id, @Param("payload") P payload);

    <P, S> List<ID> updateAll(@Param("specification") S specification, @Param("payload") P payload);

    int delete(ID id);

    <S> int deleteAll(S specification);
}
