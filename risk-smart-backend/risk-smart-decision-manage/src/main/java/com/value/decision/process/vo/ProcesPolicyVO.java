package com.value.decision.process.vo;

import lombok.Data;

/**
 * 额度任务回调返回值
 */

@Data
public class ProcesPolicyVO {

    /**
     * 任务编号
     */
    private String taskNumber;

    /**
     * 模块标识  模块id  1:评分 2:评级 3:额度 4:定价 5:规则 6:分类
     */
    private Integer moduleId;

    /**
     * 部门ID
     */
    private Integer deptId;

}
