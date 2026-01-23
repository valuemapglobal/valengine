package com.value.decision.snapshot.vo;

import lombok.Data;

import java.util.List;

@Data
public class RuleReleaseVO {

    private List<Integer> modelIdList;

    private String projectCode;

    private String ruleCode;

    private List<String> categoryType;

    private String industryName;

    private String businessCode;
}
