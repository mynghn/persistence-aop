package mynghn.persistenceaop.sampleapp.service.reader;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import mynghn.persistenceaop.aop.context.aspect.CommonCodesCacheProviderAspect;
import mynghn.persistenceaop.aop.context.contexts.CommonCodesCache;
import mynghn.persistenceaop.sampleapp.entity.CommonCode;
import mynghn.persistenceaop.sampleapp.entity.id.CommonCodeId;
import mynghn.persistenceaop.sampleapp.enums.CommonCodeGroup;
import mynghn.persistenceaop.sampleapp.mapper.CommonCodeMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommonCodeReader {

    private final CommonCodesCacheProviderAspect cacheProvider;

    private final CommonCodeMapper mapper;


    public CommonCode get(CommonCodeGroup group, String codeId) {
        Optional<CommonCodesCache> cacheOptional = cacheProvider.get();
        if (cacheOptional.isPresent()) {
            return cacheOptional.get().get(group, codeId);
        }
        return mapper.select(new CommonCodeId(group.getId(), codeId));
    }
}
