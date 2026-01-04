package com.value.decision.model.decisionmanage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.value.decision.model.decisionmanage.model.RulePool;
import com.value.decision.model.decisionmanage.model.dto.RulePoolQueryDTO;
import com.value.decision.model.decisionmanage.model.vo.RulePoolStatsVO;
import com.value.decision.model.decisionmanage.model.vo.RulePoolVO;

import java.util.List;

/**
 * 规则池服务接口
 * @author Claude Code
 * @date 2025-12-01
 */
public interface RulePoolService extends IService<RulePool> {

    /**
     * 获取规则池列表（不分页，兼容旧接口）
     */
    List<RulePoolVO> getRulePoolList();

    /**
     * 分页查询规则池列表（支持筛选和搜索）
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    PageInfo<RulePoolVO> queryRulePoolPage(RulePoolQueryDTO queryDTO);

    List<String> getHotKeywords(int topN);

    /**
     * 获取规则池统计信息
     * @return 统计数据
     */
    RulePoolStatsVO getStats();

    /**
     * 获取导出数据（无分页）
     * @param queryDTO 查询条件
     * @return 导出数据列表
     */
    List<RulePoolVO> getExportData(RulePoolQueryDTO queryDTO);
}
