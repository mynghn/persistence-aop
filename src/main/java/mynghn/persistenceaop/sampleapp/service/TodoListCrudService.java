package mynghn.persistenceaop.sampleapp.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mynghn.persistenceaop.aop.context.annotation.UseExecutionScopeContext;
import mynghn.persistenceaop.aop.context.contexts.CommonCodesCache;
import mynghn.persistenceaop.sampleapp.dto.TodoListFullReprDto;
import mynghn.persistenceaop.sampleapp.entity.TodoList;
import mynghn.persistenceaop.sampleapp.entity.specification.TodoListSpec;
import mynghn.persistenceaop.sampleapp.mapper.TodoListMapper;
import mynghn.persistenceaop.sampleapp.service.aggregator.TodoListAggregator;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TodoListCrudService {

    private final TodoListAggregator todoListAggregator;

    private final TodoListMapper todoListMapper;

    @UseExecutionScopeContext(CommonCodesCache.class)
    public List<TodoListFullReprDto> list() {
        List<TodoList> todoListsFetched = todoListMapper.selectAll(TodoListSpec.empty());

        log.debug("Todo list fetched from database: {}", todoListsFetched);

        return todoListsFetched.stream()
                .map(todoListAggregator::aggregateToDto)
                .toList();
    }
}
