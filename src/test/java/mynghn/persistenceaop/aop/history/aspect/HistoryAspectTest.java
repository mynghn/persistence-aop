package mynghn.persistenceaop.aop.history.aspect;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import mynghn.persistenceaop.mapper.base.HistoryMapper;
import mynghn.persistenceaop.sampleapp.entity.TodoList;
import mynghn.persistenceaop.sampleapp.mapper.TodoListMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;

class HistoryAspectTest {

    private final ApplicationContext mockApplicationContext = mock(ApplicationContext.class);

    private final TodoListMapper mockMapper = mock(TodoListMapper.class);

    private final AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(mockMapper);

    private final HistoryAspect sut = new HistoryAspect(mockApplicationContext);

    @BeforeEach
    void setup() {
        // Stub ApplicationContext
        when(mockApplicationContext.getBeanNamesForType(any(ResolvableType.class)))
                .thenReturn(new String[]{"Mocked"});
        when(mockApplicationContext.getBean("Mocked", HistoryMapper.class))
                .thenReturn(mockMapper);

        // Register HistoryAspect
        aspectJProxyFactory.addAspect(sut);
    }

    @Test
    void recordEntityHistoryAdviceWorksOnInsert() {
        /* Arrange */
        // prepare test data
        TodoList testInsertVo = TodoList.builder().build();
        TodoList testInsertReturn = TodoList.builder().build();
        // stub mapper
        when(mockMapper.insert(testInsertVo)).thenReturn(testInsertReturn);

        /* Act */
        TodoListMapper aopTargetMapper = getAopTargetedMapper();
        aopTargetMapper.insert(testInsertVo);

        /* Assert */
        ArgumentCaptor<TodoList> argumentCaptor = ArgumentCaptor.forClass(TodoList.class);
        verify(mockMapper, times(1)).recordHistory(argumentCaptor.capture());
        verify(mockMapper, times(0)).recordHistories(any());
        assertThat(argumentCaptor.getValue()).isEqualTo(testInsertReturn);
    }

    @Test
    public void recordEntityHistoryAdviceWorksOnUpdate() {
        /* Arrange */
        // prepare test data
        String testId = "Testing...";
        TodoList testUpdateVo = TodoList.builder().build();
        TodoList testUpdateReturn = TodoList.builder().build();
        // stub mapper
        when(mockMapper.update(testId, testUpdateVo)).thenReturn(testUpdateReturn);

        /* Act */
        TodoListMapper aopTargetMapper = getAopTargetedMapper();
        aopTargetMapper.update(testId, testUpdateVo);

        /* Assert */
        ArgumentCaptor<TodoList> argumentCaptor = ArgumentCaptor.forClass(TodoList.class);
        verify(mockMapper, times(1)).recordHistory(argumentCaptor.capture());
        verify(mockMapper, times(0)).recordHistories(any());
        assertThat(argumentCaptor.getValue()).isEqualTo(testUpdateReturn);
    }

    @Test
    public void recordMultipleEntityHistoriesAdviceWorksOnUpdateAll() {
        /* Arrange */
        // prepare test data
        TodoList testUpdateVo = TodoList.builder().build();
        TodoList testUpdateReturn1 = TodoList.builder().build();
        TodoList testUpdateReturn2 = TodoList.builder().build();
        // stub mapper
        when(mockMapper.updateAll(any(Object.class), eq(testUpdateVo)))
                .thenReturn(List.of(testUpdateReturn1, testUpdateReturn2));

        /* Act */
        TodoListMapper aopTargetMapper = getAopTargetedMapper();
        aopTargetMapper.updateAll(new Object(), testUpdateVo);

        /* Assert */
        @SuppressWarnings("unchecked") ArgumentCaptor<List<TodoList>> argumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(mockMapper, times(1)).recordHistories(argumentCaptor.capture());
        verify(mockMapper, times(0)).recordHistory(any());
        List<TodoList> actualUpdateAllReturn = argumentCaptor.getValue();
        assertThat(actualUpdateAllReturn.size()).isEqualTo(2);
        assertThat(actualUpdateAllReturn.get(0)).isEqualTo(testUpdateReturn1);
        assertThat(actualUpdateAllReturn.get(1)).isEqualTo(testUpdateReturn2);
    }

    private TodoListMapper getAopTargetedMapper() {
        return aspectJProxyFactory.getProxy();
    }
}
