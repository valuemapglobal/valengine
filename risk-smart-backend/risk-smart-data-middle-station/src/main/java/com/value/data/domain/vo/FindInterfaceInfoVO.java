package com.value.data.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 获取接口信息
 */
@Data
public class FindInterfaceInfoVO implements Serializable {

    private static final long serialVersionUID=1L;

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
     * 0-Json，1-from-data
     */
    private Integer paramType;

    /**
     * 0-POST, 1-GET
     */
    private Integer requestType;


    /**
     * 0 开启，1 关闭
     */
    private Integer interfaceOn;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;


    /**
     * 版本号
     */
    private String interfaceVersion;

    /**
     * 返回值类型（0-对象，5-数组）
     */
    private Integer returnType;
    /**
     * 单价
     */
    private Float price;

    /**
     * 超时时间(毫秒)
     */
    private Integer timeout;
}
