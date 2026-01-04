package com.value.data.domain.dto;


import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

/**
 * Api调用
 */
@Data
public class ApiDTO implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 接口信息唯一标识
     */
    @NotBlank(message = "manageNo 参数错误")
    @Pattern(regexp = "^[a-zA-Z0-9_:]{1,64}$", message = "ManageNo只允许英文、数字、下划线、冒号，且长度不超过64位")
    private String manageNo;

    /**
     * 接口供应商唯一标识
     */
    @NotBlank(message = "sourceNo 参数错误")
    @Pattern(regexp = "^[a-zA-Z0-9_:]{1,64}$", message = "sourceNo只允许英文、数字、下划线、冒号，且长度不超过64位")
    private String sourceNo;

    /**
     * 接口编号
     */
    @NotBlank(message = "interfaceNo 参数错误")
    @Size(min = 1, max = 50, message = "interfaceNo应该在1-50字符之间")
    private String interfaceNo;

    /**
     * 跳过登录验证
     */
    private String apiToken;

    /**
     * 接口入参
     */
    private JSONObject paramData;

    /**
     * 公钥
     */
    @NotBlank(message = "appKey 参数错误")
    private String appKey;

    /**
     *签名
     */
    @NotBlank(message = "sign 参数错误")
    private String sign;

    /**
     * 发起调用时间
     */
    @NotBlank(message = "timestamp 参数错误")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$", message = "timestamp 参数格式异常")
    private String timestamp;

    /**
     * 订单号
     */
    @NotBlank(message = "orderId 参数错误")
    @Size(min = 1, max = 32, message = "orderId应该在1-32字符之间")
    private String orderId;
}
