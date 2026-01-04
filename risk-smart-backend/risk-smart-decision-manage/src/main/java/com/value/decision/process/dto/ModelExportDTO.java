package com.value.decision.process.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ModelExportDTO {
    /**
     * 业务场景
     */
    @ApiModelProperty(value = "业务场景")
    private String businessName;

    /**
     * 描述
     */

    @ApiModelProperty(value = "描述")
    private String content;

    @ApiModelProperty(value = "今日异常总数")
    private Integer todayExceptionCount;
    @ApiModelProperty(value = "异常总数")
    private Integer exceptionCount;
    @ApiModelProperty(value = "今日运行总数")
    private Integer todayRuntimeCount;
    @ApiModelProperty(value = "运行总数")
    private Integer runtimeCount;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private String createTime;

}
