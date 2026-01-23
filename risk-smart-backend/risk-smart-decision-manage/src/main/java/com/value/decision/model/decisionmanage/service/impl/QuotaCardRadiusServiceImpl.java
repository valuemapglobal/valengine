package com.value.decision.model.decisionmanage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.value.decision.model.decisionmanage.mapper.QuotaCardRadiusMapper;
import com.value.decision.model.decisionmanage.mapper.QuotaCardRecordMapper;
import com.value.decision.model.decisionmanage.model.QuotaCardRadius;
import com.value.decision.model.decisionmanage.model.QuotaCardRecord;
import com.value.decision.model.decisionmanage.service.IQuotaCardRadiusService;
import com.value.decision.model.decisionmanage.service.RuoYiService;
import com.risksmart.common.core.web.AjaxResult;
import com.value.decision.common.security.LoginUser;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 额度范围表 服务实现类
 * </p>
 *
 * @author hc
 * @since 2023-08-12
 */
@Slf4j
@Service
public class QuotaCardRadiusServiceImpl extends ServiceImpl<QuotaCardRadiusMapper, QuotaCardRadius> implements IQuotaCardRadiusService {

    @Resource
    private RuoYiService ruoYiService;

    @Resource
    private QuotaCardRadiusMapper quotaCardRadiusMapper;

    @Resource
    private QuotaCardRecordMapper quotaCardRecordMapper;

    @Override
    public AjaxResult submit(QuotaCardRadius quotaCardRadius, LoginUser loginUser) {
        List<Map<String, String>> mapList = quotaCardRadius.getMapList();

        // 判断是新增还是修改: 检查mapList中第一个元素是否有id字段
        boolean isUpdate = mapList.size() > 0 && mapList.get(0).containsKey("id") && mapList.get(0).get("id") != null;

        // 调试日志
        log.info("【额度规则提交】quotaCardRadius.getId()={}, mapList.size()={}, isUpdate={}, quotaCardId={}",
                quotaCardRadius.getId(), mapList.size(), isUpdate, quotaCardRadius.getQuotaCardId());
        if (mapList.size() > 0) {
            log.info("【额度规则提交】mapList第一个元素: {}", mapList.get(0));
        }

        //id不存在 为新增
        if(quotaCardRadius.getId() == null && !isUpdate){
            if(mapList.size()!=0){
                //判断是否存在额度卡
                QuotaCardRecord quotaCardRecord = quotaCardRecordMapper.selectOne(new LambdaQueryWrapper<QuotaCardRecord>().eq(QuotaCardRecord::getId, quotaCardRadius.getQuotaCardId()));
                if (quotaCardRecord == null) {
                    return AjaxResult.error("额度卡id不存在");
                }

                //判断该额度规则是否已存在
                List<QuotaCardRadius> existingRecords = quotaCardRadiusMapper.selectList(
                        new LambdaQueryWrapper<QuotaCardRadius>().eq(QuotaCardRadius::getQuotaCardId, quotaCardRadius.getQuotaCardId()));
                log.info("【额度规则提交-新增】检查是否存在: quotaCardId={}, existCount={}", quotaCardRadius.getQuotaCardId(), existingRecords.size());

                // 如果已存在,则删除旧记录,准备重新插入
                if (!existingRecords.isEmpty()) {
                    log.info("【额度规则提交】已存在额度规则,删除旧记录后重新插入, quotaCardId={}", quotaCardRadius.getQuotaCardId());
                    quotaCardRadiusMapper.delete(new LambdaQueryWrapper<QuotaCardRadius>().eq(QuotaCardRadius::getQuotaCardId, quotaCardRadius.getQuotaCardId()));
                }

                mapList.forEach(map->{
                    QuotaCardRadius quotaCardRadius1 = new QuotaCardRadius();
                    quotaCardRadius1.setQuotaCardId(quotaCardRadius.getQuotaCardId());
                    quotaCardRadius1.setStandardRate(quotaCardRadius.getStandardRate());
                    quotaCardRadius1.setCreditSource(quotaCardRadius.getCreditSource());
                    quotaCardRadius1.setProjectCode(quotaCardRadius.getProjectCode());
                    quotaCardRadius1.setBusinessCode(quotaCardRadius.getBusinessCode());
                    quotaCardRadius1.setRuleCode(quotaCardRadius.getRuleCode());
                    quotaCardRadius1.setQuotaRangeContent(map.get("quotaRangeContent"));
                    quotaCardRadius1.setStandardName(map.get("name"));
                    quotaCardRadius1.setQuotaRange(map.get("value") ==null ? "0":map.get("value"));
                    quotaCardRadius1.setDeptFlag(ruoYiService.getStandardDepts().contains(loginUser.getSysUser().getDeptId()) ? 1 : 2);
                    quotaCardRadius1.setDeptId(loginUser.getSysUser().getDeptId().intValue());
                    quotaCardRadius1.setButtonState(1);
                    quotaCardRadius1.setCreateTime(LocalDateTime.now());
                    quotaCardRadius1.setCreateUserId(loginUser.getUserid().intValue());
                    quotaCardRadiusMapper.insert(quotaCardRadius1);
                });
            }
        }else {
            log.info("【额度规则提交-修改】进入修改分支, quotaCardId={}", quotaCardRadius.getQuotaCardId());
            if(mapList.size()!=0){
                mapList.forEach(map->{
                    log.info("【额度规则提交-修改】更新记录: id={}, name={}, value={}", map.get("id"), map.get("name"), map.get("value"));
                    QuotaCardRadius quotaCardRadius1 = new QuotaCardRadius();
                    quotaCardRadius1.setStandardName(map.get("name"));
                    quotaCardRadius1.setQuotaRange(map.get("value") ==null ? "0":map.get("value"));
                    quotaCardRadius1.setQuotaRangeContent(map.get("quotaRangeContent"));
                    quotaCardRadius1.setUpdateTime(LocalDateTime.now());
                    quotaCardRadiusMapper.update(quotaCardRadius1, new LambdaQueryWrapper<QuotaCardRadius>().eq(QuotaCardRadius::getId,map.get("id")));
                });
            }
        }
        return AjaxResult.success();
    }

    @Override
    public AjaxResult get(QuotaCardRadius quotaCardRadius, LoginUser loginUser) {
        if (quotaCardRadius.getQuotaCardId() == null) {
            return AjaxResult.error("额度卡id不可为空");
        }
        List<QuotaCardRadius> quotaCardRadii = quotaCardRadiusMapper.selectList(new LambdaQueryWrapper<QuotaCardRadius>()
                .eq(QuotaCardRadius::getQuotaCardId, quotaCardRadius.getQuotaCardId())
                .orderByAsc(QuotaCardRadius::getCreateTime));
        List<HashMap<String, String>> hashMapList = new ArrayList<>();
        quotaCardRadii.forEach(map -> {
            HashMap<String, String> resultMap = new HashMap<>();
            resultMap.put("id",map.getId().toString());
            resultMap.put("name", map.getStandardName());
            resultMap.put("value", map.getQuotaRange());
            resultMap.put("quotaRangeContent", map.getQuotaRangeContent());
            resultMap.put("standardRate", map.getStandardRate());
            resultMap.put("creditSource", map.getCreditSource());
            hashMapList.add(resultMap);
        });
        return AjaxResult.success(hashMapList);
    }
}
