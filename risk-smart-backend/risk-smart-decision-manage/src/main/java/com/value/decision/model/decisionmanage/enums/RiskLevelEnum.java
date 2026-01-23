package com.value.decision.model.decisionmanage.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 风险等级枚举
 *
 * @author Austin
 * @since 2025-10-30
 */
@Getter
@AllArgsConstructor
public enum RiskLevelEnum {

    /**
     * 低风险
     */
    LOW(1, "低风险"),

    /**
     * 中低风险
     */
    MEDIUM_LOW(2, "中低风险"),

    /**
     * 中风险
     */
    MEDIUM(3, "中风险"),

    /**
     * 中高风险
     */
    MEDIUM_HIGH(4, "中高风险"),

    /**
     * 高风险
     */
    HIGH(5, "高风险");

    /**
     * 风险等级编码
     */
    private final Integer code;

    /**
     * 风险等级描述
     */
    private final String description;

    /**
     * 根据编码获取风险等级描述
     *
     * @param code 风险等级编码
     * @return 风险等级描述，如果编码不存在则返回null
     */
    public static String getDescriptionByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (RiskLevelEnum level : values()) {
            if (level.getCode().equals(code)) {
                return level.getDescription();
            }
        }
        return null;
    }

    /**
     * 根据编码获取枚举
     *
     * @param code 风险等级编码
     * @return 风险等级枚举，如果编码不存在则返回null
     */
    public static RiskLevelEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (RiskLevelEnum level : values()) {
            if (level.getCode().equals(code)) {
                return level;
            }
        }
        return null;
    }
}
