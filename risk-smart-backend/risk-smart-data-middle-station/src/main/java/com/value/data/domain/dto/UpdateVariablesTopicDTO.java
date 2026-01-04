package com.value.data.domain.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * 更新主题
 */
@Data
public class UpdateVariablesTopicDTO implements Serializable {
    private static final long serialVersionUID=1L;

    /**
     * 元数据标识
     */
    @NotNull(message = "metaNo 参数错误")
    private List<@Pattern(regexp = "^[a-zA-Z0-9_:]{1,64}$", message = "metaNo只允许英文、数字、下划线、冒号，且长度不超过64位") String> metaNo;

    /**
     * 特征变量标识
     */
    @NotBlank(message = "no 参数错误")
    @Pattern(regexp = "^[a-zA-Z0-9_:]{1,64}$", message = "no只允许英文、数字、下划线、冒号，且长度不超过64位")
    private String no;

    /**
     * 主题名称
     */
    @NotBlank(message = "topicName 参数错误")
    @Size(min = 1, max = 30, message = "topicName应该在1-30字符之间")
    private String topicName;

    /**
     * 主题类型
     */
    @NotBlank(message = "topicType 参数错误")
    @Size(min = 1, max = 30, message = "topicType应该在1-30字符之间")
    private String topicType;

    /**
     * 主题包名称
     */
    @NotBlank(message = "topicPackage 参数错误")
    @Pattern(regexp = "^[a-z.]{1,64}$", message = "topicPackage只允许英文、点，且长度不超过64位")
    private String topicPackage;

    @Pattern(regexp = "^[a-zA-Z0-9_:]{1,64}$", message = "interfaceSourceNo 只允许英文、数字、下划线、冒号，且长度不超过64位")
    private String interfaceSourceNo;
}
