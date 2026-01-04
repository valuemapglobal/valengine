package com.value.decision.snapshot.vo;

import lombok.Data;

/**
 * @author hcp
 * @since 2023/05/08
 */
@Data
public class RdeModelTestResultEntryVO {
    /**
     * 尽调id
     */
    private String serialNumber;

    /**
     * 测试状态
     */
    private String state;

    /**
     * 项目code
     */
    private String projectCode;

    /**
     * 页面标识 分类为1,规则为0
     */
    private String ruleCode;
}
