package com.value.decision.model.decisionmanage.controller.feign;

import com.value.decision.model.decisionmanage.model.dto.InterfaceQueryDTO;
import com.value.decision.model.decisionmanage.model.vo.HttpResult;
import com.value.decision.model.decisionmanage.model.vo.InterfaceVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 * 数据中台Feign客户端接口
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@FeignClient( contextId = "DataMiddleStationFeign",value = "risk-smart-data-middle-station", fallbackFactory = DataMiddleStationFeignFactory.class)
public interface DataMiddleStationFeign {
    @PostMapping("/interfaceManage/query/list")
    HttpResult<List<InterfaceVO>> queryList(@RequestBody InterfaceQueryDTO dto);

    @PostMapping("/decision/quoted-information")
    HttpResult<Map<String,Map<String, Map<String,String>>>> decisionQI(@RequestBody InterfaceQueryDTO dto);
}
