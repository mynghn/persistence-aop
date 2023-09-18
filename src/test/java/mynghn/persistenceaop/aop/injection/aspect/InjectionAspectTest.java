package mynghn.persistenceaop.aop.injection.aspect;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import mynghn.persistenceaop.aop.injection.session.AdviceSession;
import mynghn.persistenceaop.aop.injection.session.AdviceSessionBuilder;
import mynghn.persistenceaop.sampleapp.entity.TodoList;
import mynghn.persistenceaop.sampleapp.mapper.TodoListMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

class InjectionAspectTest {

    private final TodoListMapper mockMapper = mock(TodoListMapper.class);
    private final AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(mockMapper);
    private final InjectionAspect sut = new InjectionAspect();

    private final AdviceSession testSession = AdviceSession.builder()
            .username("Test User")
            .time(LocalDateTime.now())
            .build();

    @BeforeEach
    void setup() {
        // Register InjectionAspect
        aspectJProxyFactory.addAspect(sut);
    }

    @Test
    void createStampUpdateStampAndSoftDeleteStampInjectedBeforeInsert() {
        // Arrange
        TodoList testInsertVo = TodoList.builder().build();

        // Act
        try (MockedStatic<AdviceSessionBuilder> ignored = stubAdviceSessionBuilder()) {
            getAopTargetedMapper().insert(testInsertVo);
        }

        // Assert
        ArgumentCaptor<TodoList> argumentCaptor = ArgumentCaptor.forClass(TodoList.class);
        verify(mockMapper).insert(argumentCaptor.capture());
        TodoList actualInsertVo = argumentCaptor.getValue();

        assertThat(actualInsertVo).isEqualTo(testInsertVo);
        assertThat(actualInsertVo.getIsDeleted().booleanValue()).isFalse();
        assertThat(actualInsertVo.getCreatedBy()).isEqualTo(testSession.username());
        assertThat(actualInsertVo.getCreatedAt()).isEqualTo(testSession.time());
        assertThat(actualInsertVo.getLastModifiedBy()).isEqualTo(testSession.username());
        assertThat(actualInsertVo.getLastModifiedAt()).isEqualTo(testSession.time());
    }

    @Test
    void onlyUpdateStampInjectedBeforeUpdate() {
        // Arrange
        TodoList testUpdateVo = TodoList.builder().build();

        // Act
        try (MockedStatic<AdviceSessionBuilder> ignored = stubAdviceSessionBuilder()) {
            getAopTargetedMapper().update("Testing...", testUpdateVo);
        }

        // Assert
        ArgumentCaptor<TodoList> argumentCaptor = ArgumentCaptor.forClass(TodoList.class);
        verify(mockMapper).update(any(), argumentCaptor.capture());
        TodoList actualUpdateVo = argumentCaptor.getValue();

        assertThat(actualUpdateVo).isEqualTo(testUpdateVo);
        assertThat(actualUpdateVo.getIsDeleted()).isNull();
        assertThat(actualUpdateVo.getCreatedBy()).isNull();
        assertThat(actualUpdateVo.getCreatedAt()).isNull();
        assertThat(actualUpdateVo.getLastModifiedBy()).isEqualTo(testSession.username());
        assertThat(actualUpdateVo.getLastModifiedAt()).isEqualTo(testSession.time());
    }

    @Test
    void onlyUpdateStampInjectedBeforeUpdateAll() {
        // Arrange
        TodoList testUpdateVo = TodoList.builder().build();
        TodoListMapper aopTargetedMapper = getAopTargetedMapper();

        // Act
        try (MockedStatic<AdviceSessionBuilder> ignored = stubAdviceSessionBuilder()) {
            aopTargetedMapper.updateAll("Testing...", testUpdateVo);
        }

        // Assert
        ArgumentCaptor<TodoList> argumentCaptor = ArgumentCaptor.forClass(TodoList.class);
        verify(mockMapper).updateAll(any(), argumentCaptor.capture());
        TodoList actualUpdateVo = argumentCaptor.getValue();

        assertThat(actualUpdateVo).isEqualTo(testUpdateVo);
        assertThat(actualUpdateVo.getIsDeleted()).isNull();
        assertThat(actualUpdateVo.getCreatedBy()).isNull();
        assertThat(actualUpdateVo.getCreatedAt()).isNull();
        assertThat(actualUpdateVo.getLastModifiedBy()).isEqualTo(testSession.username());
        assertThat(actualUpdateVo.getLastModifiedAt()).isEqualTo(testSession.time());
    }

    private MockedStatic<AdviceSessionBuilder> stubAdviceSessionBuilder() {
        MockedStatic<AdviceSessionBuilder> mocked = mockStatic(
                AdviceSessionBuilder.class);

        mocked.when(AdviceSessionBuilder::newSession).thenReturn(testSession);

        return mocked;
    }

    private TodoListMapper getAopTargetedMapper() {
        return aspectJProxyFactory.getProxy();
    }
}
