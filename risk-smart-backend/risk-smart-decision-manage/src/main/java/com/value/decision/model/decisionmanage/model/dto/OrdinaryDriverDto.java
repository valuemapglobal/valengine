package com.value.decision.model.decisionmanage.model.dto;

import lombok.Data;

/**
 * 司机dto
 */
@Data
public class OrdinaryDriverDto {

    /**
     * 身份证号
     */
    private String number;

    /**
     * 危货司机身份证号
     */
    private String credentialNo;
}
