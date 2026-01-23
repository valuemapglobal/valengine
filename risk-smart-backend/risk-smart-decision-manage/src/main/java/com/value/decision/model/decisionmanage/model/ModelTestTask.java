package com.value.decision.model.decisionmanage.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.time.LocalDateTime;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 模型测试任务表
 * </p>
 *
 * @author hc
 * @since 2024-11-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ModelTestTask extends Model {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 测试任务号
     */
    private String taskNo;

    /**
     * 模型编号
     */
    private Integer modelId;

    /**
     * 模型版本号
     */
    private String modelVerson;

    /**
     * 产品编号
     */
    private Integer projectCode;

    /**
     * 业务场景导航标识
     */
    private Integer businessCode;

    /**
     * 策略类型导航标识
     */
    private Integer ruleCode;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 测试drl文件
     */
    private String testDrl;

    /**
     * 测试状态  0初始化 1运行中 2成功 3失败
     */
    private Integer testStatus;

    /**
     * 数据状态  0正常 1删除
     */
    private Long dataStatus;

    /**
     * 部门ID
     */
    private Integer deptId;

    /**
     * 用户ID
     */
    private Integer userId;


}
