package com.value.decision.model.decisionmanage.util;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * 参数映射工具类
 * 实现标准字段名到接口字段名的智能映射
 *
 * @author Austin
 * @since 2025-10-30
 */
@Slf4j
public class ParameterMappingUtil {

    /**
     * 标准字段映射规则
     * Key: 标准字段名（前端使用）
     * Value: 该标准字段可能对应的接口字段名变体列表
     */
    private static final Map<String, Set<String>> STANDARD_FIELD_MAPPING = new HashMap<>();

    static {
        // 企业名称的所有可能变体
        STANDARD_FIELD_MAPPING.put("companyName", new HashSet<>(Arrays.asList(
                "companyName", "company_name", "c_name", "cname", "cName"
        )));

        // 企业统一社会信用代码的所有可能变体
        STANDARD_FIELD_MAPPING.put("creditCode", new HashSet<>(Arrays.asList(
                "creditCode", "credit_code"
        )));

        // 企业唯一标识key的所有可能变体
        STANDARD_FIELD_MAPPING.put("ckey", new HashSet<>(Arrays.asList(
                "ckey", "c_key", "cKey"
        )));

        // 订单号的可能变体（前端可能传 orderId 或 orderNo）
        STANDARD_FIELD_MAPPING.put("orderNo", new HashSet<>(Arrays.asList(
                "orderNo", "orderId"
        )));
    }

    /**
     * 根据接口需要的字段，从标准参数中映射出对应的参数
     *
     * @param standardParams  前端传入的标准参数 (例如: {companyName: "腾讯", creditCode: "xxx"})
     * @param requiredFields  接口需要的字段列表 (例如: ["c_name", "ckey", "orderNo"])
     * @param taskNo          任务编号（用于orderNo相关字段）
     * @return 映射后的参数 (例如: {c_name: "腾讯", ckey: "xxx", orderNo: "TASK123"})
     */
    public static Map<String, Object> mapParameters(
            Map<String, Object> standardParams,
            List<String> requiredFields,
            String taskNo) {

        Map<String, Object> mappedParams = new HashMap<>();

        for (String requiredField : requiredFields) {
            Object value = findValueForRequiredField(requiredField, standardParams, taskNo);

            if (value != null) {
                mappedParams.put(requiredField, value);
            } else {
                log.warn("接口需要字段[{}]，但在标准参数中未找到对应的值", requiredField);
            }
        }

        return mappedParams;
    }

    /**
     * 为接口需要的字段查找对应的值
     *
     * @param requiredField   接口需要的字段名
     * @param standardParams  标准参数
     * @param taskNo          任务编号
     * @return 对应的值，如果找不到则返回null
     */
    private static Object findValueForRequiredField(
            String requiredField,
            Map<String, Object> standardParams,
            String taskNo) {

        // 1. 特殊处理：订单号相关字段，直接使用taskNo
        Set<String> orderNoVariants = STANDARD_FIELD_MAPPING.get("orderNo");
        if (orderNoVariants != null && orderNoVariants.contains(requiredField)) {
            return taskNo;
        }

        // 2. 直接匹配：标准参数中有完全相同的字段名
        if (standardParams.containsKey(requiredField)) {
            return standardParams.get(requiredField);
        }

        // 3. 通过映射规则查找：接口字段是某个标准字段的变体
        for (Map.Entry<String, Set<String>> entry : STANDARD_FIELD_MAPPING.entrySet()) {
            String standardFieldName = entry.getKey();
            Set<String> variants = entry.getValue();

            // 如果接口需要的字段在某个标准字段的变体列表中
            if (variants.contains(requiredField)) {
                // 从标准参数中获取该标准字段的值
                if (standardParams.containsKey(standardFieldName)) {
                    return standardParams.get(standardFieldName);
                }

                // 尝试从标准参数的其他变体中查找
                for (String variant : variants) {
                    if (standardParams.containsKey(variant)) {
                        return standardParams.get(variant);
                    }
                }
            }
        }

        // 4. 未找到映射关系
        return null;
    }

    /**
     * 双向字段映射：从processEntry中查找requiredField的任何变体字段的值
     * 用于支持前端传递任意变体字段名的场景
     *
     * @param requiredField   接口需要的字段名
     * @param processEntry    前端传递的所有参数（可能包含该字段的变体）
     * @return 找到的变体字段值，如果找不到则返回null
     *
     * 示例：
     * - 接口需要 companyName，前端传了 c_name → 返回 c_name 的值
     * - 接口需要 c_name，前端传了 companyName → 返回 companyName 的值
     * - 接口需要 cname，前端传了 company_name → 返回 company_name 的值
     */
    public static Object findValueFromVariants(String requiredField, Map<String, Object> processEntry) {
        // 1. 查找 requiredField 属于哪个标准字段的变体组
        for (Map.Entry<String, Set<String>> entry : STANDARD_FIELD_MAPPING.entrySet()) {
            Set<String> variants = entry.getValue();

            // 如果 requiredField 在这个变体组中
            if (variants.contains(requiredField)) {
                // 遍历该变体组的所有变体，在 processEntry 中查找
                for (String variant : variants) {
                    if (processEntry.containsKey(variant)) {
                        // 找到匹配的变体字段，返回其值
                        log.debug("字段映射: {} <- {} (值: {})", requiredField, variant, processEntry.get(variant));
                        return processEntry.get(variant);
                    }
                }
            }
        }

        // 2. 未找到任何变体匹配
        return null;
    }

}
