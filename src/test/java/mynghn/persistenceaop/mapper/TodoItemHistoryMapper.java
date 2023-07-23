package mynghn.persistenceaop.mapper;

import mynghn.persistenceaop.entity.TodoItem;
import mynghn.persistenceaop.mapper.base.HistoryMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TodoItemHistoryMapper extends HistoryMapper<TodoItem> {
}
