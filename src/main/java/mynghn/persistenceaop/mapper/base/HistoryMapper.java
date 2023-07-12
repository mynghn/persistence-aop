package mynghn.persistenceaop.mapper.base;

import mynghn.persistenceaop.entity.base.Entity;

public interface HistoryMapper<E extends Entity<ID>, ID> {

    int recordHistory(ID entityId);
}
