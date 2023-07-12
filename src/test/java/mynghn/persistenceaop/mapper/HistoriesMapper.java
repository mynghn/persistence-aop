package mynghn.persistenceaop.mapper;

import mynghn.persistenceaop.entity.TodoItemHistory;
import mynghn.persistenceaop.entity.TodoListHistory;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface HistoriesMapper {

    int countAllTodoListHistories();

    int countAllTodoItemHistories();

    TodoListHistory getTodoListHistory(String todoListId, int historySequenceNo);

    TodoItemHistory getTodoItemHistory(String todoItemId, int historySequenceNo);
}
