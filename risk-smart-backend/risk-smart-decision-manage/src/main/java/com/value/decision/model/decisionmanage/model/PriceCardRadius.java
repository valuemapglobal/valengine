package com.value.decision.model.decisionmanage.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.value.decision.model.decisionmanage.model.dto.model.PriceMatrixRowDTO;
import com.value.decision.model.decisionmanage.model.dto.model.QuotaRangeDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 定价详情表
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PriceCardRadius extends Model {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 关联定价卡id
     */
    private Integer priceCardId;

    /**
     * 父id
     */
    private Integer parentId;

    /**
     * 祖级列表
     */
    private String ancestors;

    /**
     * 评级
      */
    private String standardName;

    /**
     * 定价
     */
    private String priceRange;

    /**
     * 定价说明
     */
    private String priceRangeContent;

    /**
     * 左侧产品导航标识
     */
    @NotBlank(message = "产品导航标识不可为空")
    private String projectCode;

    /**
     * 业务场景导航标识
     */
    @NotBlank(message = "业务场景导航标识不可为空")
    private String businessCode;

    /**
     * 策略类型导航标识
     */
    @NotBlank(message = "策略类型导航标识不可为空")
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

    /**
     * 额度区间列表（方案B）
     */
    @TableField(exist = false)
    // TODO: 临时注释校验，待前后端对齐数据格式后再启用
    // @NotNull(message = "额度区间不可为空")
    private List<QuotaRangeDTO> quotaRanges;

    /**
     * 定价矩阵（方案B）
     */
    @TableField(exist = false)
    // TODO: 临时注释校验，待前后端对齐数据格式后再启用
    // @NotNull(message = "定价矩阵不可为空")
    private List<PriceMatrixRowDTO> priceMatrix;

    /**
     * 额度下限（万元）
     */
    private BigDecimal quotaMin;

    /**
     * 额度上限（万元）
     */
    private BigDecimal quotaMax;

    /**
     * 是否包含下限：1包含 0不包含
     */
    private Integer quotaIncludeMin;

    /**
     * 是否包含上限：1包含 0不包含
     */
    private Integer quotaIncludeMax;


}
