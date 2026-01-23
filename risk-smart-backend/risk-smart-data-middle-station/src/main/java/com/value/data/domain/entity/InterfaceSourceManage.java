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
 * 接口供应商管理表
 * </p>
 *
 * @author bing
 * @since 2023-08-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("interface_source_manage")
public class InterfaceSourceManage implements Serializable {

    private static final long serialVersionUID=1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 来源唯一标识
     */
    private String interfaceSourceNo;

    /**
     * 数据名称
     */
    private String dataName;

    /**
     * 来源
     */
    private String source;

    /**
     * 网址
     */
    private String webLink;

    /**
     * 负责人
     */
    private String adminName;

    /**
     * 联系方式
     */
    private String contactDetails;

    /**
     * 0 元数据，1 特征变量，2 分析指标
     */
    private Integer interfaceDataType;

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

    @TableField(exist = false)
    List<InterfaceManage> interfaceManageList;
}
