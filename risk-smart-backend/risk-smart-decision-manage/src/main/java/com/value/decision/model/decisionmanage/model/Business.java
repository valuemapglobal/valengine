package com.value.decision.model.decisionmanage.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.risksmart.common.core.constant.OperationType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 业务场景表
 * </p>
 *
 * @author Vida
 * @since 2024-11-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("business")
public class Business implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 主键id
     */
    @NotNull(message = "id不能为空",groups = {OperationType.Update.class})
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @NotBlank(message = "业务场景名称不能为空",groups = {OperationType.Create.class})
    private String name;

    private Long userId;

    private Long deptId;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableLogic(value = "false", delval = "true")
    private Boolean dataStatus;

    private Short deptFlag;
}
