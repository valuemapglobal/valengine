package com.value.decision.model.rdenew.vo;

import com.value.decision.model.rdenew.domain.RdeModelAntiFraud;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
public class RdeModelAntiFraudDTO extends RdeModelAntiFraud {
    /*
     *  状态字段 0为关  1为开
     */
    private String flag;

    private String projectCode;

    /**
     * 左侧产品导航 产品名称
     */
    @NotNull(message = "版本号拼接必须不能为空")
    private String projectName;

    /**
     * 业务场景导航 业务名称
     */
    @NotNull(message = "版本号拼接必须不能为空")
    private String businessName;

    /**
     * 区分企业和个人 P个人C企业
     */
    @NotNull(message = "版本号拼接必须不能为空")
    private String personOrCompany;

    /**
     * 新增/修改 0/1
     */
    @NotNull(message = "版本号拼接必须不能为空")
    private Integer judgment;

}