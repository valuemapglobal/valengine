package com.value.decision.model.decisionmanage.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.value.decision.common.constant.OperationType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 产品表
 * </p>
 *
 * @author Vida
 * @since 2024-11-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("product")
public class Product implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    @NotNull(message = "id不能为空",groups = {OperationType.Update.class})
    private Long id;

    @NotBlank(message = "名称不能为空",groups = {OperationType.Create.class})
    private String name;

    private Long userId;

    private Long deptId;

    private Short deptFlag;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableLogic(value = "false", delval = "true")
    private Boolean dataStatus;
}
