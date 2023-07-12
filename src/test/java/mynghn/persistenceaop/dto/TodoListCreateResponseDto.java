package mynghn.persistenceaop.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TodoListCreateResponseDto {

    private String id;

    private List<TodoItemCreateResponseDto> todoItems;
}
