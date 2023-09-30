package mynghn.persistenceaop.sampleapp.service.aggregator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mynghn.persistenceaop.aop.context.annotation.UseExecutionScopeContext;
import mynghn.persistenceaop.aop.context.contexts.CommonCodesCache;
import mynghn.persistenceaop.sampleapp.dto.TodoListFullReprDto;
import mynghn.persistenceaop.sampleapp.entity.TodoList;
import mynghn.persistenceaop.sampleapp.entity.specification.TodoItemSpec;
import mynghn.persistenceaop.sampleapp.enums.CommonCodeGroup;
import mynghn.persistenceaop.sampleapp.mapper.TodoItemMapper;
import mynghn.persistenceaop.sampleapp.service.reader.CommonCodeReader;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TodoListAggregator {

    private final CommonCodeReader commonCodeReader;

    private final CommonCodeAggregator commonCodeAggregator;
    private final TodoItemAggregator todoItemAggregator;

    private final TodoItemMapper todoItemMapper;


    @UseExecutionScopeContext(CommonCodesCache.class)
    public TodoListFullReprDto aggregateToDto(TodoList entity) {
        TodoListFullReprDto dto = TodoListFullReprDto.builder()
                .createdAt(entity.getCreatedAt())
                .id(entity.getId())
                .title(entity.getTitle())
                .type(commonCodeAggregator.aggregateToDto(commonCodeReader.get(
                        CommonCodeGroup.TODO_LIST_TYPE,
                        entity.getTypeCodeValue())))
                .items(todoItemMapper.selectAll(TodoItemSpec.builder()
                                .todoListIdEq(entity.getId())
                                .build()).stream()
                        .map(todoItemAggregator::aggregateToDto)
                        .toList())
                .build();

        log.debug("Todo list DTO aggregated: {}", dto);

        return dto;
    }
}
