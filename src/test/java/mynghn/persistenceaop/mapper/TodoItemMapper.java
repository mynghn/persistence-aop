package mynghn.persistenceaop.mapper;

import mynghn.persistenceaop.entity.TodoItem;
import mynghn.persistenceaop.mapper.base.CrudWithHistoryMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TodoItemMapper extends CrudWithHistoryMapper<TodoItem, String> {

    @Override
    default Class<TodoItem> getEntityType() {
        return TodoItem.class;
    }

    @Override
    default Class<String> getEntityIdType() {
        return String.class;
    }
}
