package com.value.decision.model.decisionmanage.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.value.decision.model.decisionmanage.mapper.RulePoolMapper;
import com.value.decision.model.decisionmanage.model.Business;
import com.value.decision.model.decisionmanage.model.Product;
import com.value.decision.model.decisionmanage.model.RulePool;
import com.value.decision.model.decisionmanage.model.dto.RulePoolQueryDTO;
import com.value.decision.model.decisionmanage.model.vo.RulePoolStatsVO;
import com.value.decision.model.decisionmanage.model.vo.RulePoolVO;
import com.value.decision.model.decisionmanage.service.BusinessService;
import com.value.decision.model.decisionmanage.service.HotKeywordService;
import com.value.decision.model.decisionmanage.service.ProductService;
import com.value.decision.model.decisionmanage.service.RulePoolService;
import com.value.decision.model.decisionmanage.service.RulePoolSnapshotService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 规则池服务实现类
 * @author Claude Code
 * @date 2025-12-01
 */
@Service
@AllArgsConstructor
public class RulePoolServiceImpl extends ServiceImpl<RulePoolMapper, RulePool> implements RulePoolService {

    private final HotKeywordService hotKeywordService;
    private final ProductService productService;
    private final BusinessService businessService;
    private final RulePoolSnapshotService rulePoolSnapshotService;

    @Override
    public List<RulePoolVO> getRulePoolList() {
        List<RulePool> list = this.lambdaQuery().list();
        // 批量转换（性能优化，避免 N+1 查询）
        return convertBatch(list);
    }

    @Override
    public PageInfo<RulePoolVO> queryRulePoolPage(RulePoolQueryDTO queryDTO) {
        // 记录搜索关键词（用于热门搜索统计）
        if (StrUtil.isNotBlank(queryDTO.getKeyword())) {
            hotKeywordService.recordKeyword(queryDTO.getKeyword());
        }

        // 启用分页
        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());

        // 构建查询条件
        LambdaQueryWrapper<RulePool> wrapper = buildQueryWrapper(queryDTO);

        // 执行查询
        List<RulePool> list = this.list(wrapper);
        PageInfo<RulePool> pageInfo = new PageInfo<>(list);

        // 批量转换为VO（性能优化，避免 N+1 查询）
        List<RulePoolVO> voList = convertBatch(list);

        // 构建返回的分页结果
        PageInfo<RulePoolVO> result = new PageInfo<>();
        BeanUtil.copyProperties(pageInfo, result);
        result.setList(voList);

