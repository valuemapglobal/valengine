package com.value.decision.model.decisionmanage.controller.feign;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class FinancingFeignFactory implements FallbackFactory<FinancingFeign> {
    @Override
    public FinancingFeign create(Throwable cause) {
        return null;
    }
}
