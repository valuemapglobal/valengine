package com.value.decision.model.decisionmanage.controller.feign;

import com.value.decision.model.decisionmanage.model.dto.InterfaceQueryDTO;
import com.value.decision.model.decisionmanage.model.vo.HttpResult;
import com.value.decision.model.decisionmanage.model.vo.InterfaceVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author Vida
 * @date 2025年03月19日 10:35
 * @description
 */
@Component
@Slf4j
public class DataMiddleStationFeignFactory implements FallbackFactory<DataMiddleStationFeign> {
    @Override
    public DataMiddleStationFeign create(Throwable cause) {
        log.error("调用数据中台服务失败:{}", cause);
        return new DataMiddleStationFeign() {
            @Override
            public HttpResult<List<InterfaceVO>> queryList(InterfaceQueryDTO dto) {
                return HttpResult.fail(cause.getMessage());
            }

            @Override
            public HttpResult<Map<String, Map<String, Map<String, String>>>> decisionQI(InterfaceQueryDTO dto) {
                return HttpResult.fail(cause.getMessage());
            }
        };
    }
}
