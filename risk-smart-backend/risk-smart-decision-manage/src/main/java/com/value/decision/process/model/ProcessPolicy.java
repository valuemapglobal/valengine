package com.value.decision.process.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 流程策略表
 * </p>
 *
 * @author hc
 * @since 2023-08-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ProcessPolicy extends Model {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 流程策略模型名称
     */
    @NotBlank(message = "流程策略名称不可为空")
    @Length( max = 30, message = "流程策略名称不可超过30")
    private String processStrategy;

    /**
     * 产品名称
     */
//    @NotBlank(message = "产品名称不可为空")
    private String productName;

    /**
     * 业务场景
     */
    @NotBlank(message = "业务场景不可为空")
    private String businessCode;

    /**
     * 是否使用 0关闭,1开启
     */
    private Integer useIf;

    /**
     * 描述
     */
    @Length(max = 200, message = "描述不可超过200")
    private String content;

    /**
     * 申请时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime applicationTime;

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

    //策略模型
    @TableField(exist = false)
    private List<Map<String,Object>> mapList;

    private Integer dataStatus;

    @TableField(exist = false)
    private Integer pageNum;

    @TableField(exist = false)
    private Integer pageSize;
    @TableField(exist = false)
    private String startTime;
    @TableField(exist = false)
    private String endTime;

    /**
     * 产品id
     */
    private Integer productId;

}
