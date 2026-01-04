package com.value.decision.model.decisionmanage.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.value.decision.model.decisionmanage.mapper.RateCardRadiusMapper;
import com.value.decision.model.decisionmanage.mapper.RateCardRecordMapper;
import com.value.decision.model.decisionmanage.model.RateCardRadius;
import com.value.decision.model.decisionmanage.model.RateCardRadiusSnapshot;
import com.value.decision.model.decisionmanage.model.RateCardRecord;
import com.value.decision.model.decisionmanage.model.RateCardRecordSnapshot;
import com.value.decision.model.decisionmanage.service.IRateCardRadiusSnapshotService;
import com.value.decision.model.decisionmanage.service.IRateCardRecordService;
import com.value.decision.model.decisionmanage.service.IRateCardRecordSnapshotService;
import com.risksmart.common.core.web.AjaxResult;
import com.value.decision.common.security.LoginUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 评级卡主表 服务实现类
 * </p>
 *
 * @author hc
 * @since 2023-08-10
 */
@Service
public class RateCardRecordServiceImpl extends ServiceImpl<RateCardRecordMapper, RateCardRecord> implements IRateCardRecordService {

    @Resource
    private RateCardRecordMapper rateCardRecordMapper;

    @Resource
    private RateCardRadiusMapper rateCardRadiusMapper;

    @Override
    public AjaxResult updateStatus(RateCardRecord rateCardRecord) {
        if (rateCardRecord.getId() == null || rateCardRecord.getButtonState() == null) {
            return AjaxResult.error("参数缺失");
        }
        //查询评级范围表 不存在则提示无法更新状态
        if (rateCardRadiusMapper.selectCount(new LambdaQueryWrapper<RateCardRadius>().eq(RateCardRadius::getRateCardId,rateCardRecord.getId())) == 0) {
            return AjaxResult.error("当前评级卡的评级规则表未提交,请先完善评级规则表");
        }
        RateCardRecord rateCardRecord1 = new RateCardRecord();
        rateCardRecord1.setButtonState(rateCardRecord.getButtonState());
        rateCardRecordMapper.update(rateCardRecord1, new LambdaQueryWrapper<RateCardRecord>().eq(RateCardRecord::getId, rateCardRecord.getId()));
        return AjaxResult.success();
    }

    @Override
    public AjaxResult newList(RateCardRecord rateCardRecord, LoginUser loginUser) {
        Long deptId;
        if(rateCardRecord.getDeptId() != null){
            deptId = Long.valueOf(rateCardRecord.getDeptId());
        }else{
            deptId = loginUser.getSysUser().getDeptId();
        }
        LambdaQueryWrapper<RateCardRecord> like = new LambdaQueryWrapper<RateCardRecord>();
        like.like(RateCardRecord::getRateCard, rateCardRecord.getRateCard());
        like.eq(RateCardRecord::getProjectCode,rateCardRecord.getProjectCode());
        like.eq(RateCardRecord::getDeptId,deptId);
        like.orderByDesc(RateCardRecord::getCreateTime);
        return AjaxResult.success(rateCardRecordMapper.selectList(like));
    }

    @Resource
    private IRateCardRecordSnapshotService rateCardRecordSnapshotService;

    @Resource
    private IRateCardRadiusSnapshotService rateCardRadiusSnapshotService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult release(RateCardRecord rateCardRecord, LoginUser loginUser) {
        if (rateCardRecord.getProjectCode() == null || rateCardRecord.getBusinessCode() == null || rateCardRecord.getRuleCode() == null) {
            return AjaxResult.error("参数缺失");
        }
        Integer deptId = loginUser.getSysUser().getDeptId().intValue();
        LambdaQueryWrapper<RateCardRecord> eq = new LambdaQueryWrapper<RateCardRecord>()
                .eq(RateCardRecord::getDeptId, deptId)
                .eq(RateCardRecord::getProjectCode, rateCardRecord.getProjectCode())
                .eq(RateCardRecord::getBusinessCode, rateCardRecord.getBusinessCode())
                .eq(RateCardRecord::getRuleCode, rateCardRecord.getRuleCode());
        LambdaQueryWrapper<RateCardRecordSnapshot> eqS = new LambdaQueryWrapper<RateCardRecordSnapshot>()
                .eq(RateCardRecordSnapshot::getDeptId, deptId)
                .eq(RateCardRecordSnapshot::getProjectCode, rateCardRecord.getProjectCode())
                .eq(RateCardRecordSnapshot::getBusinessCode, rateCardRecord.getBusinessCode())
                .eq(RateCardRecordSnapshot::getRuleCode, rateCardRecord.getRuleCode());
        rateCardRecordSnapshotService.remove(eqS);
        List<RateCardRecord> rateCardRecords = rateCardRecordMapper.selectList(eq);
        List<RateCardRecordSnapshot> rateCardRecordSnapshots = new ArrayList<>();
        rateCardRecordSnapshots = JSON.parseArray(JSON.toJSONString(rateCardRecords), RateCardRecordSnapshot.class);
        if (rateCardRecordSnapshots != null) {
            rateCardRecordSnapshotService.saveBatch(rateCardRecordSnapshots);
        }

        LambdaQueryWrapper<RateCardRadius> eq1 = new LambdaQueryWrapper<RateCardRadius>()
                .eq(RateCardRadius::getDeptId, deptId)
                .eq(RateCardRadius::getProjectCode, rateCardRecord.getProjectCode())
                .eq(RateCardRadius::getBusinessCode, rateCardRecord.getBusinessCode())
                .eq(RateCardRadius::getRuleCode, rateCardRecord.getRuleCode());
        LambdaQueryWrapper<RateCardRadiusSnapshot> eqS1 = new LambdaQueryWrapper<RateCardRadiusSnapshot>()
                .eq(RateCardRadiusSnapshot::getDeptId, deptId)
                .eq(RateCardRadiusSnapshot::getProjectCode, rateCardRecord.getProjectCode())
                .eq(RateCardRadiusSnapshot::getBusinessCode, rateCardRecord.getBusinessCode())
                .eq(RateCardRadiusSnapshot::getRuleCode, rateCardRecord.getRuleCode());
        rateCardRadiusSnapshotService.remove(eqS1);
        List<RateCardRadius> rateCardRadiusList = rateCardRadiusMapper.selectList(eq1);
        List<RateCardRadiusSnapshot> rateCardRadiusSnapshots = new ArrayList<>();
        rateCardRadiusSnapshots = JSON.parseArray(JSON.toJSONString(rateCardRadiusList), RateCardRadiusSnapshot.class);
        if (rateCardRadiusSnapshots != null) {
            rateCardRadiusSnapshotService.saveBatch(rateCardRadiusSnapshots);
        }

        return AjaxResult.success();
    }
}
