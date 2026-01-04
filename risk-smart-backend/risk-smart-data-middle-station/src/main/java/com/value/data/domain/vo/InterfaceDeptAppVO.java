package com.value.data.domain.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

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
