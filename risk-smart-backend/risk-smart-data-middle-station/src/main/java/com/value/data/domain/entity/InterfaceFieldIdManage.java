package com.value.data.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 接口参数管理表
 * </p>
 *
 * @author bing
 * @since 2023-08-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("interface_field_id_manage")
public class InterfaceFieldIdManage implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 接口参数唯一标识
     */
    private String interfaceFieldIdManage;

    /**
     * 接口管理唯一标识
     */
    private String interfaceManageNo;

    /**
     * 接口编号
     */
    private String interfaceNo;

    /**
     * 参数名称
     */
    private String interfaceFieldIdName;

    /**
     * 参数别名
     */
    private String interfaceFieldIdAlias;

    /**
     * 参数说明
     */
    private String interfaceFieldIdDescription;

    /**
     * 参数类型(0-入参，1-出参)
     */
    private Integer interfaceFieldIdType;

    /**
     * 数据类型(0-数值，1-字符串，2-日期，3-对象，4-数组，5-文件，6-布尔，7-小数)
     */
    private Integer interfaceFieldIdDataType;

    /**
     * 入参必填项(0-是，1-否)
     */
    private Integer interfaceFieldIdRequired;

    /**
     * 接口序号
     */
    private Integer interfaceFieldIdIndex;

    /**
     * 参数备注
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String interfaceFieldIdRemark;

    /**
     * 默认值
     */
    private String interfaceFieldIdDefultValue;

    /**
     * 父级属性
     */
    private String interfaceFieldIdFather;

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
    @TableLogic
    private Boolean dataStatus;

    private Long userId;
    private Long deptId;
}
