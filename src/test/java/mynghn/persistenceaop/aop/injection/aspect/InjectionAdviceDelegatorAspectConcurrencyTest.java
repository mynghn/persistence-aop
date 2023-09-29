package mynghn.persistenceaop.aop.injection.aspect;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import mynghn.persistenceaop.aop.context.aspect.RequestSessionProviderAspect;
import mynghn.persistenceaop.aop.context.contexts.RequestSession;
import mynghn.persistenceaop.sampleapp.entity.TodoList;
import mynghn.persistenceaop.sampleapp.mapper.TodoListMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

class InjectionAdviceDelegatorAspectConcurrencyTest {

    @Test
    void stampInjectionBeforeInsertInMultiThreadsWork() throws InterruptedException {
        // Arrange
        int nThreads = 10;
        CountDownLatch latch = new CountDownLatch(nThreads);
        ExecutorService testExecutorService = Executors.newFixedThreadPool(nThreads);

        AtomicInteger testSuccessCount = new AtomicInteger();

        // Act
        for (int i = 0; i < nThreads; i++) {
            final int testThreadNo = i + 1;
            testExecutorService.execute(() -> {
                try {
                    testCreateStampUpdateStampAndSoftDeleteStampInjectedBeforeInsert(
                            RequestSession.builder()
                                    .time(LocalDateTime.now())
                                    .username(String.format("Test #%d", testThreadNo))
                                    .build()
                    );

                    testSuccessCount.getAndIncrement();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        // Assert
        assertThat(testSuccessCount.get()).isEqualTo(nThreads);
    }

    private void testCreateStampUpdateStampAndSoftDeleteStampInjectedBeforeInsert(
            RequestSession testSession) {
        // Arrange
        RequestSessionProviderAspect sessionProviderAspectSpy = spy(
                RequestSessionProviderAspect.class);
        when(sessionProviderAspectSpy.buildContext()).thenReturn(testSession);

        AspectJProxyFactory adviceCoreProxyFactory = new AspectJProxyFactory(
                new InjectionAdviceCore(sessionProviderAspectSpy));
        adviceCoreProxyFactory.addAspect(sessionProviderAspectSpy);

        TodoListMapper mockMapper = mock(TodoListMapper.class);
        AspectJProxyFactory mapperProxyFactory = new AspectJProxyFactory(
                mockMapper);
        mapperProxyFactory.addAspect(
                new InjectionAdviceDelegatorAspect(adviceCoreProxyFactory.getProxy()));
        TodoListMapper aopTargetMapper = mapperProxyFactory.getProxy();

        TodoList testInsertVo = TodoList.builder().build();

        // Act
        aopTargetMapper.insert(testInsertVo);

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
}
