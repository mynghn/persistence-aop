package mynghn.persistenceaop.mapper.base;

import java.util.List;

public interface HistoryMapper<E> {

    int recordHistory(E entity);

    int recordHistories(List<E> entities);
}
