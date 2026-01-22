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

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 特征变量属性表
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("feature_attribute")
public class FeatureAttributeEntity implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 特征变量名称
     */
    @NotBlank(message = "name 不能为空")
    private String name;

    /**
     * 参数名
     */
    @NotBlank(message = "code 不能为空")
    private String code;

    /**
     * 所属模块的id
     */
    @NotNull(message = "moduleId 不能为空")
    private Long moduleId;

    /**
     * 特征变量类型（0-字符型，1-小数，2-数值）
     */
    @NotNull(message = "type 不能为空")
    private Integer type;

    /**
     * 是否固定阈值（0-是，1-否，2-自定义）
     */
    @NotNull(message = "thresholdType 不能为空")
    @Min(value = 0, message = "thresholdType 非法")
    @Max(value = 2, message = "thresholdType 非法")
    private Integer thresholdType;

    /**
     * 阈值配置
     */
    @JsonDeserialize(using = JSON2StringDeserializer.class)
    @JsonSerialize(using = String2JSONSerializer.class)
    private String configThreshold;

    /**
     * 计算配置
     */
    @JsonDeserialize(using = JSON2StringDeserializer.class)
    @JsonSerialize(using = String2JSONSerializer.class)
    private String configCompute;

    /**
     * 脚本配置
     */
    private String configScript;

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
