package mynghn.persistenceaop.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TodoListCreateRequestDto {

    private String title;

    private List<TodoItemCreateRequestDto> todoItems;
}
