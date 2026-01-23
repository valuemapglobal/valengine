package com.value.decision.common.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 加密数据传输对象
 * 替代外部gm库的EncryptDTO
 *
 * @author OP-Lite Team
 * @since 1.0.0
 */
@Data
public class EncryptDTO {
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

    public EncryptDTO(String manageNo, String sourceNo, String interfaceNo, Map<String, ?> paramData, String appKey, String sign, String timestamp, String orderId) {
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
        return this.manageNo;
    }

    public void setManageNo(String manageNo) {
        this.manageNo = manageNo;
    }

    public String getSourceNo() {
        return this.sourceNo;
    }

    public void setSourceNo(String sourceNo) {
        this.sourceNo = sourceNo;
    }

    public String getInterfaceNo() {
        return this.interfaceNo;
    }

    public void setInterfaceNo(String interfaceNo) {
        this.interfaceNo = interfaceNo;
    }

    @Override
    public String toString() {
        return "EncryptDTO{manageNo：'" + this.manageNo + '\'' + ", sourceNo='" + this.sourceNo + '\'' + ", interfaceNo='" + this.interfaceNo + '\'' + ", paramData=" + this.paramData + ", appKey='" + this.appKey + '\'' + ", sign='" + this.sign + '\'' + ", timestamp='" + this.timestamp + '\'' + ", orderId='" + this.orderId + '\'' + '}';
    }
}