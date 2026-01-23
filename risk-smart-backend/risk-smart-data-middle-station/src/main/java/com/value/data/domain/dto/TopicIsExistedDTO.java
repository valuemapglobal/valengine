package com.value.data.domain.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 验证主题是否存在数据传输对象
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@Data
public class TopicIsExistedDTO {
    @NotBlank(message = "topicName 参数错误")
    @Size(min = 1, max = 30, message = "topicName应该在1-30字符之间")
    private String topicName;
}
