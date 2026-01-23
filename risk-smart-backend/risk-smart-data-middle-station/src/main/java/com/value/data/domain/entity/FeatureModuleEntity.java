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
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 特征变量模块表
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("feature_module")
public class FeatureModuleEntity implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 模块名称
     */
    @NotEmpty(message = "name 不能为空")
    private String name;

    /**
     * 模块唯一标识
     */
    @NotEmpty(message = "code 不能为空")
    private String code;

    /**
     * 模块类型（0-对象，1-集合）
     */
    @NotNull(message = "type 不能为空")
    private Integer type;

    /**
     * 数据分类（0-企业，1-个人，2-实体资产，3-供应链）
     */
    @NotNull(message = "dataType 不能为空")
    private Integer dataType;

    private String remark;

    /**
     * 关联元数据的接口编号，json数组格式
     */
    @NotBlank(message = "metadata 不能为空")
    @JsonDeserialize(using = JSON2StringDeserializer.class)
    @JsonSerialize(using = String2JSONSerializer.class)
    private String metadata;

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
