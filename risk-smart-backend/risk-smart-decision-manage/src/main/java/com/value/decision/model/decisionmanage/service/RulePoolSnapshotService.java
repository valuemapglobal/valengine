package com.value.decision.model.decisionmanage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.value.decision.model.decisionmanage.model.RulePoolMonthlySnapshot;

/**
 * 规则池月度快照服务接口
 *
 * @author Claude Code
 * @date 2025-12-02
 */
public interface RulePoolSnapshotService extends IService<RulePoolMonthlySnapshot> {

    /**
     * 创建月度快照
     * 将当前规则池状态快照到历史表
     *
     * @param snapshotMonth 快照月份（yyyy-MM格式）
     */
    void createMonthlySnapshot(String snapshotMonth);

    /**
     * 获取指定月份的启用规则数
     *
     * @param snapshotMonth 快照月份
     * @return 启用规则数
     */
    long getActiveRulesCount(String snapshotMonth);

    /**
     * 获取指定月份的总规则数
     *
     * @param snapshotMonth 快照月份
     * @return 总规则数
     */
    long getTotalRulesCount(String snapshotMonth);

    /**
     * 获取指定月份的产品数
     *
     * @param snapshotMonth 快照月份
     * @return 产品数
     */
    long getProductCount(String snapshotMonth);

    /**
     * 获取指定月份的场景数
     *
     * @param snapshotMonth 快照月份
     * @return 场景数
     */
    long getSceneCount(String snapshotMonth);
}
