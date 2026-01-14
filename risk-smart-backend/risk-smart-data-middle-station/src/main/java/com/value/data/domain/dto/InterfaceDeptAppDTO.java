package com.value.data.domain.dto;

import com.risksmart.common.core.constant.ValidatedType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class InterfaceDeptAppDTO implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 自增主键
     */
    @NotNull(message = "id 不能为空",groups = {ValidatedType.delete.class,ValidatedType.update.class})
    private Long id;
    /**
     * 部门id
     */
    @NotNull(message = "deptId 不能为空",groups = {ValidatedType.add.class})
    private Long deptId;

    /**
     * 部门名称
     */
    @NotBlank(message = "deptName 不能为空",groups = {ValidatedType.add.class})
    private String deptName;

    /**
     * appkey
     */
    @NotBlank(message = "appKey 不能为空",groups = {ValidatedType.add.class})
    private String appKey;

    /**
     * secret
     */
    @NotBlank(message = "secret 不能为空",groups = {ValidatedType.add.class})
    private String secret;

    /**
     * 访问路径
     */
    @NotBlank(message = "url 不能为空",groups = {ValidatedType.add.class})
    private String url;

    /**
     * 缓存时间（天）
     */
    @NotNull(message = "cacheTime 不能为空",groups = {ValidatedType.add.class})
    private Integer cacheTime;


    @NotNull(message = "pageNum 不能为空",groups = {ValidatedType.query.class})
    @Min(value = 1,message = "pageNum 不能小于1",groups = {ValidatedType.query.class})
    private Integer pageNum;
    @NotNull(message = "pageSize 不能为空",groups = {ValidatedType.query.class})
    @Min(value = 1,message = "pageSize 不能小于1",groups = {ValidatedType.query.class})
    private Integer pageSize;
}
