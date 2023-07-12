package mynghn.persistenceaop.dto;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TodoItemCreateRequestDto {

    private String title;

    private String description;

    private LocalDate dueDate;
}
