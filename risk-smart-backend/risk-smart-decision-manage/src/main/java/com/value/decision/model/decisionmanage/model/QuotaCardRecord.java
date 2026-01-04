package com.value.decision.model.decisionmanage.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * <p>
 * 额度卡主表
 * </p>
 *
 * @author hc
 * @since 2023-08-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QuotaCardRecord extends Model {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 关联评级卡id
     */
    private Integer rateCardId;

    /**
     * 额度模型名称(对应评级卡名称)
     */
    private String quotaCard;

    /**
     * 数据状态
     */
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

    private String description;

    // ==================== 标准额度计算公式配置 ====================

    /**
     * Excel计算公式
     * 示例：开户时长*100 + 账户等级*500
     */
    private String standardQuotaFormula;

    /**
     * 中文公式（展示用）
     * 示例：@开户时长 × 100 + @账户等级 × 500
     */
    private String standardQuotaFormulaZh;

    /**
     * 公式变量映射JSON（字符串格式）
     * 格式示例：
     * {
     *   "account_age": {
     *     "fieldCode": "account_age",
     *     "fieldName": "开户时长",
     *     "manageNo": "DATA_MIDDLE_STATION:1001",
     *     "dataType": "number",
     *     "fullPath": "账户信息/账户基本/开户时长(月)"
     *   }
     * }
     *
     * ⚠️ 前端需传 JSON 字符串，不是 JSON 对象
     */
    private String formulaVariables;

    /**
     * 已选字段列表JSON（字符串格式）
     * 格式示例：
     * [
     *   "账户信息/账户基本/开户时长(月)",
     *   "账户信息/账户余额/当前余额"
     * ]
     *
     * ⚠️ 前端需传 JSON 字符串，不是 JSON 对象
     */
    private String selectedFields;

}
