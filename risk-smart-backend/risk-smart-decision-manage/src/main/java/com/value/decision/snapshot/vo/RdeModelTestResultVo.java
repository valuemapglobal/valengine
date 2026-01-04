package com.value.decision.snapshot.vo;

import com.value.decision.snapshot.domain.RdeModelTestResult;
import lombok.Data;

import java.util.List;

/**
 * @author Administrator
 * @since 2023/5/8
 */
@Data
public class RdeModelTestResultVo {
    /**
     * 命中条数
     */
    private Integer hitNumber;

    /**
     * 集合总条数
     */
    private Integer total;

    /**
     * 策略集合
     */
    private List<RdeModelTestResult> rdeModelTestResultList;
}
