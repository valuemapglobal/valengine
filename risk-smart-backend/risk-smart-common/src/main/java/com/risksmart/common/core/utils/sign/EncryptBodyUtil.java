package com.risksmart.common.core.utils.sign;

import com.alibaba.fastjson2.JSON;
import com.risksmart.common.core.domain.EncryptDTO;
import java.util.HashMap;
import java.util.Map;

/**
 * 加密请求体构建工具类
 */
public class EncryptBodyUtil {
    private EncryptBodyUtil() {
    }

    /**
     * 创建加密请求体
     *
     * @param appKey 应用密钥
     * @param secret 密钥
     * @param manageNo 管理编号
     * @param sourceNo 来源编号
     * @param interfaceNo 接口编号
     * @param orderId 订单ID
     * @param paramData 参数数据
     * @return 加密后的DTO
     */
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
            Map<String, Object> sourceMap = new HashMap<>(16);
            sourceMap.put("appKey", appKey);
            sourceMap.put("manageNo", manageNo);
            sourceMap.put("sourceNo", sourceNo);
            sourceMap.put("interfaceNo", interfaceNo);
            sourceMap.put("orderId", orderId);
            sourceMap.put("paramData", JSON.toJSONString(paramData));
            sourceMap.put("timestamp", timestamp);
            String source = SM3Util.buildSource(sourceMap, secret);
            String sign = SM3Util.encrypt(source, secret);
            return new EncryptDTO(manageNo, sourceNo, interfaceNo, paramData, appKey, sign, timestamp, orderId);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to build encrypted body", e);
        }
    }
}
