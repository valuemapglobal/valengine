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
 * 
 * </p>
 *
 * @author Vida
 * @since 2023-12-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("interface_dept_app")
public class InterfaceDeptApp implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 自增主键
     */
      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 部门id
     */
    private Long deptId;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * appkey
     */
    private String appKey;

    /**
     * secret
     */
    private String secret;

    /**
     * 访问路径
     */
    private String url;

    /**
     * 缓存时间（天）
     */
    private Integer cacheTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 逻辑删除（0-未删除，1-已删除）
     */
    private Boolean dataStatus;


}
