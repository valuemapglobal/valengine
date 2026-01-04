package com.value.decision.model.decisionmanage.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 模型规则存储表
 * </p>
 *
 * @author dianne
 * @since 2024-12-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ModelRegularData extends Model {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 任务编号
     */
    private String taskNumber;

    /**
     * 所处节点
     */
    private Integer nodeId;

    /**
     * 所选模型规则数据
     */
    private String ruleData;

    /**
     * 策略场景标识
     */
    private Integer ruleCode;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;

    /**
     * 数据状态 默认0 删除1
     */
    private Boolean dataStatus;


}
