package com.value.decision.model.decisionmanage.service;

import java.util.List;

/**
 * 热门搜索服务接口
 * @date 2025-12-02
 */
public interface HotKeywordService {

    /**
     * 记录搜索关键词
     * @param keyword 搜索关键词
     */
    void recordKeyword(String keyword);

    /**
     * 获取热门搜索关键词
     * @param topN 获取前N个
     * @return 热门关键词列表
     */
    List<String> getHotKeywords(int topN);

    /**
     * 获取热门搜索关键词（默认前10个）
     * @return 热门关键词列表
     */
    default List<String> getHotKeywords() {
        return getHotKeywords(10);
    }
}
