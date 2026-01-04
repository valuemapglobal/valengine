package com.value.decision.process.service.feign;

import com.alibaba.fastjson2.JSONObject;
import com.value.decision.common.dto.EncryptDTO;
import com.risksmart.common.core.web.AjaxResult;
import com.value.decision.process.dto.GetInterfaceInputParameterDTO;
import com.value.decision.process.vo.InterfaceUser;
import com.value.decision.common.domain.R;
import com.value.decision.process.dto.FindInterfaceInfoDTO;
import com.value.decision.process.dto.FindInterfaceFieldIdInfoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class FeignDataMiddleStationFactory implements FallbackFactory<FeignDataMiddleStationService> {
    private static final Logger log = LoggerFactory.getLogger(FeignDataMiddleStationFactory.class);
    @Override
    public FeignDataMiddleStationService create(Throwable cause) {
        log.error("调用数据中台服务失败:{}", cause.getMessage());
        return new FeignDataMiddleStationService() {
            @Override
            public JSONObject api(EncryptDTO encryptBody) {
                return JSONObject.parseObject(JSONObject.toJSONString(AjaxResult.error("调用数据中台异常："+cause.getMessage())));
            }

            @Override
            public AjaxResult getInterfaceInputParameter(GetInterfaceInputParameterDTO params) {
                return AjaxResult.error("调用数据中台接口参数失败:" + cause.getMessage());
            }

            @Override
            public R<InterfaceUser> queryAppKeyByUserId(Integer userId) {
                return R.fail("查询该用户appkey时出错");
            }

            @Override
            public AjaxResult findInterfaceFieldIdInfo(FindInterfaceFieldIdInfoDTO dto) {
                return AjaxResult.error("调用数据中台接口字段信息失败:" + cause.getMessage());
            }

            @Override
            public AjaxResult findInterfaceInfo(FindInterfaceInfoDTO dto) {
                return AjaxResult.error("调用数据中台接口信息失败:" + cause.getMessage());
            }
        };
    }
}
