package mynghn.persistenceaop.entity;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import mynghn.persistenceaop.entity.base.EntityBase;

@Getter
public class TodoList extends EntityBase {

    private String id;

    private final String title;

    @Builder
    public TodoList(
            String id, String title,
            Boolean isDeleted,
            LocalDateTime createdAt, String createdBy,
            LocalDateTime lastModifiedAt, String lastModifiedBy
    ) {
        super(isDeleted, createdAt, createdBy, lastModifiedAt, lastModifiedBy);
        this.id = id;
        this.title = title;
    }

    public void setId(String id) {
        this.id = id;
    }
}
