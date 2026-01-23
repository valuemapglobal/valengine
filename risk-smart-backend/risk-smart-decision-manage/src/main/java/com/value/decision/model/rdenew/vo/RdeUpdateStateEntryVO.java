package com.value.decision.model.rdenew.vo;

import lombok.Data;

/**
 * @author hcp
 * @date 2023年05月16日 15:46
 */
@Data
public class RdeUpdateStateEntryVO {
    /**
     * 需要修改状态的id （模型id/策略组id/策略id）
     */
    private Integer id;
    /**
     * 部门id
     */
    private Integer deptId;
    /**
     * 修改状态 1为启用 0为关闭
     */
    private Integer status;
    /**
     * 分类 1为策略模型；2为规则组；3为规则；4为level表
     */
    private Integer classify;
    /**
     * 数据库表名
     */
    private String tableName;

    /**
     * 页面标识 分类为6,规则为5
     */
    private String ruleCode;

    /**
     * 项目代码;银行流水1001,支付流水1002
     */
    private String projectCode;

    /**
     * 业务场景导航标识
     */
    private String businessCode;

    /**
     * 版本控制
     */
    private String versionControl;
}
