package mynghn.persistenceaop.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import mynghn.persistenceaop.entity.base.EntityBase;

@Getter
public class TodoItem extends EntityBase {

    private String id;

    private final String title;

    private final String description;

    private final LocalDate dueDate;

    private final String todoListId;

    @Builder
    public TodoItem(
            String id, String title, String description, LocalDate dueDate, String todoListId,
            Boolean isDeleted,
            LocalDateTime createdAt, String createdBy,
            LocalDateTime lastModifiedAt, String lastModifiedBy
    ) {
        super(isDeleted, createdAt, createdBy, lastModifiedAt, lastModifiedBy);
        this.id = id;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.todoListId = todoListId;
    }

    public void setId(String id) {
        this.id = id;
    }
}
