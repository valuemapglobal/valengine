package com.value.data.domain.dto;

import lombok.Data;

@Data
public class AddExceptionLogDTO {

    /**
     * 接口名称
     */
    private String interfaceName;

    /**
     * 操作类型（0-新增、1-修改、2-删除、3-授权、4-导出、5-导入、6-强退、7-生成代码、8-清空数据）
     */
    private Integer operationType;

    /**
     * 请求类型 （0-post,1-get）
     */
    private Integer requestType;

    /**
     * 操作人员
     */
    private String operator;
    /**
     * 请求参数
     */
    private String requestBody;

    /**
     * 返回参数
     */
    private String responseBody;

}
