package com.value.decision.snapshot.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * @author Dianne
 * @since 2023-05-08
 */
@Data
@Table(name = "rde_model_test_result")
public class RdeModelTestResult {

    /**
     * 主键id
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 策略id
     */
    @Column(name = "code_id")
    private Integer codeId;

    /**
     * 流水id
     */
    @Column(name = "serial_number")
    private String serialNumber;

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
     * 风险类型
     */
    @Column(name = "risk_type")
    private String riskType;

    /**
     * 风险描述
     */
    @Column(name = "risk_description")
    private String riskDescription;

    /**
     * 是否命中 0为未命中，1为命中
     */
    @Column(name = "hit")
    private Integer hit;

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
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @Column(name = "update_time")
    private LocalDateTime updateTime;
}
