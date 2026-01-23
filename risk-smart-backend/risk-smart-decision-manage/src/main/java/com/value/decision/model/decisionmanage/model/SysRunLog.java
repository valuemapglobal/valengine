package com.value.decision.model.decisionmanage.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author hc
 * @since 2023-08-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysRunLog extends Model {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 申请人id
     */
    private Integer approvalUserId;

    /**
     * 申请人用户名称
     */
    private String approvalUserName;

    /**
     * 部门id
     */
    private Integer deptId;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 金融产品
     */
    private String productName;

    /**
     * 业务场景
     */
    private String businessCode;

    /**
     * 策略模型
     */
    private String ruleCode;

    /**
     * 策略名称
     */
    private String ruleName;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 返还参数
     */
    private String result;

    /**
     * 返还状态
     */
    private String resultCode;

    /**
     * 请求地址
     */
    private String apiUrl;

    /**
     * 请求参数
     */
    private String requestData;

    /**
     * 响应时间
     */
    private int returnTime;

    /**
     * 模型id
     */
    private int moduleId;

    @TableField(exist = false)
    private String startTime;
    @TableField(exist = false)
    private String endTime;

    @TableField(exist = false)
    private Integer pageNum;
    @TableField(exist = false)
    private Integer pageSize;
}
