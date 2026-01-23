package com.value.decision.model.decisionmanage.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * <p>
 * 数据调用表
 * </p>
 *
 * @author hc
 * @since 2023-11-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DataCalling extends Model {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 数据中台接口唯一键
     */
    @TableField("manageNo")
    private String manageNo;

    /**
     * 数据类型
     */
    @TableField("interfaceType")
    private String interfaceType;

    /**
     * 模型应用
     */
    @TableField("modelApplication")
    private Integer modelApplication;

    /**
     * 调用状态
     */
    @TableField("callStatus")
    private String callStatus;

    /**
     * 交易号
     */
    @TableField("orderNo")
    private String orderNo;

    /**
     * 创建时间
     */
    @TableField("createTime")
    private LocalDateTime createTime;

    /**
     * 返还数据
     */
    @TableField("responseBody")
    private String responseBody;
  }
