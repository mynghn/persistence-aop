package mynghn.persistenceaop.sampleapp.mapper;

import mynghn.persistenceaop.sampleapp.entity.TodoItem;
import mynghn.persistenceaop.mapper.base.HistoryMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TodoItemHistoryMapper extends HistoryMapper<TodoItem> {
}
