package com.value.decision.model.decisionmanage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.value.decision.model.decisionmanage.mapper.PriceCardRecordMapper;
import com.value.decision.model.decisionmanage.mapper.QuotaCardRecordMapper;
import com.value.decision.model.decisionmanage.mapper.RateCardRadiusMapper;
import com.value.decision.model.decisionmanage.mapper.RateCardRecordMapper;
import com.value.decision.model.decisionmanage.model.PriceCardRecord;
import com.value.decision.model.decisionmanage.model.QuotaCardRecord;
import com.value.decision.model.decisionmanage.model.RateCardRadius;
import com.value.decision.model.decisionmanage.model.RateCardRecord;
import com.value.decision.model.decisionmanage.service.IRateCardRadiusService;
import com.value.decision.model.decisionmanage.service.RuoYiService;
import com.risksmart.common.core.web.AjaxResult;
import com.value.decision.common.security.LoginUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 评级范围表 服务实现类
 * </p>
 *
 * @author hc
 * @since 2023-08-10
 */
@Service
public class RateCardRadiusServiceImpl extends ServiceImpl<RateCardRadiusMapper, RateCardRadius> implements IRateCardRadiusService {
    @Resource
    private RuoYiService ruoYiService;

    @Resource
    private RateCardRadiusMapper rateCardRadiusMapper;

    @Resource
    private QuotaCardRecordMapper quotaCardRecordMapper;

    @Resource
    private RateCardRecordMapper rateCardRecordMapper;

