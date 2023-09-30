package mynghn.persistenceaop.aop.context.contexts;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import mynghn.persistenceaop.sampleapp.entity.CommonCode;
import mynghn.persistenceaop.sampleapp.entity.specification.CommonCodeSpec;
import mynghn.persistenceaop.sampleapp.enums.CommonCodeGroup;
import mynghn.persistenceaop.sampleapp.mapper.CommonCodeMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommonCodesCacheTest {

    private final List<CommonCode> testTodoListTypeCodes = List.of(
            CommonCode.builder()
                    .groupId(CommonCodeGroup.TODO_LIST_TYPE.getId())
                    .value("TL-01")
                    .name("Test todo list type 1")
                    .build(),
            CommonCode.builder()
                    .groupId(CommonCodeGroup.TODO_LIST_TYPE.getId())
                    .value("TL-02")
                    .name("Test todo list type 2")
                    .build(),
            CommonCode.builder()
                    .groupId(CommonCodeGroup.TODO_LIST_TYPE.getId())
                    .value("TL-03")
                    .name("Test todo list type 3")
                    .build()
    );

    @Mock
    private CommonCodeMapper mockMapper;
    @InjectMocks
    private CommonCodesCache sut;

    @Test
    void commonCodeCorrectlyReturnedWhenCacheIsEmpty() {
        // Arrange
        CommonCodeGroup testCodeGroupEnum = CommonCodeGroup.TODO_LIST_TYPE;
        CommonCode testTargetCode = testTodoListTypeCodes.get(0);
        when(mockMapper.selectAll(
                argThat((CommonCodeSpec spec) -> spec != null
                        && CommonCodeGroup.TODO_LIST_TYPE.getId().equals(spec.getGroupIdEq()))
        )).thenReturn(testTodoListTypeCodes);

        // Act
        CommonCode actualReturned = sut.get(testCodeGroupEnum, testTargetCode.getValue());

        // Assert
        assertThat(actualReturned).isEqualTo(testTargetCode);
    }

    @Test
    void commonCodeCorrectlyReturnedFromCacheWhenAlreadyLoaded() {
        // Arrange
        CommonCodeGroup testCodeGroupEnum = CommonCodeGroup.TODO_LIST_TYPE;
        CommonCode testTargetCode = testTodoListTypeCodes.get(0);
        when(mockMapper.selectAll(
                argThat((CommonCodeSpec spec) -> spec != null
                        && CommonCodeGroup.TODO_LIST_TYPE.getId().equals(spec.getGroupIdEq()))
        )).thenReturn(testTodoListTypeCodes);
        sut.get(testCodeGroupEnum, testTodoListTypeCodes.get(1).getValue());

        // Assert-1
        verifyNoMoreInteractions(mockMapper); // Assert before act

        // Act
        CommonCode actualReturned = sut.get(testCodeGroupEnum, testTargetCode.getValue());

        // Assert-2
        assertThat(actualReturned).isEqualTo(testTargetCode);
    }

    @Test
    void commonCodeReturnFailedWhenInvalidCodeValueProvided() {
        // Arrange
        CommonCodeGroup testCodeGroupEnum = CommonCodeGroup.TODO_LIST_TYPE;
        String invalidCodeValue = "This is an invalid code value.";
        when(mockMapper.selectAll(
                argThat((CommonCodeSpec spec) -> spec != null
                        && CommonCodeGroup.TODO_LIST_TYPE.getId().equals(spec.getGroupIdEq()))
        )).thenReturn(testTodoListTypeCodes);

        // Act & Assert
        assertThatThrownBy(() -> sut.get(testCodeGroupEnum, invalidCodeValue)).isInstanceOf(
                IllegalArgumentException.class);
    }

    @Test
    void commonCodeReturnFailedWhenEmptyCodeGroupProvided() {
        // Arrange
        CommonCodeGroup testCodeGroupEnum = CommonCodeGroup.TODO_LIST_TYPE;
        String testCodeValue = testTodoListTypeCodes.get(0).getValue();
        when(mockMapper.selectAll(
                argThat((CommonCodeSpec spec) -> spec != null
                        && CommonCodeGroup.TODO_LIST_TYPE.getId().equals(spec.getGroupIdEq()))
        )).thenReturn(List.of());

        // Act & Assert
        assertThatThrownBy(() -> sut.get(testCodeGroupEnum, testCodeValue)).isInstanceOf(
                IllegalStateException.class);
    }
}
