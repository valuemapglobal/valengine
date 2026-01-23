package com.risksmart.common.core.domain;

import java.io.Serializable;
import java.util.Map;

/**
 * 加密请求体DTO
 */
public class EncryptDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String manageNo;
    private String sourceNo;
    private String interfaceNo;
    private Map<String, ?> paramData;
    private String appKey;
    private String sign;
    private String timestamp;
    private String orderId;

    public EncryptDTO() {
    }

    public EncryptDTO(
            String manageNo,
            String sourceNo,
            String interfaceNo,
            Map<String, ?> paramData,
            String appKey,
            String sign,
            String timestamp,
            String orderId
    ) {
        this.manageNo = manageNo;
        this.sourceNo = sourceNo;
        this.interfaceNo = interfaceNo;
        this.paramData = paramData;
        this.appKey = appKey;
        this.sign = sign;
        this.timestamp = timestamp;
        this.orderId = orderId;
    }

    public String getManageNo() {
        return manageNo;
    }

    public void setManageNo(String manageNo) {
        this.manageNo = manageNo;
    }

    public String getSourceNo() {
        return sourceNo;
    }

    public void setSourceNo(String sourceNo) {
        this.sourceNo = sourceNo;
    }

    public String getInterfaceNo() {
        return interfaceNo;
    }

    public void setInterfaceNo(String interfaceNo) {
        this.interfaceNo = interfaceNo;
    }

    public Map<String, ?> getParamData() {
        return paramData;
    }

    public void setParamData(Map<String, ?> paramData) {
        this.paramData = paramData;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
