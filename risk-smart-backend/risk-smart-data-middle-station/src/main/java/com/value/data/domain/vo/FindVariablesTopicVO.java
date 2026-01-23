package com.value.data.domain.vo;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

@Data
public class FindVariablesTopicVO implements Serializable {
    private static final long serialVersionUID=1L;

    /**
     * 特征变量标识
     */
    private String no;

    /**
     * 主题名称
     */
    private String topicName;

    /**
     * 主题类型
     */
    private String topicType;

    /**
     * 包名称
     */
    private String topicPackage;

    @JSONField(alternateNames = {"no","characteristicVariablesNo"})
    public void setNo(String no) {
        this.no = no;
    }
}
