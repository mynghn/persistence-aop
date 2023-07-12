package mynghn.persistenceaop.entity;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TodoListHistory {

    private String todoListId;

    private Integer historySequenceNo;

    private String todoListTitle;
}
