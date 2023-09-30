package mynghn.persistenceaop.sampleapp.service.aggregator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mynghn.persistenceaop.sampleapp.dto.TodoItemFullReprDto;
import mynghn.persistenceaop.sampleapp.entity.TodoItem;
import mynghn.persistenceaop.sampleapp.enums.CommonCodeGroup;
import mynghn.persistenceaop.sampleapp.service.reader.CommonCodeReader;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TodoItemAggregator {

    private final CommonCodeReader commonCodeReader;

    private final CommonCodeAggregator commonCodeAggregator;

    public TodoItemFullReprDto aggregateToDto(TodoItem entity) {
        TodoItemFullReprDto dto = TodoItemFullReprDto.builder()
                .createdAt(entity.getCreatedAt())
                .id(entity.getId())
                .title(entity.getTitle())
                .type(commonCodeAggregator.aggregateToDto(commonCodeReader.get(
                        CommonCodeGroup.TODO_ITEM_TYPE,
                        entity.getTypeCodeValue())))
                .build();

        log.debug("Todo item DTO aggregated: {}", dto);

        return dto;
    }
}
