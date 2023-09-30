package mynghn.persistenceaop.sampleapp.service.converter;

import lombok.extern.slf4j.Slf4j;
import mynghn.persistenceaop.sampleapp.dto.CommonCodeGroupDto;
import mynghn.persistenceaop.sampleapp.entity.CommonCodeGroup;

@Slf4j
public class CommonCodeGroupConverter {
    public static CommonCodeGroupDto toDto(CommonCodeGroup entity) {
        CommonCodeGroupDto dto = CommonCodeGroupDto.builder()
                .createdAt(entity.getCreatedAt())
                .id(entity.getId())
                .name(entity.getName())
                .build();

        log.debug("Common code group DTO converted: {}", dto);

        return dto;
    }
}
