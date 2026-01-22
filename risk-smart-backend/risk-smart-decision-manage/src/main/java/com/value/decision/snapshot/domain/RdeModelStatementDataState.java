package com.value.decision.snapshot.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Date;

/**
 * 模型报表数据状态表
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@Data
@Table(name = "rde_model_statement_data_state")
public class RdeModelStatementDataState {

    /**
     * 主键id
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 流水id
     */
    @Column(name = "serial_number")
    private String serialNumber;

    /**
     * 测试时间
     */
    @Column(name = "test_time")
    private Date testTime;

    /**
     * 部门id
     */
    @Column(name = "dept_id")
    private Integer deptId;

    /**
     * 用户id
     */
    @Column(name = "user_id")
    private Integer userId;

    /**
     * 测试状态
     */
    @Column(name = "test_status")
    private String testStatus;

    /**
     * 页面标识 分类为1,规则为0
     */
    @Column(name = "rule_code")
    private String ruleCode;

    /**
     * 项目代码;银行流水1001,支付流水1002
     */
    @Column(name = "project_code")
    private String projectCode;

    /**
     * 数据状态
     */
    @Column(name = "data_status")
    private Integer dataStatus;

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
}
