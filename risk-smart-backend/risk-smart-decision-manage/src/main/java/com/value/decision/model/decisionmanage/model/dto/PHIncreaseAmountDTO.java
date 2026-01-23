package com.value.decision.model.decisionmanage.model.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * 普货提额
 */
@Data
public class PHIncreaseAmountDTO {

    @NotBlank(message = "姓名不可为空")
    private String realName;

    @NotBlank(message = "身份证不可为空")
    private String idNumber;

    @NotBlank(message = "手机号不可为空")
    private String phoneNumber;

    //认证姓名
    private String authName;

    //行驶证
    @NotBlank(message = "行驶证不可为空")
    private String credentialNo;

    //证件有效期-终
    @NotNull(message = "证件有效期-终不可为空")
    private LocalDate credentialTimeEnd;

    private String orderNo;

}
