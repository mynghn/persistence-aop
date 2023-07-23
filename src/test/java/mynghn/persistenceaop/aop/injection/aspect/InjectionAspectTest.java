package mynghn.persistenceaop.aop.injection.aspect;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import mynghn.persistenceaop.entity.TodoList;
import mynghn.persistenceaop.entity.TodoListSpec;
import mynghn.persistenceaop.mapper.TodoListMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest
@ActiveProfiles("test")
class InjectionAspectTest {

    @Autowired
    private TodoListMapper todoListMapper;

    @Test
    @Sql("/test-schema.sql")
    public void allCreateUpdateSoftDeleteStampsInjectedBeforeInsert() {
        // Arrange
        TodoList testPayload = TodoList.builder().title("Test TodoList").build();

        // Act
        TodoList todoListInserted = todoListMapper.insert(testPayload);

        // Assert
        assertThat(testPayload.getIsDeleted()).isFalse();

        LocalDateTime timestampInjected = testPayload.getCreatedAt();
        assertThat(timestampInjected).isNotNull();
        assertThat(testPayload.getLastModifiedAt()).isEqualTo(timestampInjected);

        String usernameInjected = testPayload.getCreatedBy();
        assertThat(usernameInjected).isNotNull();
        assertThat(testPayload.getLastModifiedBy()).isEqualTo(usernameInjected);

        assertThat(todoListInserted.getIsDeleted()).isEqualTo(testPayload.getIsDeleted());
        assertThat(todoListInserted.getCreatedAt()).isEqualTo(testPayload.getCreatedAt());
        assertThat(todoListInserted.getCreatedBy()).isEqualTo(testPayload.getCreatedBy());
        assertThat(todoListInserted.getLastModifiedAt()).isEqualTo(testPayload.getLastModifiedAt());
        assertThat(todoListInserted.getLastModifiedBy()).isEqualTo(testPayload.getLastModifiedBy());
    }

    @Test
    @Sql({"/test-schema.sql", "/test-data.sql"})
    public void onlyUpdateStampInjectedBeforeUpdate() {
        // Arrange
        String testTodoListId = "230712-000";
        TodoList testPayload = TodoList.builder().title("Test TodoList Updated").build();

        // Act
        todoListMapper.update(testTodoListId, testPayload);

        // Assert
        assertThat(testPayload.getIsDeleted()).isNull();

        assertThat(testPayload.getCreatedAt()).isNull();
        assertThat(testPayload.getCreatedBy()).isNull();

        assertThat(testPayload.getLastModifiedAt()).isNotNull();
        assertThat(testPayload.getLastModifiedBy()).isNotNull();
        TodoList todoListSelected = todoListMapper.select(testTodoListId);
        assertThat(todoListSelected.getLastModifiedAt()).isEqualTo(testPayload.getLastModifiedAt());
        assertThat(todoListSelected.getLastModifiedBy()).isEqualTo(testPayload.getLastModifiedBy());
    }

    @Test
    @Sql({"/test-schema.sql", "/test-data.sql"})
    public void onlyUpdateStampInjectedBeforeUpdateAll() {
        // Arrange
        String titleLikeQ = "Test%";
        TodoListSpec filter = TodoListSpec.builder().titleLike(titleLikeQ).build();
        TodoList testPayload = TodoList.builder().title("Test TodoList Updated").build();

        // Act
        List<TodoList> todoListsUpdated = todoListMapper.updateAll(filter, testPayload);

        // Assert
        assertThat(testPayload.getIsDeleted()).isNull();

        assertThat(testPayload.getCreatedAt()).isNull();
        assertThat(testPayload.getCreatedBy()).isNull();

        assertThat(testPayload.getLastModifiedAt()).isNotNull();
        assertThat(testPayload.getLastModifiedBy()).isNotNull();
        todoListsUpdated.forEach(todoListUpdated -> {
            assertThat(todoListUpdated.getLastModifiedAt()).isEqualTo(testPayload.getLastModifiedAt());
            assertThat(todoListUpdated.getLastModifiedBy()).isEqualTo(testPayload.getLastModifiedBy());
        });
    }
}
