package com.value.data.service.feign;

import com.value.data.constant.ServiceNameConstant;
import com.value.data.domain.VMHttpResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(contextId = "decisionManageFeignService", value = ServiceNameConstant.DECISION_MANAGE_SERVICE, fallbackFactory = DecisionManageFallbackFactory.class)
public interface DecisionManageFeignService {
    /**
     * 检查字段是否有引用
     * @param id 数据平台属性no编号
     * @return true-可以删除，false-不可以删除
     */
    @GetMapping("/rdenew/dataMiddleStation/checkReferences/{id}")
    VMHttpResult<Boolean> checkField(@PathVariable String id);
}
