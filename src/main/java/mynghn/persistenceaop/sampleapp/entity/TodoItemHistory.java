package mynghn.persistenceaop.sampleapp.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    private Boolean isDeleted;

    private LocalDateTime createdAt;
    private String createdBy;

    private LocalDateTime lastModifiedAt;
    private String lastModifiedBy;
}
