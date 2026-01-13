package com.risksmart.common.core.constant;

import java.math.BigDecimal;

/**
 * 授信额度计算公式常量
 * 用于硬编码的财务数据额度计算（遗留逻辑）
 *
 * @author System
 * @since 2023-08-08
 */
public class QuotaCalculationConstants {

    /**
     * 营业收入系数
     */
    public static final BigDecimal REVENUE_FACTOR = new BigDecimal("0.5");

    /**
     * 净利润系数
     */
    public static final BigDecimal PROFIT_FACTOR = new BigDecimal("7");

    /**
     * 所有者权益系数
     */
    public static final BigDecimal EQUITY_FACTOR = new BigDecimal("0.5");

    /**
     * 资产负债率阈值
     */
    public static final BigDecimal RATIO_THRESHOLD = new BigDecimal("0.5");

    /**
     * 资产负债率调节系数
     */
    public static final BigDecimal RATIO_ADJUSTMENT = new BigDecimal("2");

    /**
     * 百分比转换除数
     */
    public static final BigDecimal PERCENT_DIVISOR = new BigDecimal("100");

    /**
     * 计算精度（小数位）
     */
    public static final int SCALE_PRECISION = 4;

    /**
     * 结果精度（小数位）
     */
    public static final int RESULT_SCALE = 2;

    /**
     * 私有构造函数，防止实例化
     */
    private QuotaCalculationConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
