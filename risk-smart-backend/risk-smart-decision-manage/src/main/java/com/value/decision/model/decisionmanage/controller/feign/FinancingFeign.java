package com.value.decision.model.decisionmanage.controller.feign;

import com.risksmart.common.core.web.AjaxResult;
import com.value.decision.model.decisionmanage.model.vo.VmFinancingCreditIncreaseQuotaAuthVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient( contextId = "FeignPhRuleResultService",value = "vm-financing-base", fallbackFactory = FinancingFeignFactory.class)
public interface FinancingFeign {

    @PostMapping("/creditIncreaseQuotaAuth/findCreditIncreaseQuotaAuthByCredentialNo")
    AjaxResult findCreditIncreaseQuotaAuthByCredentialNo(@RequestBody VmFinancingCreditIncreaseQuotaAuthVO creditIncreaseQuotaAuthVO);

}
