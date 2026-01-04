package com.value.decision.model.decisionmanage.service.impl;

import cn.hutool.core.util.StrUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.value.decision.model.decisionmanage.service.HotKeywordService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 热门搜索服务实现类
 * 使用 Caffeine 本地缓存实现热门关键词统计
 * @date 2025-12-02
 */
@Slf4j
@Service
public class HotKeywordServiceImpl implements HotKeywordService {

    /**
     * 关键词计数器
     * key: 搜索关键词
     * value: 搜索次数
     */
    private final ConcurrentHashMap<String, AtomicLong> keywordCounter = new ConcurrentHashMap<>();

    /**
     * 热门关键词缓存（定期从 keywordCounter 中计算）
     * 缓存1分钟，避免每次都排序
     */
    private Cache<String, List<String>> hotKeywordsCache;

    /**
     * 缓存key
     */
    private static final String CACHE_KEY = "hot_keywords";

    /**
     * 最大缓存关键词数量（防止内存溢出）
     */
    private static final int MAX_KEYWORDS = 1000;

    @PostConstruct
    public void init() {
        hotKeywordsCache = Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.MINUTES)  // 1分钟过期，自动重新计算
                .maximumSize(1)
                .build();
    }

    @Override
    public void recordKeyword(String keyword) {
        if (StrUtil.isBlank(keyword)) {
            return;
        }
        // 统一转小写并去除首尾空格
        String normalizedKeyword = keyword.trim().toLowerCase();

        // 防止缓存过大，超过上限时清理低频词
        if (keywordCounter.size() >= MAX_KEYWORDS) {
            cleanupLowFrequencyKeywords();
        }

        // 计数+1
        keywordCounter.computeIfAbsent(normalizedKeyword, k -> new AtomicLong(0)).incrementAndGet();

        // 使缓存失效，下次获取时重新计算
        hotKeywordsCache.invalidate(CACHE_KEY);
    }

    @Override
    public List<String> getHotKeywords(int topN) {
        return hotKeywordsCache.get(CACHE_KEY, key -> calculateHotKeywords(topN));
    }

    /**
     * 计算热门关键词
     */
    private List<String> calculateHotKeywords(int topN) {
        return keywordCounter.entrySet().stream()
                .sorted(Comparator.comparingLong((Map.Entry<String, AtomicLong> e) -> e.getValue().get()).reversed())
                .limit(topN)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * 清理低频词（保留搜索次数前50%的词）
     */
    private void cleanupLowFrequencyKeywords() {
        if (keywordCounter.isEmpty()) {
            return;
        }

        // 计算中位数阈值
        long medianCount = keywordCounter.values().stream()
                .mapToLong(AtomicLong::get)
                .sorted()
                .skip(keywordCounter.size() / 2)
                .findFirst()
                .orElse(1);

        // 移除低于中位数的词
        keywordCounter.entrySet().removeIf(entry -> entry.getValue().get() < medianCount);
    }
}
