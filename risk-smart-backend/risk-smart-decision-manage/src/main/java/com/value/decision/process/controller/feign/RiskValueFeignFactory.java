package com.value.decision.process.controller.feign;

import org.springframework.cloud.openfeign.FallbackFactory;

public class RiskValueFeignFactory implements FallbackFactory<RiskValueFeign> {

    @Override
    public RiskValueFeign create(Throwable cause) {
        return null;
    }
}
