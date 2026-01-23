package com.value.decision.model.decisionmanage.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.value.decision.model.decisionmanage.mapper.RulePoolMapper;
import com.value.decision.model.decisionmanage.mapper.RulePoolMonthlySnapshotMapper;
import com.value.decision.model.decisionmanage.model.RulePool;
import com.value.decision.model.decisionmanage.model.RulePoolMonthlySnapshot;
import com.value.decision.model.decisionmanage.service.RulePoolSnapshotService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 规则池月度快照服务实现类
 *
 * @author Claude Code
 * @date 2025-12-02
 */
@Slf4j
@Service
@AllArgsConstructor
public class RulePoolSnapshotServiceImpl extends ServiceImpl<RulePoolMonthlySnapshotMapper, RulePoolMonthlySnapshot>
        implements RulePoolSnapshotService {

    private final RulePoolMapper rulePoolMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createMonthlySnapshot(String snapshotMonth) {
        log.info("开始创建规则池月度快照，快照月份：{}", snapshotMonth);

        // 1. 检查是否已存在该月份的快照
        long existCount = this.lambdaQuery()
                .eq(RulePoolMonthlySnapshot::getSnapshotMonth, snapshotMonth)
                .count();
        if (existCount > 0) {
            log.warn("快照月份 {} 已存在 {} 条记录，跳过创建", snapshotMonth, existCount);
            return;
        }

        // 2. 查询当前所有规则
        List<RulePool> rulePoolList = rulePoolMapper.selectList(new LambdaQueryWrapper<>());

        if (rulePoolList.isEmpty()) {
            log.info("规则池为空，无需创建快照");
            return;
        }

        // 3. 转换为快照记录
        LocalDateTime now = LocalDateTime.now();
        List<RulePoolMonthlySnapshot> snapshotList = rulePoolList.stream()
                .map(rule -> {
                    RulePoolMonthlySnapshot snapshot = new RulePoolMonthlySnapshot();
                    snapshot.setSnapshotMonth(snapshotMonth);
                    snapshot.setRuleId(rule.getId());
                    snapshot.setStatus(rule.getStatus());
                    snapshot.setProjectCode(rule.getReferenceProduct());
                    snapshot.setBusinessCode(rule.getBusinessScene());
                    snapshot.setRuleCode(rule.getModelType());
                    snapshot.setCreateTime(now);
                    return snapshot;
                })
                .collect(Collectors.toList());

        // 4. 批量保存
        this.saveBatch(snapshotList);

        log.info("规则池月度快照创建完成，快照月份：{}，记录数：{}", snapshotMonth, snapshotList.size());
    }

    @Override
    public long getActiveRulesCount(String snapshotMonth) {
        return this.lambdaQuery()
                .eq(RulePoolMonthlySnapshot::getSnapshotMonth, snapshotMonth)
                .eq(RulePoolMonthlySnapshot::getStatus, "1")
                .count();
    }

    @Override
    public long getTotalRulesCount(String snapshotMonth) {
        return this.lambdaQuery()
                .eq(RulePoolMonthlySnapshot::getSnapshotMonth, snapshotMonth)
                .count();
    }

    @Override
    public long getProductCount(String snapshotMonth) {
        List<RulePoolMonthlySnapshot> list = this.lambdaQuery()
                .eq(RulePoolMonthlySnapshot::getSnapshotMonth, snapshotMonth)
                .list();
        return list.stream()
                .map(RulePoolMonthlySnapshot::getProjectCode)
                .filter(StrUtil::isNotBlank)
                .distinct()
                .count();
    }

    @Override
    public long getSceneCount(String snapshotMonth) {
        List<RulePoolMonthlySnapshot> list = this.lambdaQuery()
                .eq(RulePoolMonthlySnapshot::getSnapshotMonth, snapshotMonth)
                .list();
        return list.stream()
                .map(RulePoolMonthlySnapshot::getBusinessCode)
                .filter(StrUtil::isNotBlank)
                .distinct()
                .count();
    }
}
