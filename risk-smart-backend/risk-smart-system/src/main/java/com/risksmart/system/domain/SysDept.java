package com.risksmart.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 部门表 sys_dept
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@Data
@TableName("sys_dept")
public class SysDept implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 部门ID */
    @TableId(type = IdType.AUTO)
    private Long deptId;

    /** 父部门ID */
    private Long parentId;

    /** 祖级列表 */
    private String ancestors;

    /** 部门名称 */
    private String deptName;

    /** 显示顺序 */
    private Integer orderNum;

    /** 负责人 */
    private Long leader;

    /** 联系电话 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 部门状态:0正常,1停用 */
    private String status;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    /** 创建者 */
    private String createBy;

    /** 创建时间 */
    private Date createTime;

    /** 更新者 */
    private String updateBy;

    /** 更新时间 */
    private Date updateTime;

    /** 子部门 */
    @TableField(exist = false)
    private List<SysDept> children = new ArrayList<>();
}
