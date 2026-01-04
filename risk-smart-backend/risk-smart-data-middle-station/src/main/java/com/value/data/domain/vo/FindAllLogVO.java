package com.value.data.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 获取日志信息
 */
@Data
public class FindAllLogVO {

    /**
     * 接口管理唯一标识
     */
    private String interfaceManageNo;

    /**
     * 接口日志唯一标识
     */
    private String interfaceLog;

    /**
     * 接口名称
     */
    private String interfaceName;

    //接口中文名称
    private String interfaceNameZh;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户部门
     */
    private String userDept;

    /**
     * 调用时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime accessTime;

    /**
     * 响应时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime resultTime;

    /**
     * 调用地址
     */
    private String ip;

    /**
     * 请求头
     */
    private String requestHeaders;

    /**
     * 请求方法
     */
    private String requestMethod;

    /**
     * 响应code
     */
    private Integer code;

    /**
     * 请求参数
     */
    private String param;

    /**
     * 响应结果
     */
    private String result;

    /**
     * 异常msg
     */
    private String errorMsg;

    /**
     * 异常信息
     */
    private String error;

    /**
     * 创建者
     */
    private String createBy;
}
