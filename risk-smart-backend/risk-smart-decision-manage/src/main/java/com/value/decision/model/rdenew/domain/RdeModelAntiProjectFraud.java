package com.value.decision.model.rdenew.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.risksmart.common.core.web.domain.BaseVO;
import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import java.util.Date;

/**
 * 决策引擎项目-模型关联表实体
 * @author Raysen
 * @create 2023/4/14 9:24
 */
@Data
@Table(name = "rde_model_anti_project_fraud")
public class RdeModelAntiProjectFraud extends BaseVO {
    @Id
    @GeneratedValue(generator = "JDBC")
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 项目模块code，1001：银行流水
     */
    @Column(name = "project_code")
    private String projectCode;

    /**
     * 模型id
     */
    @Column(name = "model_id")
    private Integer modelId;

    /**
     * 规则组id
     */
    @Column(name = "group_id")
    private Integer groupId;

    /**
     * 规则id
     */
    @Column(name = "rule_id")
    private Integer ruleId;

    /**
     * 操作人ID
     */
    @Column(name = "user_id")
    private Integer userId;

    /**
     * 部门id
     */
    @Column(name = "dept_id")
    private Integer deptId;
    
    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private Date updateTime;

    /**
     * 数据状态
     */
    @Column(name = "data_status")
    private Byte dataStatus;
    /**
     * 业务场景导航标识
     */
    @NotBlank(message = "业务场景标识不能为空")
    @Column(name = "business_code")
    private String businessCode;


    @Column(name = "rule_code")
    private String ruleCode;

}