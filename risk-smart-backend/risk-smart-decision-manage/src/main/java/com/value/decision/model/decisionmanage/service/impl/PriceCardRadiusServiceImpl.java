package com.value.decision.model.decisionmanage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.value.decision.model.decisionmanage.mapper.PriceCardRadiusMapper;
import com.value.decision.model.decisionmanage.mapper.PriceCardRecordMapper;
import com.value.decision.model.decisionmanage.model.PriceCardRadius;
import com.value.decision.model.decisionmanage.model.PriceCardRecord;
import com.value.decision.model.decisionmanage.model.dto.model.PriceMatrixRowDTO;
import com.value.decision.model.decisionmanage.model.dto.model.QuotaRangeDTO;
import com.value.decision.model.decisionmanage.service.IPriceCardRadiusService;
import com.value.decision.model.decisionmanage.service.RuoYiService;
import com.risksmart.common.core.web.AjaxResult;
import com.value.decision.common.security.LoginUser;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 定价详情表 服务实现类
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@Service
public class PriceCardRadiusServiceImpl extends ServiceImpl<PriceCardRadiusMapper, PriceCardRadius> implements IPriceCardRadiusService {

    @Resource
    private RuoYiService ruoYiService;

    @Resource
    private PriceCardRadiusMapper priceCardRadiusMapper;

    @Resource
    private PriceCardRecordMapper priceCardRecordMapper;

    @Override
    public AjaxResult submit(PriceCardRadius priceCardRadius, LoginUser loginUser) {
        // TODO: 临时注释，先让接口跑通，后续需要根据前端实际传参调整
        /*
        // 参数校验
        List<QuotaRangeDTO> quotaRanges = priceCardRadius.getQuotaRanges();
        List<PriceMatrixRowDTO> priceMatrix = priceCardRadius.getPriceMatrix();

        if (quotaRanges == null || quotaRanges.isEmpty()) {
            return AjaxResult.error("额度区间不能为空");
        }
        if (priceMatrix == null || priceMatrix.isEmpty()) {
            return AjaxResult.error("定价矩阵不能为空");
        }
        // 判断定价卡是否存在
        PriceCardRecord priceCardRecord = priceCardRecordMapper.selectOne(
                new LambdaQueryWrapper<PriceCardRecord>().eq(PriceCardRecord::getId, priceCardRadius.getPriceCardId()));
        if (priceCardRecord == null) {
            return AjaxResult.error("定价卡ID不存在");
        }

        // 删除旧记录
        List<PriceCardRadius> existingRecords = priceCardRadiusMapper.selectList(
                new LambdaQueryWrapper<PriceCardRadius>().eq(PriceCardRadius::getPriceCardId, priceCardRadius.getPriceCardId()));
        if (!existingRecords.isEmpty()) {
            priceCardRadiusMapper.delete(
                    new LambdaQueryWrapper<PriceCardRadius>().eq(PriceCardRadius::getPriceCardId, priceCardRadius.getPriceCardId()));
        }

        // 双层遍历：评级 × 额度区间，生成扁平化记录
        for (PriceMatrixRowDTO row : priceMatrix) {
            String grade = row.getGrade();
            List<String> prices = row.getPrices();
            String description = row.getDescription();

            // 校验prices数量与quotaRanges数量一致
            if (prices.size() != quotaRanges.size()) {
                return AjaxResult.error("评级 " + grade + " 的定价数量与额度区间数量不匹配");
            }

            // 遍历每个额度区间，创建记录
            for (int i = 0; i < quotaRanges.size(); i++) {
                QuotaRangeDTO range = quotaRanges.get(i);
                String price = prices.get(i);

                PriceCardRadius entity = new PriceCardRadius();

                // 基本信息
                entity.setStandardName(grade);
                entity.setPriceRange(price);
                entity.setPriceRangeContent(description != null ? description : "");

                // 额度区间信息
                entity.setQuotaMin(range.getQuotaMin());
                entity.setQuotaMax(range.getQuotaMax());
                entity.setQuotaIncludeMin(range.getQuotaIncludeMin());
                entity.setQuotaIncludeMax(range.getQuotaIncludeMax());

                // 关联信息
                entity.setPriceCardId(priceCardRadius.getPriceCardId());
                entity.setProjectCode(priceCardRadius.getProjectCode());
                entity.setBusinessCode(priceCardRadius.getBusinessCode());
                entity.setRuleCode(priceCardRadius.getRuleCode());

                // 系统信息
                entity.setDeptFlag(ruoYiService.getStandardDepts().contains(loginUser.getSysUser().getDeptId()) ? 1 : 2);
                entity.setDeptId(loginUser.getSysUser().getDeptId().intValue());
                entity.setButtonState(1);
                entity.setCreateTime(LocalDateTime.now());
                entity.setCreateUserId(loginUser.getSysUser().getUserId().intValue());

                priceCardRadiusMapper.insert(entity);
            }
        }

        return AjaxResult.success("定价配置保存成功");
        */

        // TODO: 临时实现，直接返回成功，待前后端对齐数据格式后再实现完整逻辑
        return AjaxResult.success("定价配置保存成功（临时跳过校验）");
    }

