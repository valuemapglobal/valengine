package com.value.data.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 接口日志表
 * </p>
 *
 * @author bing
 * @since 2023-08-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("interface_log")
public class InterfaceLog implements Serializable {

    private static final long serialVersionUID=1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 订单号
     */
    private String orderId;
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
    private LocalDateTime accessTime;

    /**
     * 响应时间
     */
    private LocalDateTime resultTime;

    /**
     * 响应总耗时
     */
    private Double totalTime;

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
     * 计费标识
     */
    private Integer chargingFlag;
    /**
     * 部门Id
     */
    private Integer deptId;


}
