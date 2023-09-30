package mynghn.persistenceaop.sampleapp.mapper;

import mynghn.persistenceaop.mapper.base.CrudWithHistoryMapper;
import mynghn.persistenceaop.mapper.base.HistoryMapper;
import mynghn.persistenceaop.sampleapp.entity.TodoItem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TodoItemMapper extends CrudWithHistoryMapper<TodoItem, String>,
        HistoryMapper<TodoItem> {

    @Override
    default Class<TodoItem> getEntityType() {
        return TodoItem.class;
    }
}
