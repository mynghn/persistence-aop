package mynghn.persistenceaop.aop.context.contexts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mynghn.persistenceaop.sampleapp.entity.CommonCode;
import mynghn.persistenceaop.sampleapp.entity.specification.CommonCodeSpec;
import mynghn.persistenceaop.sampleapp.enums.CommonCodeGroup;
import mynghn.persistenceaop.sampleapp.mapper.CommonCodeMapper;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommonCodesCache implements ExecutionScopeContext {

    private final Map<CommonCodeGroup, Map<String, CommonCode>> storage = new HashMap<>();
    private final CommonCodeMapper mapper;

    public CommonCode get(CommonCodeGroup group, String codeValue) {
        Map<String, CommonCode> groupBook = storage.computeIfAbsent(group, this::buildGroupBook);

        if (!groupBook.containsKey(codeValue)) {
            throw new IllegalArgumentException(
                    String.format("Common code w/ ID '%s' does not exist in group '%s'",
                            codeValue, group));
        }

        return groupBook.get(codeValue);
    }

    private Map<String, CommonCode> buildGroupBook(CommonCodeGroup group) {
        List<CommonCode> fetched = mapper.selectAll(
                CommonCodeSpec.builder().groupIdEq(group.getId()).build());

        if (fetched.isEmpty()) {
            throw new IllegalStateException(
                    String.format("Common codes with group '%s' do not exist.", group));
        }
        log.debug("Common codes in group '{}' loaded to cache: {}", group, fetched);

        return fetched.stream()
                .collect(Collectors.toMap(CommonCode::getValue, Function.identity()));
    }

}
