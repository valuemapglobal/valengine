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
 * 风险变量分组明细表
 *
 * @author Vida
 * @since 2023-08-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class RdeRiskVariableRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String code;
    private String name;
    private String type;
    private String remark;
    private Integer createUserId;
    private Date createTime;
    private Date updateTime;

    @TableLogic
    private Integer dataStatus;

    private String groupNo;
    private String recordNo;
    private String parentNo;
    private String interfaceFieldIdManage;
    private Long userId;
    private Long deptId;
}
