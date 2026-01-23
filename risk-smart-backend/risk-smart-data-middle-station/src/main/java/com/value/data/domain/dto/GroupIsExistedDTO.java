package com.value.data.domain.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 验证分组是否存在数据传输对象
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@Data
public class GroupIsExistedDTO {
    @NotBlank(message = "no 参数错误")
    @Pattern(regexp = "^[a-zA-Z0-9_:]{1,64}$", message = "no只允许英文、数字、下划线、冒号，且长度不超过64位")
    private String no;

    /**
     *  分组名称
     */
    @NotBlank(message = "groupName 参数错误")
    @Size(min = 1, max = 30, message = "groupName应该在1-30字符之间")
    private String groupName;
}
