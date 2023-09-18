package mynghn.persistenceaop.sampleapp.entity;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TodoListSpec {

    private String id;

    private String titleLike;
}
