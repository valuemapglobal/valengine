package com.value.data.common.utils;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * 业务配置属性
 */
@Data
@Component
@Validated
@ConfigurationProperties(prefix = "business")
public class BusinessProperties {

    /**
     * API Token 解密密码（必须在配置文件中设置）
     */
    @NotBlank(message = "business.apiTokenDecryptPassword 未配置")
    private String apiTokenDecryptPassword;

}
