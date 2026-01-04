package com.value.data.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 分析指标规则详情表
 * </p>
 *
 * @author Vida
 * @since 2023-08-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("rule_details")
public class RuleDetails implements Serializable {

    private static final long serialVersionUID=1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 分析指标标识
     */
    private String analysisIndicatorsNo;

    /**
     * 分析指标对象标识
     */
    private String analysisIndicatorsObjectNo;

    /**
     * 分析指标规则标识
     */
    private String analysisIndicatorsRuleNo;

    /**
     * 分析指标规则详情标识
     */
    private String ruleDetailsNo;

    /**
     * 判断条件一级标题
     */
    private String packageNo;

    /**
     * 判断条件二级标题
     */
    private String objectNo;

    /**
     * 判断条件一级标题
     */
    private String attributeNo;

    /**
     * 运算符
     */
    private String operator;

    /**
     * 值
     */
    private String value;

    /**
     * 父标识
     */
    private String parentNo;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建者
     */
    private String createBy;

    /**
     * 更新者
     */
    private String updateBy;

    /**
     * 数据状态(0-正常，1-删除)
     */
    private Boolean dataStatus;


}
