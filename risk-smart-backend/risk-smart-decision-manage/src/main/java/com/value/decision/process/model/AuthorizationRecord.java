package com.value.decision.process.model;

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
 * 
 * </p>
 *
 * @author luke
 * @since 2023-12-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("authorization_record")
public class AuthorizationRecord implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 主键
     */
      @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 客户名称
     */
    private String name;

    /**
     * 身份证号码
     */
    private String number;

    /**
     * 手机号码
     */
    private String mobile;

    /**
     * 授信状态 1 授信中  2 通过 3 拒绝
     */
    private Integer creditStatus;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 授权成功时间
     */
    private LocalDateTime authorizationTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     *  0-正常 1-过期
     */
    private Integer dataStatus;

    /**
     * 策略ID
     */
    //流程id
    private Integer processStrategyId;

    /**
     * 订单号
     */
    private String orderNo;

    private Integer userId;

    private Integer deptId;


}
