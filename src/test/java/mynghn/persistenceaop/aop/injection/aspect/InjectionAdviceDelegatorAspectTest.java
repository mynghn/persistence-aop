package mynghn.persistenceaop.aop.injection.aspect;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import mynghn.persistenceaop.aop.context.aspect.RequestSessionProviderAspect;
import mynghn.persistenceaop.aop.context.contexts.RequestSession;
import mynghn.persistenceaop.sampleapp.entity.TodoList;
import mynghn.persistenceaop.sampleapp.mapper.TodoListMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

class InjectionAdviceDelegatorAspectTest {

    private final TodoListMapper mockMapper = mock(TodoListMapper.class);
    private final AspectJProxyFactory mockMapperAspectJProxyFactory = new AspectJProxyFactory(
            mockMapper);

    private final RequestSession testSession = RequestSession.builder()
            .username("Test User")
            .time(LocalDateTime.now())
            .build();

    @BeforeEach
    void setup() {
        // Stub RequestSessionProviderAspect
        RequestSessionProviderAspect sessionProviderAspectSpy = spy(
                RequestSessionProviderAspect.class);
        when(sessionProviderAspectSpy.buildContext()).thenReturn(testSession);

        // Register AOP aspects
        AspectJProxyFactory adviceCoreProxyFactory = new AspectJProxyFactory(
                new InjectionAdviceCore(sessionProviderAspectSpy));
        adviceCoreProxyFactory.addAspect(sessionProviderAspectSpy);

        mockMapperAspectJProxyFactory.addAspect(new InjectionAdviceDelegatorAspect(
                adviceCoreProxyFactory.getProxy()));
    }

    @Test
    void createStampUpdateStampAndSoftDeleteStampInjectedBeforeInsert() {
        // Arrange
        TodoList testInsertVo = TodoList.builder().build();

        // Act
        getAopTargetedMapper().insert(testInsertVo);

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
        getAopTargetedMapper().update("Testing...", testUpdateVo);

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
        aopTargetedMapper.updateAll("Testing...", testUpdateVo);

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

    private TodoListMapper getAopTargetedMapper() {
        return mockMapperAspectJProxyFactory.getProxy();
    }
}
