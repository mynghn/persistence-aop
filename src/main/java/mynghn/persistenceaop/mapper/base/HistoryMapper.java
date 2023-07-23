package mynghn.persistenceaop.mapper.base;

import java.util.List;

public interface HistoryMapper<E> {

    void recordHistory(E entity);

    void recordHistories(List<E> entities);
}
