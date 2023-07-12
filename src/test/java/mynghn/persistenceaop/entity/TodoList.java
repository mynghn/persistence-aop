package mynghn.persistenceaop.entity;

import lombok.Builder;
import lombok.Getter;
import mynghn.persistenceaop.entity.base.Entity;

@Getter
@Builder
public class TodoList implements Entity<String> {

    private String id;

    private String title;

    public void setId(String id) {
        this.id = id;
    }
}
