package com.value.data.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class InterfaceChargingVo {
    private Integer total;
    private List<InterfaceChargingListVo> ListVo;
    private BigDecimal count;

    private BigDecimal todayChargingCount;
    private String todayUpdateTime;
    private BigDecimal allChargingCount;
    private String allUpdateTime;


}
