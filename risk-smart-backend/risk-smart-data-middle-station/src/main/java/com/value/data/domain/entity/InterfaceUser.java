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
 * @author luke
 * @since 2023-10-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("interface_user")
public class InterfaceUser implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO )
    private Integer id;

    /**
     * 用户Id
     */
    private Integer userId;

    /**
     * appKey
     */
    private String appKey;

    /**
     * secret
     */
    private String secret;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
