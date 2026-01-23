package com.value.decision.model.decisionmanage.util;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 接口字段缓存工具类
 * 实现懒加载+带过期时间的缓存，避免重复调用接口管理API
 * 同时保证缓存数据与数据中台配置的一致性
 *
 * @author Austin
 * @since 2025-10-30
 */
@Component
@Slf4j
public class InterfaceFieldCache {

    @Value("${service.interfaceManage.fieldsUrl}")
    private String fieldsUrl;

    /**
     * 缓存过期时间（毫秒）
     * 固定24小时，配合接口调用失败自动刷新机制确保缓存一致性
     */
    private static final long CACHE_TTL = 24 * 60 * 60 * 1000L;  // 24小时

    /**
     * 接口输入字段缓存
     * Key: manageNo (接口管理编号)
     * Value: 缓存项（包含字段列表和过期时间）
     */
    private static final Map<String, CacheEntry> INPUT_FIELDS_CACHE = new ConcurrentHashMap<>();

    /**
     * 缓存项（包含数据和过期时间）
     */
    @Data
    @AllArgsConstructor
    private static class CacheEntry {
        private List<String> fields;
        private long expireTime;

        public boolean isExpired() {
            return System.currentTimeMillis() > expireTime;
        }
    }

    /**
     * 获取接口需要的输入字段列表（带缓存和过期检查）
     *
     * @param manageNo 接口管理编号
     * @return 接口需要的输入字段名列表
     */
    public List<String> getInputFields(String manageNo) {
        CacheEntry cacheEntry = INPUT_FIELDS_CACHE.get(manageNo);

        // 缓存不存在或已过期，重新查询
        if (cacheEntry == null || cacheEntry.isExpired()) {
            if (cacheEntry != null) {
                log.info("接口[{}]缓存已过期（24小时），重新查询字段配置", manageNo);
            }
            List<String> fields = fetchInputFieldsFromApi(manageNo);
            long expireTime = System.currentTimeMillis() + CACHE_TTL;
            INPUT_FIELDS_CACHE.put(manageNo, new CacheEntry(fields, expireTime));
            return fields;
        }

        // 缓存命中且未过期
        return cacheEntry.getFields();
    }

    /**
     * 强制刷新指定接口的缓存（用于接口配置变更后立即刷新）
     *
     * @param manageNo 接口管理编号
     * @return 刷新后的字段列表
     */
    public List<String> refreshCache(String manageNo) {
        log.info("强制刷新接口[{}]的字段缓存", manageNo);
        INPUT_FIELDS_CACHE.remove(manageNo);
        return getInputFields(manageNo);
    }

    /**
     * 从接口管理API获取输入字段配置（使用原始的HttpUtil.get方式，不需要签名）
     *
     * @param manageNo 接口管理编号
     * @return 输入字段名列表
     */
    private List<String> fetchInputFieldsFromApi(String manageNo) {
        try {
            log.info("首次查询接口[{}]的输入字段配置，正在从接口管理API获取...", manageNo);

            // 使用原始代码的方式：直接HTTP GET请求（不需要签名验证）
            String result = HttpUtil.get(fieldsUrl + manageNo + "/fields");

            if (!JSONUtil.isJson(result)) {
                log.warn("接口管理API返回非JSON格式，manageNo: {}", manageNo);
                return Collections.emptyList();
            }

            JSONObject data = JSONObject.parseObject(result);
            JSONObject jsonObject = data.getJSONObject("data");

            if (jsonObject == null) {
                log.warn("接口管理API返回data为空，manageNo: {}", manageNo);
                return Collections.emptyList();
            }

            // 获取输入字段配置
            JSONObject fields = jsonObject.getJSONObject("fields");
            if (fields == null || !fields.containsKey("input")) {
                log.warn("接口[{}]没有配置输入字段", manageNo);
                return Collections.emptyList();
            }

            JSONArray inputFields = fields.getJSONArray("input");
            if (inputFields == null || inputFields.isEmpty()) {
                log.warn("接口[{}]的输入字段列表为空", manageNo);
                return Collections.emptyList();
            }

            // 解析字段名列表
            List<String> fieldNames = new ArrayList<>();
            for (int i = 0; i < inputFields.size(); i++) {
                JSONObject field = inputFields.getJSONObject(i);
                if (field != null && field.containsKey("name")) {
                    String fieldName = field.getString("name");
                    fieldNames.add(fieldName);
                }
            }

            log.info("接口[{}]输入字段配置已缓存: {}", manageNo, fieldNames);
            return fieldNames;

        } catch (Exception e) {
            log.error("获取接口[{}]输入字段配置失败", manageNo, e);
            return Collections.emptyList();
        }
    }

}
