package com.value.decision.snapshot.vo;

import lombok.Data;

/**
 * @ClassName RdeModelSelectListEntryVo
 * @Description TODO
 * @Authot Administrator
 * @Date 2023/5/12 16:31
 **/
@Data
public class RdeModelSelectListEntryVo {

    private int pageNum;

    /**
     * 企业名称
     */
    private String companyName;
    /**
     * 测试状态
     */
    private String testState;
    /**
     * 项目code
     */
    private String projectCode;
    /**
     * 页面标识 分类为1,规则为0
     */
    private String ruleCode;

    private String businessCode;
}