    @Resource
    private PriceCardRecordMapper priceCardRecordMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult submit(RateCardRadius rateCardRadius, LoginUser loginUser) {
        List<Map<String, String>> mapList = rateCardRadius.getMapList();
        if (rateCardRadius.getId() == null) {
            if (mapList.size() != 0) {
                //根据评级卡id获取评级卡信息
                RateCardRecord rateCardRecord = rateCardRecordMapper.selectOne(new LambdaQueryWrapper<RateCardRecord>().eq(RateCardRecord::getId, rateCardRadius.getRateCardId()));

                if (rateCardRecord == null) {
                    return AjaxResult.error("评级卡不存在");
                }

                //判断是否已存在评级规则
                Long existingCount = Long.valueOf(rateCardRadiusMapper.selectCount(new LambdaQueryWrapper<RateCardRadius>().eq(RateCardRadius::getRateCardId, rateCardRadius.getRateCardId())));

                if (existingCount > 0) {
                    mapList.forEach(map -> {
                        // 根据 rateCardId + standardRate(name) 查找现有记录
                        RateCardRadius existingRecord = rateCardRadiusMapper.selectOne(
                            new LambdaQueryWrapper<RateCardRadius>()
                                .eq(RateCardRadius::getRateCardId, rateCardRadius.getRateCardId())
                                .eq(RateCardRadius::getStandardRate, map.get("name"))
                        );

                        if (existingRecord != null) {
                            // 更新现有记录
                            RateCardRadius updateEntity = new RateCardRadius();
                            updateEntity.setRateRangeContent(map.get("rateRangeContent"));
                            updateEntity.setRateRange(map.get("value"));
                            updateEntity.setUpdateTime(LocalDateTime.now());
                            rateCardRadiusMapper.update(updateEntity,
                                new LambdaQueryWrapper<RateCardRadius>().eq(RateCardRadius::getId, existingRecord.getId())
                            );
                        } else {
                            // 新增记录（如果评级名称不存在）
                            RateCardRadius rateCardRadius1 = new RateCardRadius();
                            rateCardRadius1.setRateCardId(rateCardRadius.getRateCardId());
                            rateCardRadius1.setProjectCode(rateCardRadius.getProjectCode());
                            rateCardRadius1.setBusinessCode(rateCardRadius.getBusinessCode());
                            rateCardRadius1.setRuleCode(rateCardRadius.getRuleCode());
                            rateCardRadius1.setRateRangeContent(map.get("rateRangeContent"));
                            rateCardRadius1.setStandardRate(map.get("name"));
                            rateCardRadius1.setRateRange(map.get("value"));
                            rateCardRadius1.setDeptFlag(ruoYiService.getStandardDepts().contains(loginUser.getSysUser().getDeptId()) ? 1 : 2);
                            rateCardRadius1.setDeptId(loginUser.getSysUser().getDeptId().intValue());
                            rateCardRadius1.setButtonState(1);
                            rateCardRadius1.setCreateTime(LocalDateTime.now());
                            rateCardRadius1.setCreateUserId(loginUser.getUserid().intValue());
                            rateCardRadiusMapper.insert(rateCardRadius1);
                        }
                    });
                } else {
                    // 首次创建评级规则
                    mapList.forEach(map -> {
                        RateCardRadius rateCardRadius1 = new RateCardRadius();
                        rateCardRadius1.setRateCardId(rateCardRadius.getRateCardId());
                        rateCardRadius1.setProjectCode(rateCardRadius.getProjectCode());
                        rateCardRadius1.setBusinessCode(rateCardRadius.getBusinessCode());
                        rateCardRadius1.setRuleCode(rateCardRadius.getRuleCode());
                        rateCardRadius1.setRateRangeContent(map.get("rateRangeContent"));
                        rateCardRadius1.setStandardRate(map.get("name"));
                        rateCardRadius1.setRateRange(map.get("value"));
                        rateCardRadius1.setDeptFlag(ruoYiService.getStandardDepts().contains(loginUser.getSysUser().getDeptId()) ? 1 : 2);
                        rateCardRadius1.setDeptId(loginUser.getSysUser().getDeptId().intValue());
                        rateCardRadius1.setButtonState(1);
                        rateCardRadius1.setCreateTime(LocalDateTime.now());
                        rateCardRadius1.setCreateUserId(loginUser.getUserid().intValue());
                        rateCardRadiusMapper.insert(rateCardRadius1);
                    });

                    // 只在首次创建时才创建额度卡和定价卡
                    //新增定额信息
                    QuotaCardRecord quotaCardRecord = new QuotaCardRecord();
                    quotaCardRecord.setQuotaCard(rateCardRecord != null ? rateCardRecord.getRateCard() : null);
                    quotaCardRecord.setRateCardId(rateCardRadius.getRateCardId());
                    quotaCardRecord.setDataState(0);
                    quotaCardRecord.setProjectCode(rateCardRadius.getProjectCode());
                    quotaCardRecord.setBusinessCode(rateCardRadius.getBusinessCode());
                    quotaCardRecord.setRuleCode("3");
                    quotaCardRecord.setDeptFlag(ruoYiService.getStandardDepts().contains(loginUser.getSysUser().getDeptId()) ? 1 : 2);
                    quotaCardRecord.setDeptId(loginUser.getSysUser().getDeptId().intValue());
                    quotaCardRecord.setButtonState(0);
                    quotaCardRecord.setCreateTime(LocalDateTime.now());
                    quotaCardRecord.setCreateUserId(loginUser.getUserid().intValue());
                    quotaCardRecord.setDescription(rateCardRecord.getDescription());
                    quotaCardRecordMapper.insert(quotaCardRecord);

                    //新增定价信息
                    PriceCardRecord priceCardRecord = new PriceCardRecord();
                    priceCardRecord.setPriceCard(rateCardRecord != null ? rateCardRecord.getRateCard() : null);
                    priceCardRecord.setRateCardId(rateCardRadius.getRateCardId());
                    priceCardRecord.setDataState(0);
                    priceCardRecord.setProjectCode(rateCardRadius.getProjectCode());
                    priceCardRecord.setBusinessCode(rateCardRadius.getBusinessCode());
                    priceCardRecord.setRuleCode("4");
                    priceCardRecord.setDeptFlag(ruoYiService.getStandardDepts().contains(loginUser.getSysUser().getDeptId()) ? 1 : 2);
                    priceCardRecord.setDeptId(loginUser.getSysUser().getDeptId().intValue());
                    priceCardRecord.setButtonState(0);
                    priceCardRecord.setCreateTime(LocalDateTime.now());
                    priceCardRecord.setCreateUserId(loginUser.getUserid().intValue());
                    priceCardRecord.setDescription(rateCardRecord.getDescription());
                    priceCardRecordMapper.insert(priceCardRecord);
                }
            }
        } else {
            if (mapList.size() != 0) {
                mapList.forEach(map -> {
                    RateCardRadius rateCardRadius1 = new RateCardRadius();
                    rateCardRadius1.setRateRangeContent(map.get("rateRangeContent"));
                    rateCardRadius1.setRateRange(map.get("value"));
                    rateCardRadius1.setStandardRate(map.get("name"));
                    rateCardRadius1.setUpdateTime(LocalDateTime.now());
                    rateCardRadiusMapper.update(rateCardRadius1, new LambdaQueryWrapper<RateCardRadius>().eq(RateCardRadius::getId, map.get("id")));
                });
            }
        }
        return AjaxResult.success();
    }

    @Override
    public AjaxResult get(RateCardRadius rateCardRadius, LoginUser loginUser) {
        if (rateCardRadius.getRateCardId() == null) {
            return AjaxResult.error("评级卡id不可为空");
        }
        List<RateCardRadius> rateCardRadii = rateCardRadiusMapper.selectList(new LambdaQueryWrapper<RateCardRadius>()
                .eq(RateCardRadius::getRateCardId, rateCardRadius.getRateCardId())
                .orderByAsc(RateCardRadius::getCreateTime));
        List<HashMap<String, String>> hashMapList = new ArrayList<>();
        rateCardRadii.forEach(map -> {
            HashMap<String, String> resultMap = new HashMap<>();
            resultMap.put("id", map.getId().toString());
            resultMap.put("name", map.getStandardRate());
            resultMap.put("value", map.getRateRange());
            resultMap.put("rateRangeContent", map.getRateRangeContent());
            hashMapList.add(resultMap);
        });
        return AjaxResult.success(hashMapList);
    }
}
