package com.value.data.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.value.data.common.jackson.JSON2StringDeserializer;
import com.value.data.common.jackson.String2JSONSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 分析指标属性表
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("metrics_attribute")
public class MetricsAttributeEntity implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属模块的id
     */
    @NotNull(message = "moduleId 不能为空")
    private Long moduleId;

    /**
     * 属性名称
     */
    @NotBlank(message = "name 不能为空")
    private String name;

    /**
     * 属性唯一标识
     */
    @NotBlank(message = "code 不能为空")
    private String code;

    /**
     * 公式（中文展示）
     */
    @NotBlank(message = "formulaZh 不能为空")
    private String formulaZh;

    /**
     * 公式
     */
    @NotBlank(message = "formula 不能为空")
    private String formula;

    /**
     * 预变量，json格式
     */
    @NotBlank(message = "variable 不能为空")
    @JsonDeserialize(using = JSON2StringDeserializer.class)
    @JsonSerialize(using = String2JSONSerializer.class)
    private String variable;

    /**
     * 逻辑删除（0-正常，1-删除）
     */
    @JsonIgnore
    @TableLogic(value = "false",delval = "true")
    private Boolean dataStatus;

    /**
     * 数据创建用户id
     */
    @JsonIgnore
    private Long userId;

    /**
     * 数据创建用户部门id
     */
    @JsonIgnore
    private Long deptId;

    /**
     * 数据创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 数据更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
