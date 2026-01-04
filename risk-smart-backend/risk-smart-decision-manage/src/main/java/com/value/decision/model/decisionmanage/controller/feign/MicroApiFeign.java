package com.value.decision.model.decisionmanage.controller.feign;

import com.risksmart.common.core.web.AjaxResult;
import com.value.decision.model.decisionmanage.model.dto.OrdinaryDriverDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "MicroApiFeignClient",value = "vm-micro-api",fallbackFactory = MicroApiFeignFactory.class)
public interface MicroApiFeign {

    /**
     * 根据身份证号获取普货司机信息
     * @param ordinaryDriverDto
     * @return
     */
    @PostMapping("/driver/increase/amount")
    public AjaxResult getOrdinaryDriverByCardNo(@RequestBody OrdinaryDriverDto ordinaryDriverDto);

}
