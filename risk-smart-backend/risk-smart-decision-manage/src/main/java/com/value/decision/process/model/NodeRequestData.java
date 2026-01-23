package com.value.decision.process.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * <p>
 * 节点请求数据存储表
 * </p>
 *
 * @author hc
 * @since 2023-08-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class NodeRequestData extends Model {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 任务编号
     */
    private String taskNumber;

    /**
     * 策略导航标识
     */
    private String ruleCode;

    /**
     * 节点标识
     */
    private Integer nodeId;

    /**
     * 入参请求数据
     */
    private String requestData;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime updateTime;

    /**
     * 部门ID
     */
    private Integer deptId;

    /**
     * 用户ID
     */
    private Integer userId;

    //请求结果
    private String resultData;


}
