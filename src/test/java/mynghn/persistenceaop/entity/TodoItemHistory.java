package mynghn.persistenceaop.entity;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TodoItemHistory {

    private String todoItemId;

    private Integer historySequenceNo;

    private String todoItemTitle;

    private String todoItemDescription;

    private LocalDate todoItemDueDate;

    private String todoItemTodoListId;
}
