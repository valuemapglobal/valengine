package com.value.data.common.utils;

import com.alibaba.fastjson.JSONObject;
import com.value.data.common.dto.EncryptDTO;
import java.util.HashMap;
import java.util.Map;

public class EncryptBodyUtil {
    private EncryptBodyUtil() {
    }

    public static EncryptDTO createEncryptBody(
            String appKey,
            String secret,
            String manageNo,
            String sourceNo,
            String interfaceNo,
            String orderId,
            Map<String, ?> paramData
    ) {
        try {
            String timestamp = SM3Util.getTime();
            Map<String, Object> sourceMap = new HashMap<>();
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
        } catch (Exception e) {
            throw new IllegalStateException("Failed to build encrypted body", e);
        }
    }
}