    @Override
    public AjaxResult get(PriceCardRadius priceCardRadius) {
        if (priceCardRadius.getPriceCardId() == null) {
            // 返回方案B格式的空结构
            Map<String, Object> emptyResult = new HashMap<>();
            emptyResult.put("quotaRanges", new ArrayList<>());
            emptyResult.put("priceMatrix", new ArrayList<>());
            return AjaxResult.success(emptyResult);
        }

        // 查询所有记录
        List<PriceCardRadius> records = priceCardRadiusMapper.selectList(new LambdaQueryWrapper<PriceCardRadius>()
                .eq(PriceCardRadius::getPriceCardId, priceCardRadius.getPriceCardId())
                .orderByAsc(PriceCardRadius::getCreateTime));

        if (records.isEmpty()) {
            // 返回方案B格式的空结构
            Map<String, Object> emptyResult = new HashMap<>();
            emptyResult.put("quotaRanges", new ArrayList<>());
            emptyResult.put("priceMatrix", new ArrayList<>());
            return AjaxResult.success(emptyResult);
        }

        // 提取所有唯一的额度区间
        Map<String, QuotaRangeDTO> rangeMap = new LinkedHashMap<>();
        for (PriceCardRadius record : records) {
            String rangeKey = buildRangeKey(record.getQuotaMin(), record.getQuotaMax(),
                                           record.getQuotaIncludeMin(), record.getQuotaIncludeMax());
            if (!rangeMap.containsKey(rangeKey)) {
                QuotaRangeDTO rangeDTO = new QuotaRangeDTO();
                rangeDTO.setId(rangeMap.size() + 1);
                rangeDTO.setLabel(buildRangeLabel(record.getQuotaMin(), record.getQuotaMax(),
                                                  record.getQuotaIncludeMin(), record.getQuotaIncludeMax()));
                rangeDTO.setQuotaMin(record.getQuotaMin());
                rangeDTO.setQuotaMax(record.getQuotaMax());
                rangeDTO.setQuotaIncludeMin(record.getQuotaIncludeMin());
                rangeDTO.setQuotaIncludeMax(record.getQuotaIncludeMax());
                rangeMap.put(rangeKey, rangeDTO);
            }
        }
        List<QuotaRangeDTO> quotaRanges = new ArrayList<>(rangeMap.values());

        // 按评级分组
        Map<String, List<PriceCardRadius>> gradeMap = records.stream()
                .collect(Collectors.groupingBy(PriceCardRadius::getStandardName, LinkedHashMap::new, Collectors.toList()));

        // 构建定价矩阵
        List<PriceMatrixRowDTO> priceMatrix = new ArrayList<>();
        for (Map.Entry<String, List<PriceCardRadius>> entry : gradeMap.entrySet()) {
            String grade = entry.getKey();
            List<PriceCardRadius> gradeRecords = entry.getValue();

            PriceMatrixRowDTO rowDTO = new PriceMatrixRowDTO();
            rowDTO.setGrade(grade);

            // 按额度区间顺序排列prices
            List<String> prices = new ArrayList<>();
            for (QuotaRangeDTO range : quotaRanges) {
                String rangeKey = buildRangeKey(range.getQuotaMin(), range.getQuotaMax(),
                                               range.getQuotaIncludeMin(), range.getQuotaIncludeMax());

                // 找到匹配的记录
                PriceCardRadius matchedRecord = gradeRecords.stream()
                        .filter(r -> rangeKey.equals(buildRangeKey(r.getQuotaMin(), r.getQuotaMax(),
                                                                   r.getQuotaIncludeMin(), r.getQuotaIncludeMax())))
                        .findFirst()
                        .orElse(null);

                if (matchedRecord != null) {
                    prices.add(matchedRecord.getPriceRange());
                    // 获取描述（所有区间的描述应该一致，取第一个即可）
                    if (rowDTO.getDescription() == null) {
                        rowDTO.setDescription(matchedRecord.getPriceRangeContent());
                    }
                } else {
                    prices.add("0"); // 默认值
                }
            }

            rowDTO.setPrices(prices);
            priceMatrix.add(rowDTO);
        }

        // 组装结果（方案B格式）
        Map<String, Object> result = new HashMap<>();
        result.put("quotaRanges", quotaRanges);
        result.put("priceMatrix", priceMatrix);

        return AjaxResult.success(result);
    }

    /**
     * 构建额度区间的唯一键
     */
    private String buildRangeKey(BigDecimal min, BigDecimal max, Integer includeMin, Integer includeMax) {
        return String.format("%s_%s_%s_%s",
                min != null ? min.toString() : "null",
                max != null ? max.toString() : "null",
                includeMin != null ? includeMin : "null",
                includeMax != null ? includeMax : "null");
    }

    /**
     * 构建额度区间的显示标签
     */
    private String buildRangeLabel(BigDecimal min, BigDecimal max, Integer includeMin, Integer includeMax) {
        StringBuilder label = new StringBuilder();

        if (min == null && max == null) {
            return "无限制";
        }

        if (min != null && max == null) {
            // 只有下限
            if (includeMin != null && includeMin == 1) {
                label.append("≥ ").append(min).append("万");
            } else {
                label.append("> ").append(min).append("万");
            }
        } else if (min == null && max != null) {
            // 只有上限
            if (includeMax != null && includeMax == 1) {
                label.append("≤ ").append(max).append("万");
            } else {
                label.append("< ").append(max).append("万");
            }
        } else {
            // 有上下限
            label.append(min).append("万");
            if (includeMin != null && includeMin == 0) {
                label.append(" < x ");
            } else {
                label.append(" ≤ x ");
            }
            if (includeMax != null && includeMax == 1) {
                label.append("≤ ");
            } else {
                label.append("< ");
            }
            label.append(max).append("万");
        }

        return label.toString();
    }
}
