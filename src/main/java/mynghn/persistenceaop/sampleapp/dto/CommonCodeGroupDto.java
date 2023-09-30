package mynghn.persistenceaop.sampleapp.dto;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record CommonCodeGroupDto(
        LocalDateTime createdAt,
        String id,
        String name) {

}
