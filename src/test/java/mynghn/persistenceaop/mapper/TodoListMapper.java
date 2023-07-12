package mynghn.persistenceaop.mapper;

import mynghn.persistenceaop.entity.TodoList;
import mynghn.persistenceaop.mapper.base.CrudWithHistoryMapper;
import mynghn.persistenceaop.mapper.base.HistoryMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TodoListMapper extends CrudWithHistoryMapper<TodoList, String>, HistoryMapper<TodoList, String> {

    @Override
    default Class<TodoList> getEntityType() {
        return TodoList.class;
    }

    @Override
    default Class<String> getEntityIdType() {
        return String.class;
    }
}
