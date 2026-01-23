package com.value.decision.model.decisionmanage.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.value.decision.model.decisionmanage.mapper.PriceCardRadiusMapper;
import com.value.decision.model.decisionmanage.mapper.PriceCardRecordMapper;
import com.value.decision.model.decisionmanage.model.PriceCardRadius;
import com.value.decision.model.decisionmanage.model.PriceCardRadiusSnapshot;
import com.value.decision.model.decisionmanage.model.PriceCardRecord;
import com.value.decision.model.decisionmanage.model.PriceCardRecordSnapshot;
import com.value.decision.model.decisionmanage.service.IPriceCardRadiusSnapshotService;
import com.value.decision.model.decisionmanage.service.IPriceCardRecordService;
import com.value.decision.model.decisionmanage.service.IPriceCardRecordSnapshotService;
import com.risksmart.common.core.web.AjaxResult;
import com.value.decision.common.security.LoginUser;
import com.value.decision.model.decisionmanage.model.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 定价卡主表 服务实现类
 * </p>
 *
 * @author hc
 * @since 2023-08-12
 */
@Service
public class PriceCardRecordServiceImpl extends ServiceImpl<PriceCardRecordMapper, PriceCardRecord> implements IPriceCardRecordService {

    @Resource
    private PriceCardRecordMapper priceCardRecordMapper;

    @Override
    public AjaxResult newList(PriceCardRecord priceCardRecord,LoginUser loginUser) {
        LambdaQueryWrapper<PriceCardRecord> priceCardRecordLambdaQueryWrapper = new LambdaQueryWrapper<>();
        priceCardRecordLambdaQueryWrapper.like(PriceCardRecord::getPriceCard,priceCardRecord.getPriceCard());
        if (StrUtil.isNotBlank(priceCardRecord.getProjectCode())) {
            priceCardRecordLambdaQueryWrapper.eq(PriceCardRecord::getProjectCode,priceCardRecord.getProjectCode());
        }
        priceCardRecordLambdaQueryWrapper.eq(PriceCardRecord::getBusinessCode,priceCardRecord.getBusinessCode());
        priceCardRecordLambdaQueryWrapper.eq(PriceCardRecord::getRuleCode,priceCardRecord.getRuleCode());
        priceCardRecordLambdaQueryWrapper.eq(PriceCardRecord::getDeptId,loginUser.getSysUser().getDeptId())
                .orderByDesc(PriceCardRecord::getCreateTime);
        return AjaxResult.success(priceCardRecordMapper.selectList(priceCardRecordLambdaQueryWrapper));
    }

    @Override
    public AjaxResult updateStatus(PriceCardRecord priceCardRecord) {
        if (priceCardRecord.getId() == null || priceCardRecord.getButtonState() == null) {
            return AjaxResult.error("参数缺失");
        }
        //查询定价范围表 不存在则提示无法更新状态
        if (priceCardRadiusMapper.selectCount(new LambdaQueryWrapper<PriceCardRadius>().eq(PriceCardRadius::getPriceCardId,priceCardRecord.getId())) == 0) {
            return AjaxResult.error("当前定价规则未提交,请先完善风险定价规则");
        }
        PriceCardRecord priceCardRecord1 = new PriceCardRecord();
        priceCardRecord1.setButtonState(priceCardRecord.getButtonState());
        priceCardRecordMapper.update(priceCardRecord1, new LambdaQueryWrapper<PriceCardRecord>().eq(PriceCardRecord::getId, priceCardRecord.getId()));
        return AjaxResult.success();
    }

    @Resource
    private IPriceCardRadiusSnapshotService priceCardRadiusSnapshotService;

    @Resource
    private IPriceCardRecordSnapshotService priceCardRecordSnapshotService;

    @Resource
    private PriceCardRadiusMapper priceCardRadiusMapper;



    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult release(PriceCardRecord priceCardRecord, LoginUser loginUser) {
        if (priceCardRecord.getProjectCode() == null || priceCardRecord.getBusinessCode() == null || priceCardRecord.getRuleCode() == null) {
            return AjaxResult.error("参数缺失");
        }
        Integer deptId = loginUser.getSysUser().getDeptId().intValue();
        LambdaQueryWrapper<PriceCardRecord> eq = new LambdaQueryWrapper<PriceCardRecord>()
                .eq(PriceCardRecord::getDeptId, deptId)
                .eq(PriceCardRecord::getProjectCode, priceCardRecord.getProjectCode())
                .eq(PriceCardRecord::getBusinessCode, priceCardRecord.getBusinessCode())
                .eq(PriceCardRecord::getRuleCode, priceCardRecord.getRuleCode());
        LambdaQueryWrapper<PriceCardRecordSnapshot> eqS = new LambdaQueryWrapper<PriceCardRecordSnapshot>()
                .eq(PriceCardRecordSnapshot::getDeptId, deptId)
                .eq(PriceCardRecordSnapshot::getProjectCode, priceCardRecord.getProjectCode())
                .eq(PriceCardRecordSnapshot::getBusinessCode, priceCardRecord.getBusinessCode())
                .eq(PriceCardRecordSnapshot::getRuleCode, priceCardRecord.getRuleCode());
        priceCardRecordSnapshotService.remove(eqS);
        List<PriceCardRecord> rateCardRecords = priceCardRecordMapper.selectList(eq);
        List<PriceCardRecordSnapshot> quotaCardRecordSnapshots = new ArrayList<>();
        quotaCardRecordSnapshots = JSON.parseArray(JSON.toJSONString(rateCardRecords), PriceCardRecordSnapshot.class);
        if (quotaCardRecordSnapshots != null) {
            priceCardRecordSnapshotService.saveBatch(quotaCardRecordSnapshots);
        }

        LambdaQueryWrapper<PriceCardRadius> eq1 = new LambdaQueryWrapper<PriceCardRadius>()
                .eq(PriceCardRadius::getDeptId, deptId)
                .eq(PriceCardRadius::getProjectCode, priceCardRecord.getProjectCode())
                .eq(PriceCardRadius::getBusinessCode, priceCardRecord.getBusinessCode())
                .eq(PriceCardRadius::getRuleCode, priceCardRecord.getRuleCode());
        LambdaQueryWrapper<PriceCardRadiusSnapshot> eqS1 = new LambdaQueryWrapper<PriceCardRadiusSnapshot>()
                .eq(PriceCardRadiusSnapshot::getDeptId, deptId)
                .eq(PriceCardRadiusSnapshot::getProjectCode, priceCardRecord.getProjectCode())
                .eq(PriceCardRadiusSnapshot::getBusinessCode, priceCardRecord.getBusinessCode())
                .eq(PriceCardRadiusSnapshot::getRuleCode, priceCardRecord.getRuleCode());
        priceCardRadiusSnapshotService.remove(eqS1);
        List<PriceCardRadius> quotaCardRadii = priceCardRadiusMapper.selectList(eq1);
        List<PriceCardRadiusSnapshot> quotaCardRadiusSnapshots = new ArrayList<>();
        quotaCardRadiusSnapshots = JSON.parseArray(JSON.toJSONString(quotaCardRadii), PriceCardRadiusSnapshot.class);
        if (quotaCardRadiusSnapshots != null) {
            priceCardRadiusSnapshotService.saveBatch(quotaCardRadiusSnapshots);
        }

        return AjaxResult.success();
    }
}
