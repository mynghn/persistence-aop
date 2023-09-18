package mynghn.persistenceaop.sampleapp.mapper;

import mynghn.persistenceaop.sampleapp.entity.TodoItemHistory;
import mynghn.persistenceaop.sampleapp.entity.TodoListHistory;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface HistoriesMapper {

    int countAllTodoListHistories();

    int countAllTodoItemHistories();

    TodoListHistory getTodoListHistory(String todoListId, int historySequenceNo);

    TodoItemHistory getTodoItemHistory(String todoItemId, int historySequenceNo);
}
