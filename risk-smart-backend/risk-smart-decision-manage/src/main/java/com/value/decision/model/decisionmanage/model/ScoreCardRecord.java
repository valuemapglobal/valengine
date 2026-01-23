package com.value.decision.model.decisionmanage.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * <p>
 * 评分卡主表
 * </p>
 *
 * @author hc
 * @since 2023-08-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ScoreCardRecord extends Model {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 评分卡名称
     */
    @NotBlank(message = "评分卡名称不能为空")
    @Length(max = 30,message =  "评分卡名称不可超过30")
    private String scoreCard;

    /**
     * 行业
     */
    @NotEmpty(message = "行业不能为空")
    private String profession;

    /**
     * 参数
     */
    private String argument;

    /**
     * 计算评分
     */
    private String calculateScore;

    /**
     * 描述
     */
    @Length(max = 200,message = "描述不可超过200")
    private String description;

    /**
     * 数据状态 0正常 1删除
     */
    @TableLogic
    private Integer dataState;

    /**
     * 左侧产品导航标识
     */
    @NotBlank(message = "左侧产品导航标识不能为空")
    private String projectCode;

    /**
     * 业务场景导航标识
     */
    @NotNull(message = "业务场景导航标识不能为空")
    private String businessCode;

    /**
     * 策略类型导航标识
     */
    @NotNull(message = "策略类型导航标识不能为空")
    private String ruleCode;

    /**
     * 部门标识 1超级管理员 2普通用户
     */
    private Integer deptFlag;

    /**
     * 部门ID
     */
    private Integer deptId;

    /**
     * 部门ID 页面传入，用于查看已配置的部门数据
     */
    @TableField(exist = false)
    private String deptIdNew;

    /**
     * 按钮状态 1启用 0禁用
     */
    private Integer buttonState;

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
     * 操作人ID
     */
    private Integer createUserId;

    /**
     * 版本控制
     */
    private String versionControl;

    /**
     * 模型总分数
     */
    private String modelScore;

    //操作功能点
//    @NotBlank(message = "操作功能点不能为空")
    @TableField(exist = false)
    private String operationPoints;

    //策略模型名称
//    @NotNull(message = "策略模型名称不能为空")
    @TableField(exist = false)
    private String policyModelName;

    //功能点标志
//    @NotNull(message = "功能点标志不能为空")
    @TableField(exist = false)
    private String points;

//*****版本控制
    /**
     * 左侧产品导航 产品名称
     */
    @NotNull(message = "版本号拼接必须不能为空")
    @TableField(exist = false)
    private String projectName;

    /**
     * 业务场景导航 业务名称
     */
    @NotNull(message = "版本号拼接必须不能为空")
    @TableField(exist = false)
    private String businessName;

    /**
     * 区分企业和个人 P个人C企业
     */
    @NotNull(message = "版本号拼接必须不能为空")
    @TableField(exist = false)
    private String personOrCompany;

    /**
     * 模型名称
     */
    @NotNull(message = "版本号查询必须不能为空")
    @TableField(exist = false)
    private String modelName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScoreCardRecord that = (ScoreCardRecord) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(scoreCard, that.scoreCard) &&
                Objects.equals(profession, that.profession) &&
                Objects.equals(argument, that.argument) &&
                Objects.equals(calculateScore, that.calculateScore) &&
                Objects.equals(description, that.description) &&
                Objects.equals(dataState, that.dataState) &&
                Objects.equals(projectCode, that.projectCode) &&
                Objects.equals(businessCode, that.businessCode) &&
                Objects.equals(ruleCode, that.ruleCode) &&
                Objects.equals(deptFlag, that.deptFlag) &&
                Objects.equals(deptId, that.deptId) &&
                Objects.equals(deptIdNew, that.deptIdNew) &&
                Objects.equals(buttonState, that.buttonState) &&
                Objects.equals(createTime, that.createTime) &&
                Objects.equals(updateTime, that.updateTime) &&
                Objects.equals(createUserId, that.createUserId) &&
                Objects.equals(versionControl, that.versionControl) &&
                Objects.equals(modelScore, that.modelScore) &&
                Objects.equals(operationPoints, that.operationPoints) &&
                Objects.equals(policyModelName, that.policyModelName) &&
                Objects.equals(points, that.points) &&
                Objects.equals(projectName, that.projectName) &&
                Objects.equals(businessName, that.businessName) &&
                Objects.equals(personOrCompany, that.personOrCompany) &&
                Objects.equals(modelName, that.modelName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, scoreCard, profession, argument, calculateScore, description, dataState, projectCode, businessCode, ruleCode, deptFlag, deptId, deptIdNew, buttonState, createTime, updateTime, createUserId, versionControl, modelScore, operationPoints, policyModelName, points, projectName, businessName, personOrCompany, modelName);
    }

    @Override
    public String toString() {
        return "ScoreCardRecord{" +
                "id=" + id +
                ", scoreCard='" + scoreCard + '\'' +
                ", profession='" + profession + '\'' +
                ", argument='" + argument + '\'' +
                ", calculateScore='" + calculateScore + '\'' +
                ", description='" + description + '\'' +
                ", dataState=" + dataState +
                ", projectCode='" + projectCode + '\'' +
                ", businessCode='" + businessCode + '\'' +
                ", ruleCode='" + ruleCode + '\'' +
                ", deptFlag=" + deptFlag +
                ", deptId=" + deptId +
                ", deptIdNew='" + deptIdNew + '\'' +
                ", buttonState=" + buttonState +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", createUserId=" + createUserId +
                ", versionControl='" + versionControl + '\'' +
                ", modelScore='" + modelScore + '\'' +
                ", operationPoints='" + operationPoints + '\'' +
                ", policyModelName='" + policyModelName + '\'' +
                ", points='" + points + '\'' +
                ", projectName='" + projectName + '\'' +
                ", businessName='" + businessName + '\'' +
                ", personOrCompany='" + personOrCompany + '\'' +
                ", modelName='" + modelName + '\'' +
                '}';
    }
}
