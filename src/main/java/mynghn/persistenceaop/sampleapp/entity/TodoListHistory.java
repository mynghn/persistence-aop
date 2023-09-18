package mynghn.persistenceaop.sampleapp.entity;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TodoListHistory {

    private String todoListId;

    private Integer historySequenceNo;

    private String todoListTitle;

    private Boolean isDeleted;

    private LocalDateTime createdAt;
    private String createdBy;

    private LocalDateTime lastModifiedAt;
    private String lastModifiedBy;
}
