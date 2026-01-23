package com.value.data.domain.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 接口部门应用视图对象
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class InterfaceDeptAppVO implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 自增主键
     */
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
}
