package com.value.data.domain.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class GetPermissionsDTO implements Serializable {

    @NotNull(message = "userId 参数错误")
    private Long userId;
}
