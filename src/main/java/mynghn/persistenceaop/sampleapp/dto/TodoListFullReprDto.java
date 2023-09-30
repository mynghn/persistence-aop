package mynghn.persistenceaop.sampleapp.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record TodoListFullReprDto(
        LocalDateTime createdAt,
        String id,
        String title,
        CommonCodeDto type,
        List<TodoItemFullReprDto> items) {

}
