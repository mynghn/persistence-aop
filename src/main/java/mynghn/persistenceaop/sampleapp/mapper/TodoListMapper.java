package mynghn.persistenceaop.sampleapp.mapper;

import mynghn.persistenceaop.sampleapp.entity.TodoList;
import mynghn.persistenceaop.mapper.base.CrudWithHistoryMapper;
import mynghn.persistenceaop.mapper.base.HistoryMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TodoListMapper extends CrudWithHistoryMapper<TodoList, String>, HistoryMapper<TodoList> {

    @Override
    default Class<TodoList> getEntityType() {
        return TodoList.class;
    }
}
