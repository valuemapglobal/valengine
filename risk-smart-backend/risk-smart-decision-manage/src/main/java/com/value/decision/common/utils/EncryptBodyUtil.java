package com.value.decision.common.utils;

import com.alibaba.fastjson2.JSONObject;
import com.value.decision.common.dto.EncryptDTO;

import java.util.HashMap;
import java.util.Map;

/**
 * 加密工具类
 * 替代外部gm库的EncryptBodyUtil
 *
 * @author OP-Lite Team
 * @since 1.0.0
 */
public class EncryptBodyUtil {
    public EncryptBodyUtil() {
    }

    public static EncryptDTO createEncryptBody(String appKey, String secret, String manageNo, String sourceNo, String interfaceNo, String orderId, Map<String, ?> paramData) throws Exception {
        String timestamp = SM3Util.getTime();
        Map<String, Object> sourceMap = new HashMap();
        sourceMap.put("appKey", appKey);
        sourceMap.put("manageNo", manageNo);
        sourceMap.put("sourceNo", sourceNo);
        sourceMap.put("interfaceNo", interfaceNo);
        sourceMap.put("orderId", orderId);
        sourceMap.put("paramData", JSONObject.toJSONString(paramData));
        sourceMap.put("timestamp", timestamp);
        String source = SM3Util.buildSource(sourceMap, secret);
        String sign = SM3Util.encrypt(source, secret);
        return new EncryptDTO(manageNo, sourceNo, interfaceNo, paramData, appKey, sign, timestamp, orderId);
    }
}
