package com.value.decision.model.decisionmanage.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author dianne
 * @since 2024-11-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ModelProcessData extends Model {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 任务号
     */
    private String taskNo;

    /**
     * 入参
     */
    private String parameter;

    /**
     * 响应值
     */
    private String responseValue;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 部门ID
     */
    private Integer deptId;

    /**
     * 数据状态 0正常 1删除
     */
    private Boolean dataStatus;

    /**
     * 策略标识导航  1评分 5规则 6分类
     */
    private Integer ruleCode;

    /**
     * 策略ID
     */
    private String modelId;


}
