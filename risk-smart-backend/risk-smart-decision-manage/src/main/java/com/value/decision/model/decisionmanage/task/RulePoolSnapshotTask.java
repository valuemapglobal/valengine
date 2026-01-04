package com.value.decision.model.decisionmanage.task;

import com.value.decision.model.decisionmanage.service.RulePoolSnapshotService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 规则池月度快照定时任务
 * 每月1号凌晨1点执行，创建上月的规则池快照
 *
 * @author Claude Code
 * @date 2025-12-02
 */
@Slf4j
@Component
@AllArgsConstructor
public class RulePoolSnapshotTask {

    private final RulePoolSnapshotService snapshotService;

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    /**
     * 每月1号凌晨1点执行
     * 创建上月的规则池状态快照
     */
    @Scheduled(cron = "0 0 1 1 * ?")
    public void createMonthlySnapshot() {
        // 获取上月月份
        String lastMonth = LocalDate.now().minusMonths(1).format(MONTH_FORMATTER);
        log.info("定时任务：开始创建规则池月度快照，快照月份：{}", lastMonth);

        try {
            snapshotService.createMonthlySnapshot(lastMonth);
            log.info("定时任务：规则池月度快照创建成功，快照月份：{}", lastMonth);
        } catch (Exception e) {
            log.error("定时任务：规则池月度快照创建失败，快照月份：{}", lastMonth, e);
        }
    }

    /**
     * 手动触发创建指定月份的快照（供测试或补数据使用）
     *
     * @param snapshotMonth 快照月份（yyyy-MM格式）
     */
    public void manualCreateSnapshot(String snapshotMonth) {
        log.info("手动触发：开始创建规则池月度快照，快照月份：{}", snapshotMonth);
        snapshotService.createMonthlySnapshot(snapshotMonth);
    }
}
