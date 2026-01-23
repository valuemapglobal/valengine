package com.value.decision.model.decisionmanage.controller.feign;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class MicroApiFeignFactory implements FallbackFactory<MicroApiFeign> {
    @Override
    public MicroApiFeign create(Throwable cause) {
        return null;
    }
}
