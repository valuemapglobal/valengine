package com.value.decision.version.dto;

import lombok.Data;

@Data
public class VersionControlDTO {

    /**
     * 当前模型id
     */
    private Integer modelId;

    /**
     * 当前展示版本
     */
    private String newVersion = "-";

    /**
     * 当前模型使用版本号
     */
    private String userVersion = "-";

    /**
     * 是否为冠军版本标识 1是 0为挑战者版本
     */
    private Integer championVersion = 0;

}
