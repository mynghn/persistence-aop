package mynghn.persistenceaop.mapper.base;

public interface EntityMapper<E, ID> {

    Class<E> getEntityType();

    Class<ID> getEntityIdType();
}
