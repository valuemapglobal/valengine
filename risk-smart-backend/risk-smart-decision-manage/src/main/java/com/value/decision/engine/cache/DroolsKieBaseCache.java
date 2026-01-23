package com.value.decision.engine.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import lombok.extern.slf4j.Slf4j;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Drools 规则引擎缓存组件 (基于Caffeine优化版)
 * 用于缓存已编译的 KieBase，避免重复编译规则提升性能
 *
 * <p>优化特性:</p>
 * <ul>
 *   <li>基于LRU的容量控制: 最多缓存200条规则</li>
 *   <li>双重过期策略: 写入后24小时过期 + 访问后12小时过期</li>
 *   <li>自动清理冷数据,防止内存泄漏</li>
 *   <li>内置统计监控,支持缓存命中率分析</li>
 * </ul>
 *
 * <p>缓存清理时机:</p>
 * <ul>
 *   <li>规则模型保存/更新: /rdenew/model/decisionCodeLevel/submit|update</li>
 *   <li>评分模型保存/更新: /score-index-rule/submit|update</li>
 *   <li>自动过期: 24小时未写入 或 12小时未访问</li>
 * </ul>
 *
 * @author Austin
 * @since 2025-11-04
 */
@Component
@Slf4j
public class DroolsKieBaseCache {

    /**
     * KieBase 缓存: ruleContent的Hash -> KieBase
     * 使用 Caffeine 实现高性能缓存,支持LRU淘汰和TTL过期
     */
    private final Cache<Integer, KieBase> kieBaseCache;

    public DroolsKieBaseCache() {
        this.kieBaseCache = Caffeine.newBuilder()
                // 最大容量: 200条规则 (根据业务规模配置)
                .maximumSize(200)

                // 写入后过期: 24小时 (确保每天刷新规则)
                .expireAfterWrite(24, TimeUnit.HOURS)

                // 访问后过期: 12小时 (冷规则自动清理)
                .expireAfterAccess(12, TimeUnit.HOURS)

                // 开启统计: 便于监控缓存命中率
                .recordStats()

                // 移除监听: 记录缓存清理事件
                .removalListener((Integer key, KieBase value, RemovalCause cause) -> {
                    log.info("【Drools缓存清理】ruleHash={}, 原因={}", key, cause);
                })

                .build();
    }

    /**
     * 获取或创建 KieBase
     *
     * @param ruleContent DRL 规则内容
     * @return KieBase 实例
     */
    public KieBase getOrCompileKieBase(String ruleContent) {
        // 使用规则内容的 hashCode 作为缓存 key
        int ruleHash = ruleContent.hashCode();

        // 检查是否命中缓存
        KieBase cachedKieBase = kieBaseCache.getIfPresent(ruleHash);
        if (cachedKieBase != null) {
            log.info("【Drools缓存命中】ruleHash={}, 当前缓存大小={}, 缓存统计={}",
                ruleHash, getCacheSize(), getCacheStats());
            return cachedKieBase;
        }

        // 使用 Caffeine 的 get() 方法,自动处理并发编译
        // Caffeine保证同一个key只会被一个线程加载,其他线程等待
        return kieBaseCache.get(ruleHash, key -> {
            // 执行编译 (此闭包内的代码只会被一个线程执行)
            log.info("【Drools规则编译】首次编译规则: ruleHash={}, ruleLength={}", key, ruleContent.length());
            long startTime = System.currentTimeMillis();

            KieBase kieBase = compileRule(ruleContent);

            long elapsedTime = System.currentTimeMillis() - startTime;
            log.info("【Drools规则编译】编译完成: ruleHash={}, 耗时={}ms, 当前缓存大小={}",
                key, elapsedTime, getCacheSize());

            return kieBase;
        });
    }

    /**
     * 创建 KieSession（每次调用都创建新实例）
     *
     * @param ruleContent DRL 规则内容
     * @return KieSession 实例
     */
    public KieSession createKieSession(String ruleContent) {
        KieBase kieBase = getOrCompileKieBase(ruleContent);
        return kieBase.newKieSession();
    }

    /**
     * 编译规则内容为 KieBase
     *
     * @param ruleContent DRL 规则内容
     * @return 编译后的 KieBase
     */
    private KieBase compileRule(String ruleContent) {
        KieHelper helper = new KieHelper();
        try {
            helper.addContent(ruleContent, ResourceType.DRL);
            return helper.build();
        } catch (Exception e) {
            log.error("【Drools规则编译】编译失败: ruleContent={}", ruleContent, e);
            throw new RuntimeException("Drools 规则编译失败", e);
        }
    }

    /**
     * 清空指定规则的缓存
     * 注意: 由于无法从规则ID反向查找hashCode,此方法实际执行全部清空
     *
     * @param ruleContent DRL 规则内容 (可为空)
     */
    public void invalidateCache(String ruleContent) {
        if (ruleContent != null && !ruleContent.isEmpty()) {
            // 方案1: 如果知道ruleContent,可以精确删除
            int ruleHash = ruleContent.hashCode();
            kieBaseCache.invalidate(ruleHash);
            log.info("【Drools缓存失效】清除指定规则缓存: ruleHash={}", ruleHash);
        } else {
            // 方案2: 不知道具体内容,清空所有
            clearAll();
        }
    }

    /**
     * 清空所有缓存
     * 应用场景: 规则模型保存/更新时调用
     */
    public void clearAll() {
        long size = kieBaseCache.estimatedSize();
        String stats = getCacheStats();
        kieBaseCache.invalidateAll();
        log.info("【Drools缓存清空】清空所有规则缓存: 共清除{}条, 清空前统计={}", size, stats);
    }

    /**
     * 获取缓存统计信息
     *
     * @return 缓存大小
     */
    public int getCacheSize() {
        return (int) kieBaseCache.estimatedSize();
    }

    /**
     * 获取缓存统计详情
     *
     * @return 统计信息 (命中率、加载次数等)
     */
    public String getCacheStats() {
        return kieBaseCache.stats().toString();
    }
}
