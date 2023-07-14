package mynghn.persistenceaop.entity;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TodoList {

    private String id;

    private String title;

    public void setId(String id) {
        this.id = id;
    }
}