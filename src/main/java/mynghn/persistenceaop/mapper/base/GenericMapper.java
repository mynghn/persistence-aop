package mynghn.persistenceaop.mapper.base;

public interface GenericMapper<E, ID> {

    Class<E> getEntityType();

    Class<ID> getEntityIdType();
}
