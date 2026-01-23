package com.value.data.service.feign;

import com.value.data.domain.VMHttpResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DecisionManageFallbackFactory implements FallbackFactory<DecisionManageFeignService> {
    @Override
    public DecisionManageFeignService create(Throwable cause) {
        // 记录日志或者进行其他处理
        return new DecisionManageFeignService() {
            @Override
            public VMHttpResult<Boolean> checkField(String id) {
                log.error("属性删除校验出现异常："+id,cause);
                return VMHttpResult.error(cause.getMessage(),Boolean.FALSE);
            }
        };
    }
}
