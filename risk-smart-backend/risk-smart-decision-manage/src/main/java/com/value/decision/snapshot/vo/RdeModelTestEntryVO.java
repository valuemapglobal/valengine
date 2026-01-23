package com.value.decision.snapshot.vo;

import lombok.Data;

import java.util.List;

/**
 * @author hcp
 * 模型测试入参
 * @date 2023年05月08日 10:56
 */
@Data
public class RdeModelTestEntryVO {
    /**
     * 流水ID
     */
    private String serialNumber;
    /**
     * 模型id
     */
    private List<Integer> modelIdList;

    /**
     * 项目code
     */
    private String projectCode;

    /**
     * 页面标识 分类为1,规则为0
     */
    private String ruleCode;

    /**
     * 职业
     */
    private String profession;

    /**
     * 公司名称
     */
    private String companyName;

    /**
     * 当前记录起始索引
     */
    private Integer pageNum;

    /**
     * 每页显示记录数
     */
    private Integer pageSize;

}
