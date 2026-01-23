package com.value.data.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 风险变量主题表
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class RdeRiskVariableTheme implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String name;
    private String keycode;
    private String packageType;
    private Integer createUserId;
    private Date createTime;
    private Date updateTime;

    @TableLogic
    private Integer dataStatus;

    private String themeNo;
    private String interfaceSourceNo;
    private Long userId;
    private Long deptId;
}
