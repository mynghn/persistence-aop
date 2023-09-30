package mynghn.persistenceaop.aop.context.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mynghn.persistenceaop.aop.context.contexts.CommonCodesCache;
import mynghn.persistenceaop.aop.context.contexts.ExecutionScopeContext;
import mynghn.persistenceaop.sampleapp.mapper.CommonCodeMapper;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class CommonCodesCacheProviderAspect extends
        ExecutionScopeContextProvider<CommonCodesCache> {

    private final CommonCodeMapper commonCodeMapper;

    @Override
    protected boolean supports(Class<? extends ExecutionScopeContext> contextClass) {
        return CommonCodesCache.class.equals(contextClass);
    }

    @Override
    public CommonCodesCache buildContext() {
        return new CommonCodesCache(commonCodeMapper);
    }

    @Override
    protected Logger getLogger() {
        return log;
    }
}
