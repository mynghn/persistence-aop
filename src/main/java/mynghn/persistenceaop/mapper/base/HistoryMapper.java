package mynghn.persistenceaop.mapper.base;

import java.util.List;

public interface HistoryMapper<E, ID> {

    int recordHistory(ID entityId);

    int recordHistories(List<ID> entityIds);
}
