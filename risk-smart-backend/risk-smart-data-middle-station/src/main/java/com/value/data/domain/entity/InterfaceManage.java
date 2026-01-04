package com.value.data.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 接口管理表
 * </p>
 *
 * @author bing
 * @since 2023-08-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("interface_manage")
public class InterfaceManage implements Serializable {

    private static final long serialVersionUID=1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 接口供应商唯一标识
     */
    private String interfaceSourceNo;

    /**
     * 接口管理唯一标识
     */
    private String interfaceManageNo;

    /**
     * 接口编号
     */
    private String interfaceNo;

    /**
     * 接口名称
     */
    private String interfaceName;

    /**
     * 接口类型 0个人接口，1 企业接口，2 司法接口
     */
    private Integer interfaceType;

    /**
     * 接口描述
     */
    private String interfaceDescription;

    /**
     * 接口特点
     */
    private String interfaceFeatures;

    /**
     * 接口场景
     */
    private String interfaceScenes;

    /**
     * 接口覆盖体量
     */
    private String interfaceCoveringVolume;

    /**
     * 接口标签
     */
    private String interfaceTag;

    /**
     * 接口网址
     */
    private String interfaceLink;

    /**
     * 接口配额
     */
    private Integer interfaceQuota;

    /**
     * 接口序号
     */
    private Integer interfaceIndex;

//    /**
//     * 0 元数据，1 特征变量
//     */
//    private Integer interfaceDataType;

    /**
     * 0 开启，1 关闭
     */
    private Integer interfaceOn;

    /**
     * 0-Json，1-from-data
     */
    private Integer paramType;

    /**
     * 0-POST, 1-GET
     */
    private Integer requestType;

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

    /**
     * 版本号
     */
    private String interfaceVersion;

    /**
     * 返回值类型（0-对象，1-数组）
     */
    private Integer returnType;

    /**
     * 单价
     */
    private Float price;

    private Long userId;
    private Long deptId;

    /**
     * 超时时间(毫秒)
     */
    private Integer timeout;

    @TableField(exist = false)
    private List<InterfaceFieldIdManage> fields;
}
