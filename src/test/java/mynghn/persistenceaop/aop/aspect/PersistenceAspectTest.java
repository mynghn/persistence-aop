package mynghn.persistenceaop.aop.aspect;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import mynghn.persistenceaop.dto.TodoItemCreateRequestDto;
import mynghn.persistenceaop.dto.TodoListCreateRequestDto;
import mynghn.persistenceaop.dto.TodoListCreateResponseDto;
import mynghn.persistenceaop.entity.TodoItemHistory;
import mynghn.persistenceaop.entity.TodoListHistory;
import mynghn.persistenceaop.mapper.HistoriesMapper;
import mynghn.persistenceaop.service.TodoListService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase
class PersistenceAspectTest {

    @Autowired
    private TodoListService todoListService;

    @Autowired
    private HistoriesMapper historiesMapper;

    @Test
    @Sql("/test-schema.sql")
    public void recordEntityHistoryAdviceWorksOnInsert() {
        // Arrange
        String testTodoItemTitle1 = "Test Todo Item 1";
        String testTodoItemDescription1 = "This is test todo item 1.";
        LocalDate testTodoItemDueDate1 = LocalDate.of(2023, 7, 30);
        TodoItemCreateRequestDto testTodoItem1Dto = TodoItemCreateRequestDto.builder()
                .title(testTodoItemTitle1).description(testTodoItemDescription1)
                .dueDate(testTodoItemDueDate1).build();

        String testTodoItemTitle2 = "Test Todo Item 2";
        String testTodoItemDescription2 = "This is test todo item 2.";
        LocalDate testTodoItemDueDate2 = LocalDate.of(2023, 8, 30);
        TodoItemCreateRequestDto testTodoItem2Dto = TodoItemCreateRequestDto.builder()
                .title(testTodoItemTitle2).description(testTodoItemDescription2)
                .dueDate(testTodoItemDueDate2).build();

        String testTodoListTitle = "=== Test Todo List ===";
        TodoListCreateRequestDto testTodoListDto = TodoListCreateRequestDto.builder()
                .title(testTodoListTitle).todoItems(List.of(testTodoItem1Dto, testTodoItem2Dto))
                .build();

        // Act
        TodoListCreateResponseDto todoListCreateResponseDto = todoListService.create(
                testTodoListDto);

        // Assert
        assertThat(historiesMapper.countAllTodoListHistories()).isEqualTo(1);

        TodoListHistory actualTodoListHistory = historiesMapper.getTodoListHistory(
                todoListCreateResponseDto.getId(), 0);
        assertThat(actualTodoListHistory.getTodoListTitle()).isEqualTo(testTodoListTitle);

        assertThat(historiesMapper.countAllTodoItemHistories()).isEqualTo(2);

        TodoItemHistory actualTodoItem1History = historiesMapper.getTodoItemHistory(
                todoListCreateResponseDto.getTodoItems().get(0).getId(), 0);
        assertThat(actualTodoItem1History.getTodoItemTitle()).isEqualTo(testTodoItemTitle1);
        assertThat(actualTodoItem1History.getTodoItemDescription()).isEqualTo(
                testTodoItemDescription1);
        assertThat(actualTodoItem1History.getTodoItemDueDate()).isEqualTo(testTodoItemDueDate1);
        assertThat(actualTodoItem1History.getTodoItemTodoListId()).isEqualTo(
                todoListCreateResponseDto.getId());

        TodoItemHistory actualTodoItem2History = historiesMapper.getTodoItemHistory(
                todoListCreateResponseDto.getTodoItems().get(1).getId(), 0);
        assertThat(actualTodoItem2History.getTodoItemTitle()).isEqualTo(testTodoItemTitle2);
        assertThat(actualTodoItem2History.getTodoItemDescription()).isEqualTo(
                testTodoItemDescription2);
        assertThat(actualTodoItem2History.getTodoItemDueDate()).isEqualTo(testTodoItemDueDate2);
        assertThat(actualTodoItem2History.getTodoItemTodoListId()).isEqualTo(
                todoListCreateResponseDto.getId());
    }

    @Test
    @Sql({"/test-schema.sql", "/test-data.sql"})
    public void recordEntityHistoryAdviceWorksOnUpdate() {
        // Arrange
        String todoListId = "230712-000";
        String newTitle = "Test Todo List Title Updated";
        TodoListCreateRequestDto updatePayload = TodoListCreateRequestDto.builder().title(newTitle)
                .build();

        // Act
        todoListService.update(todoListId, updatePayload);

        // Assert
        assertThat(historiesMapper.countAllTodoListHistories()).isEqualTo(1);

        TodoListHistory actualHistory = historiesMapper.getTodoListHistory(todoListId, 0);
        assertThat(actualHistory.getTodoListTitle()).isEqualTo(newTitle);
    }
}
