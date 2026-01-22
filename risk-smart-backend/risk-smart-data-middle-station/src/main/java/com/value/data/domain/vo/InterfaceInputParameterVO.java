package com.value.data.domain.vo;

import lombok.Data;

import java.util.List;

/**
 * 接口入参参数视图对象
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@Data
public class InterfaceInputParameterVO {
    private ParameterInfo parameterInfo;
    private List<InterfaceInfo> interfaceInfoList;
    @Data
    public static class ParameterInfo{
        private String nameEn;
        private String nameZh;
        private Integer type;
    }
    @Data
    public static class InterfaceInfo{
        private String manageNo;
        private String interfaceNo;
    }
}