        return result;
    }

    @Override
    public List<String> getHotKeywords(int topN) {
        return hotKeywordService.getHotKeywords(topN);
    }

    @Override
    public RulePoolStatsVO getStats() {
        RulePoolStatsVO stats = new RulePoolStatsVO();

        // 计算上月月份标识（yyyy-MM格式）
        String lastMonth = LocalDate.now()
                .minusMonths(1)
                .format(DateTimeFormatter.ofPattern("yyyy-MM"));

        // ==================== 本月数据（从当前 rule_pool 表实时计算） ====================
        // 总规则数
        long totalRules = this.lambdaQuery().count();
        stats.setTotalRules(totalRules);

        // 启用中规则数（status = '1'）
        long activeRules = this.lambdaQuery()
                .eq(RulePool::getStatus, "1")
                .count();
        stats.setActiveRules(activeRules);

        // 规则覆盖产品数（去重统计 referenceProduct）
        List<RulePool> currentRules = this.lambdaQuery().list();
        long productCount = currentRules.stream()
                .map(RulePool::getReferenceProduct)
                .filter(StrUtil::isNotBlank)
                .distinct()
                .count();
        stats.setProductCount(productCount);

        // 规则覆盖场景数（去重统计 businessScene）
        long sceneCount = currentRules.stream()
                .map(RulePool::getBusinessScene)
                .filter(StrUtil::isNotBlank)
                .distinct()
                .count();
        stats.setSceneCount(sceneCount);

        // ==================== 上月数据（从快照表获取） ====================
        long lastMonthTotalRules = rulePoolSnapshotService.getTotalRulesCount(lastMonth);
        long lastMonthActiveRules = rulePoolSnapshotService.getActiveRulesCount(lastMonth);
        long lastMonthProductCount = rulePoolSnapshotService.getProductCount(lastMonth);
        long lastMonthSceneCount = rulePoolSnapshotService.getSceneCount(lastMonth);

        // ==================== 计算环比 ====================
        stats.setTotalRulesRate(calculateRate(totalRules, lastMonthTotalRules));
        stats.setActiveRulesRate(calculateRate(activeRules, lastMonthActiveRules));
        stats.setProductCountRate(calculateRate(productCount, lastMonthProductCount));
        stats.setSceneCountRate(calculateRate(sceneCount, lastMonthSceneCount));

        return stats;
    }

    /**
     * 计算环比增长率
     * @param current 本月数据
     * @param last 上月数据
     * @return 增长率（百分比，保留1位小数）
     */
    private Double calculateRate(long current, long last) {
        if (last == 0) {
            // 上月为0，本月有数据，返回100%
            return current > 0 ? 100.0 : 0.0;
        }
        // (本月 - 上月) / 上月 * 100
        BigDecimal currentBd = new BigDecimal(current);
        BigDecimal lastBd = new BigDecimal(last);
        BigDecimal diff = currentBd.subtract(lastBd);
        BigDecimal rate = diff.divide(lastBd, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100));
        return rate.setScale(1, RoundingMode.HALF_UP).doubleValue();
    }

    @Override
    public List<RulePoolVO> getExportData(RulePoolQueryDTO queryDTO) {
        List<RulePool> list;

        // 如果指定了ID列表，按ID导出（复选框选中导出）
        if (queryDTO.getIds() != null && !queryDTO.getIds().isEmpty()) {
            list = this.listByIds(queryDTO.getIds());
        } else {
            // 否则按筛选条件导出
            LambdaQueryWrapper<RulePool> wrapper = buildQueryWrapper(queryDTO);
            list = this.list(wrapper);
        }

        // 批量查询优化：避免 N+1 查询问题
        return convertBatch(list);
    }

    /**
     * 构建查询条件
     */
    private LambdaQueryWrapper<RulePool> buildQueryWrapper(RulePoolQueryDTO queryDTO) {
        LambdaQueryWrapper<RulePool> wrapper = Wrappers.lambdaQuery();

        // 关键词搜索（规则编码、规则描述、策略名称）
        if (StrUtil.isNotBlank(queryDTO.getKeyword())) {
            String keyword = queryDTO.getKeyword();
            wrapper.and(w -> w
                    .like(RulePool::getRuleCode, keyword)
                    .or().like(RulePool::getRuleDesc, keyword)
                    .or().like(RulePool::getStrategy, keyword)
                    .or().like(RulePool::getReferenceProduct, keyword)
            );
        }

        // 状态筛选（0-已禁用，1-启用中）
        if (StrUtil.isNotBlank(queryDTO.getStatus())) {
            wrapper.eq(RulePool::getStatus, queryDTO.getStatus());
        }

        // 风险等级筛选
        if (StrUtil.isNotBlank(queryDTO.getRiskLevel())) {
            wrapper.eq(RulePool::getRiskLevel, queryDTO.getRiskLevel());
        }

        // 策略筛选
        if (StrUtil.isNotBlank(queryDTO.getStrategy())) {
            wrapper.like(RulePool::getStrategy, queryDTO.getStrategy());
        }

        // 命中动作筛选
        if (StrUtil.isNotBlank(queryDTO.getHitAction())) {
            wrapper.like(RulePool::getHitAction, queryDTO.getHitAction());
        }

        // 业务场景筛选
        if (StrUtil.isNotBlank(queryDTO.getBusinessScene())) {
            wrapper.eq(RulePool::getBusinessScene, queryDTO.getBusinessScene());
        }

        // 模型类型筛选
        if (StrUtil.isNotBlank(queryDTO.getModelType())) {
            wrapper.eq(RulePool::getModelType, queryDTO.getModelType());
        }

        // 参考产品筛选（模糊匹配）
        if (StrUtil.isNotBlank(queryDTO.getReferenceProduct())) {
            wrapper.like(RulePool::getReferenceProduct, queryDTO.getReferenceProduct());
        }

        // 时间范围筛选
        if (StrUtil.isNotBlank(queryDTO.getCreateTimeStart())) {
            wrapper.ge(RulePool::getCreateTime, queryDTO.getCreateTimeStart());
        }
        if (StrUtil.isNotBlank(queryDTO.getCreateTimeEnd())) {
            wrapper.le(RulePool::getCreateTime, queryDTO.getCreateTimeEnd() + " 23:59:59");
        }

        // 默认按创建时间倒序
        wrapper.orderByDesc(RulePool::getCreateTime);

        return wrapper;
    }

    /**
     * 批量实体转VO（性能优化版本，避免 N+1 查询）
     */
    private List<RulePoolVO> convertBatch(List<RulePool> rulePoolList) {
        if (rulePoolList == null || rulePoolList.isEmpty()) {
            return new ArrayList<>();
        }

        // 1. 收集所有的产品ID和业务场景ID
        Set<Long> productIds = new HashSet<>();
        Set<Long> businessIds = new HashSet<>();

        for (RulePool rulePool : rulePoolList) {
            // 收集产品ID
            if (StrUtil.isNotBlank(rulePool.getReferenceProduct())) {
                String[] ids = rulePool.getReferenceProduct().split(",");
                for (String id : ids) {
                    id = id.trim();
                    if (StrUtil.isNotBlank(id)) {
                        try {
                            productIds.add(Long.parseLong(id));
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }
            }

            // 收集业务场景ID
            if (StrUtil.isNotBlank(rulePool.getBusinessScene())) {
                String[] ids = rulePool.getBusinessScene().split(",");
                for (String id : ids) {
                    id = id.trim();
                    if (StrUtil.isNotBlank(id)) {
                        try {
                            businessIds.add(Long.parseLong(id));
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }
            }
        }

        // 2. 批量查询产品和业务场景（一次性查询）
        Map<Long, String> productNameMap = new HashMap<>();
        Map<Long, String> businessNameMap = new HashMap<>();

        if (!productIds.isEmpty()) {
            List<Product> products = productService.listByIds(productIds);
            for (Product product : products) {
                productNameMap.put(product.getId(), product.getName());
            }
        }

        if (!businessIds.isEmpty()) {
            List<Business> businesses = businessService.listByIds(businessIds);
            for (Business business : businesses) {
                businessNameMap.put(business.getId(), business.getName());
            }
        }

        // 3. 批量转换
        return rulePoolList.stream()
                .map(rulePool -> convertWithCache(rulePool, productNameMap, businessNameMap))
                .collect(Collectors.toList());
    }

    /**
     * 实体转VO（使用缓存的映射关系）
     */
    private RulePoolVO convertWithCache(RulePool rulePool,
                                        Map<Long, String> productNameMap,
                                        Map<Long, String> businessNameMap) {
        RulePoolVO vo = new RulePoolVO();
        BeanUtil.copyProperties(rulePool, vo);

        // 转换风险等级为中文
        fillRiskLevelZH(rulePool.getRiskLevel(), vo);

        // 转换状态为中文
        fillStatusZH(rulePool.getStatus(), vo);

        // 转换模型类型为中文
        fillModelTypeZH(rulePool.getModelType(), vo);

        // 拼接命中动作展示字段（强拒绝-转人工）
        fillHitActionDisplay(rulePool, vo);

        // 转换产品ID为产品名称（使用缓存）
        fillProductNameWithCache(rulePool.getReferenceProduct(), vo, productNameMap);

        // 转换业务场景ID为业务场景名称（使用缓存）
        fillBusinessNameWithCache(rulePool.getBusinessScene(), vo, businessNameMap);

        return vo;
    }

    /**
     * 拼接命中动作展示字段
     * 格式：强拒绝-转人工
     */
    private void fillHitActionDisplay(RulePool rulePool, RulePoolVO vo) {
        String reject = "1".equals(rulePool.getStronglyReject()) ? "强拒绝" : "";
        String transfer = "1".equals(rulePool.getTransferToPerson()) ? "转人工" : "";

        // 拼接，如果都为空则显示 "-"
        if (StrUtil.isAllBlank(reject, transfer)) {
            vo.setHitActionDisplay("-");
        } else if (StrUtil.isBlank(reject)) {
            vo.setHitActionDisplay(transfer);
        } else if (StrUtil.isBlank(transfer)) {
            vo.setHitActionDisplay(reject);
        } else {
            vo.setHitActionDisplay(reject + "-" + transfer);
        }
    }

    /**
     * 风险等级转中文
     */
    private void fillRiskLevelZH(String riskLevel, RulePoolVO vo) {
        if (StrUtil.isBlank(riskLevel)) {
            vo.setRiskLevel("-");
            return;
        }
        switch (riskLevel) {
            case "1": vo.setRiskLevel("低风险"); break;
            case "2": vo.setRiskLevel("中低风险"); break;
            case "3": vo.setRiskLevel("中风险"); break;
            case "4": vo.setRiskLevel("中高风险"); break;
            case "5": vo.setRiskLevel("高风险"); break;
            default: vo.setRiskLevel("-");
        }
    }

    /**
     * 状态转中文
     */
    private void fillStatusZH(String status, RulePoolVO vo) {
        if ("1".equals(status)) {
            vo.setStatus("启用中");
        } else {
            vo.setStatus("已禁用");
        }
    }

    /**
     * 模型类型转中文
     */
    private void fillModelTypeZH(String modelType, RulePoolVO vo) {
        if (StrUtil.isBlank(modelType)) {
            vo.setModelType("-");
            return;
        }
        switch (modelType) {
            case "1": vo.setModelType("评分"); break;
            case "5": vo.setModelType("规则"); break;
            case "6": vo.setModelType("分类"); break;
            default: vo.setModelType("-");
        }
    }

    /**
     * 产品ID转产品名称（使用缓存，性能优化版本）
     */
    private void fillProductNameWithCache(String referenceProduct, RulePoolVO vo, Map<Long, String> productNameMap) {
        if (StrUtil.isBlank(referenceProduct)) {
            vo.setReferenceProduct("-");
            return;
        }
        try {
            // 支持多个产品ID（逗号分隔）
            String[] productIds = referenceProduct.split(",");
            StringBuilder productNames = new StringBuilder();

            for (String productId : productIds) {
                productId = productId.trim();
                if (StrUtil.isNotBlank(productId)) {
                    Long id = Long.parseLong(productId);
                    String name = productNameMap.get(id);
                    if (StrUtil.isNotBlank(name)) {
                        if (productNames.length() > 0) {
                            productNames.append(", ");
                        }
                        productNames.append(name);
                    }
                }
            }

            vo.setReferenceProduct(productNames.length() > 0 ? productNames.toString() : "-");
        } catch (Exception e) {
            // 解析失败时保持原值
            vo.setReferenceProduct(referenceProduct);
        }
    }

    /**
     * 业务场景ID转业务场景名称（使用缓存，性能优化版本）
     */
    private void fillBusinessNameWithCache(String businessScene, RulePoolVO vo, Map<Long, String> businessNameMap) {
        if (StrUtil.isBlank(businessScene)) {
            vo.setBusinessScene("-");
            return;
        }
        try {
            // 支持多个业务场景ID（逗号分隔）
            String[] businessIds = businessScene.split(",");
            StringBuilder businessNames = new StringBuilder();

            for (String businessId : businessIds) {
                businessId = businessId.trim();
                if (StrUtil.isNotBlank(businessId)) {
                    Long id = Long.parseLong(businessId);
                    String name = businessNameMap.get(id);
                    if (StrUtil.isNotBlank(name)) {
                        if (businessNames.length() > 0) {
                            businessNames.append(", ");
                        }
                        businessNames.append(name);
                    }
                }
            }

            vo.setBusinessScene(businessNames.length() > 0 ? businessNames.toString() : "-");
        } catch (Exception e) {
            // 解析失败时保持原值
            vo.setBusinessScene(businessScene);
        }
    }
}
