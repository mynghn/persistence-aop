package mynghn.persistenceaop.aop.injection.aspect;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import mynghn.persistenceaop.aop.injection.session.AdviceSession;
import mynghn.persistenceaop.aop.injection.session.AdviceSessionBuilder;
import mynghn.persistenceaop.sampleapp.entity.TodoList;
import mynghn.persistenceaop.sampleapp.mapper.TodoListMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

class InjectionAspectConcurrencyTest {

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
                            AdviceSession.builder()
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

    private MockedStatic<AdviceSessionBuilder> stubAdviceSessionBuilder(AdviceSession testSession) {
        MockedStatic<AdviceSessionBuilder> mocked = mockStatic(
                AdviceSessionBuilder.class);

        mocked.when(AdviceSessionBuilder::newSession).thenReturn(testSession);

        return mocked;
    }

    private void testCreateStampUpdateStampAndSoftDeleteStampInjectedBeforeInsert(AdviceSession testSession) {
        // Arrange
        TodoListMapper mockMapper = mock(TodoListMapper.class);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(mockMapper);
        aspectJProxyFactory.addAspect(new InjectionAspect());
        TodoListMapper aopTargetedMapper = aspectJProxyFactory.getProxy();

        TodoList testInsertVo = TodoList.builder().build();

        // Act
        try (MockedStatic<AdviceSessionBuilder> ignored = stubAdviceSessionBuilder(testSession)) {
            aopTargetedMapper.insert(testInsertVo);
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
}
