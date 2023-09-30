package mynghn.persistenceaop.sampleapp.service.aggregator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mynghn.persistenceaop.sampleapp.dto.CommonCodeDto;
import mynghn.persistenceaop.sampleapp.entity.CommonCode;
import mynghn.persistenceaop.sampleapp.mapper.CommonCodeGroupMapper;
import mynghn.persistenceaop.sampleapp.service.converter.CommonCodeGroupConverter;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommonCodeAggregator {

    private final CommonCodeGroupMapper commonCodeGroupMapper;

    public CommonCodeDto aggregateToDto(CommonCode entity) {
        CommonCodeDto dto = CommonCodeDto.builder()
                .createdAt(entity.getCreatedAt())
                .id(entity.getValue())
                .name(entity.getName())
                .group(CommonCodeGroupConverter.toDto(
                        commonCodeGroupMapper.select(entity.getGroupId())))
                .build();

        log.debug("Common code DTO aggregated: {}", dto);

        return dto;
    }

}
