package com.value.data.domain.vo;

import lombok.Data;

import java.util.List;

/**
 * @author Vida
 * @date 2023年12月19日 11:11
 * @description
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
