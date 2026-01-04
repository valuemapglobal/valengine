package com.value.data.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Vida
 * @date 2023年08月14日 11:30
 * @description
 */
@Data
public class CategoryListPageVO implements Serializable {
    private static final long serialVersionUID=1L;

    private Long id;

    /**
     * 分析指标标识
     */
    private String analysisIndicatorsNo;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 分类code
     */
    private String categoryCode;

    /**
     * 分析指标类型
     */
    private String categoryType;

    /**
     * 版本号
     */
    private String interfaceVersion;


    /**
     * 关联的元数据
     */
    private List<String> metadatas;

    /**
     * 关联的特征变量
     */
    private List<String> variables;

}
