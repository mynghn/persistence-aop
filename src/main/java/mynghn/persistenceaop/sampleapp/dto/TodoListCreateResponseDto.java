package mynghn.persistenceaop.sampleapp.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TodoListCreateResponseDto {

    private String id;

    private List<TodoItemCreateResponseDto> todoItems;
}
