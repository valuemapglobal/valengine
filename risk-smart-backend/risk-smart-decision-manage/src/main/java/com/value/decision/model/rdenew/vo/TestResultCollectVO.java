package com.value.decision.model.rdenew.vo;

import lombok.Data;

import java.util.List;

@Data
public class TestResultCollectVO {

    /**
     * 流水总条数
     */
    private Integer totalNum;

    /**
     * 未命中分类数量
     */
    private Integer nonum;

    /**
     * 结果数据
     */
    private List<TestResultVO> testResultVOList;

    /**
     * 列表总条数
     */
    private Integer collectNum;

}
