package com.value.decision.model.decisionmanage.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 定价详情表
 * </p>
 *
 * @author hc
 * @since 2023-08-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PriceCardRadiusSnapshot extends Model {

    private static final long serialVersionUID = 1L;


    private Integer id;

    /**
     * 关联额度卡id
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
    private String projectCode;

    /**
     * 业务场景导航标识
     */
    private String businessCode;

    /**
     * 策略类型导航标识
     */
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
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
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
     * 评级名称
     */
    private String standardName;

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
