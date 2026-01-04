package com.value.decision.process.controller.feign;

import com.risksmart.common.core.web.AjaxResult;
import com.value.decision.process.vo.InitJudicialReportVO;
import com.value.decision.process.vo.QueryJudicialHistoryVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// @FeignClient(contextId = "RiskValueFeign",value = "vm-risk-value",fallbackFactory = RiskValueFeignFactory.class)
// 暂时禁用此Feign客户端以解决启动问题
// @FeignClient(contextId = "RiskValueFeign",value = "vm-risk-value")
public interface RiskValueFeign {

    @PostMapping("/riskJudicial/initReportWithoutAuth")
    AjaxResult initJudicialReport(@RequestBody InitJudicialReportVO initJudicialReport);

    @PostMapping("/riskJudicial/queryJudicialWithoutAuth")
    AjaxResult queryJudicialHistory(@RequestBody QueryJudicialHistoryVO queryJudicialHistoryVO);

}
