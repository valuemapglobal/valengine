package com.value.data.domain.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.List;

@Data
public class InterfacePermissionsDTO implements Serializable {
    /**
     * 接口管理唯一标识
     */
    @NotNull(message = "manageNos 参数错误")
    private List<@Pattern(regexp = "^[a-zA-Z0-9_:]{1,64}$", message = "smanageNos只允许英文、数字、下划线、冒号，且长度不超过64位") String> manageNos;

    @NotNull(message = "userId 参数错误")
    private Long userId;
}
