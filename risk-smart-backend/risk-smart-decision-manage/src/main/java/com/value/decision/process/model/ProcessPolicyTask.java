package com.value.decision.process.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * <p>
 * 流程策略任务表
 * </p>
 *
 * @author hc
 * @since 2023-08-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ProcessPolicyTask extends Model {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 任务编号
     */
    @NotBlank(message = "任务编号不为空")
    private String taskNumber;

    /**
     * 流程策略模型id
     */
    @NotNull(message = "流程策略模型id不为空")
    private Integer processStrategyId;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime updateTime;

    /**
     * 部门ID
     */
    private Integer deptId;

    @TableField(exist = false)
    private String deptName;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 目前所处节点标识
     */
    @NotNull(message = "目前所处节点标识不为空")
    private Integer nodeId;

    /**
     * 企业名称
     */
    private String enterpriseName;

    /**
     * 手机号
     */
    private String mobilePhone;

    /**
     * 身份证号码
     */
    private String idNumber;

    /**
     * 个人名称
     */
    private String personalName;

    /**
     * 申请用户
     */
    @NotBlank(message = "申请用户不可为空")
    private String applyUserName;

    /**
     * 申请用户id
     */
    @NotNull(message = "申请用户id不可为空")
    private Integer applyUserId;

    @TableField(exist = false)
    private Object requestData;

    //企业标识
    private String ckey;

    //个人标识0 企业标识 1
    private String flag;

    //企业得分
    private String enterpriseScore;

    //业务标识
    @TableField(exist = false)
    private String orderNo;

    //业务系统
    @TableField(exist = false)
    private String systemFlag;

    //授信状态 1 授信中 2 通过 3 拒绝
    private Integer creditStatus;

    /**
     * 授信通过时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime creditTime;

    //txd用户授权流水号
    @TableField(exist = false)
    private String userOrderNo;

    //业务参数
    @TableField(exist = false)
    private Map<String,Object> map;

    private String errorMsg;
    
    @TableField(exist = false)
    private String credentialNo;
    
}
