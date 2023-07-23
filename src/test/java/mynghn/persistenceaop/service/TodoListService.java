package mynghn.persistenceaop.service;

import lombok.RequiredArgsConstructor;
import mynghn.persistenceaop.dto.TodoItemCreateRequestDto;
import mynghn.persistenceaop.dto.TodoItemCreateResponseDto;
import mynghn.persistenceaop.dto.TodoListCreateRequestDto;
import mynghn.persistenceaop.dto.TodoListCreateResponseDto;
import mynghn.persistenceaop.entity.TodoItem;
import mynghn.persistenceaop.entity.TodoList;
import mynghn.persistenceaop.mapper.TodoItemMapper;
import mynghn.persistenceaop.mapper.TodoListMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TodoListService {

    private final TodoListMapper todoListMapper;
    private final TodoItemMapper todoItemMapper;

    @Transactional
    public TodoListCreateResponseDto create(TodoListCreateRequestDto requestDto) {
        TodoList insertVo = fromRequest(requestDto);
        TodoList entityInserted = todoListMapper.insert(insertVo);

        return new TodoListCreateResponseDto(
                entityInserted.getId(),
                requestDto.getTodoItems().stream().map(todoItemDto -> {
                    TodoItem todoItemInsertVo = fromRequest(entityInserted.getId(), todoItemDto);
                    TodoItem todoItemInserted = todoItemMapper.insert(todoItemInsertVo);
                    return new TodoItemCreateResponseDto(todoItemInserted.getId());
                }).toList()
        );
    }

    @Transactional
    public void update(String todoListId, TodoListCreateRequestDto requestDto) {
        todoListMapper.update(todoListId, fromRequest(requestDto));
    }

    private TodoList fromRequest(TodoListCreateRequestDto requestDto) {
        return TodoList.builder().title(requestDto.getTitle()).build();
    }

    private TodoItem fromRequest(String todoListId, TodoItemCreateRequestDto requestDto) {
        return TodoItem.builder().todoListId(todoListId).title(requestDto.getTitle())
                .description(requestDto.getDescription()).dueDate(requestDto.getDueDate()).build();
    }
}
