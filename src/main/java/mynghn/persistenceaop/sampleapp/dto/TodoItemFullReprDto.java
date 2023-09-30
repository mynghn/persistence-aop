package mynghn.persistenceaop.sampleapp.dto;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record TodoItemFullReprDto(
        LocalDateTime createdAt,
        String id,
        String title,
        CommonCodeDto type) {

}
