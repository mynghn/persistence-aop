package mynghn.persistenceaop.entity;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import mynghn.persistenceaop.entity.base.Entity;

@Getter
@Builder
public class TodoItem implements Entity<String> {

    private String todoListId;

    private String id;

    private String title;

    private String description;

    private LocalDate dueDate;

    public void setId(String id) {
        this.id = id;
    }
}
