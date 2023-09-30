package mynghn.persistenceaop.sampleapp.dto;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record CommonCodeDto(
        LocalDateTime createdAt,
        String id,
        String name,
        CommonCodeGroupDto group) {

}
