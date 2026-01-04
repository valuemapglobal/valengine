package com.value.decision.model.rdenew.vo;

import java.util.List;

/**
 * 树级对象
 */
public class RdeRiskVariableThemeVO {

    private String label;
    private String value;
    private String code;
    private Integer id;
    private Integer type;
    private List<RdeRiskVariableThemeVO> children;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<RdeRiskVariableThemeVO> getChildren() {
        return children;
    }

    public void setChildren(List<RdeRiskVariableThemeVO> children) {
        this.children = children;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
