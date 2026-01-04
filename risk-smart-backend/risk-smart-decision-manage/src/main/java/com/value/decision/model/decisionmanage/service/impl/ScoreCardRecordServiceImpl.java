package com.value.decision.model.decisionmanage.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.value.decision.common.utils.StringUtils;
import com.value.decision.model.decisionmanage.constants.RuleConstants;
import com.value.decision.model.decisionmanage.mapper.*;
import com.value.decision.model.decisionmanage.model.*;
import com.value.decision.model.decisionmanage.model.vo.ScoreCardRecordVO;
import com.value.decision.model.decisionmanage.service.*;
import com.value.decision.process.mapper.ProcessNodeMapper;
import com.value.decision.process.mapper.ProcessPolicyMapper;
import com.value.decision.version.common.util.StringUtil;
import com.value.decision.version.domain.ModelVersionClassification;
import com.value.decision.version.domain.ScoreCardRecordVersion;
import com.value.decision.version.mapper.ModelVersionClassificationMapper;
import com.value.decision.version.mapper.ScoreCardRecordVersionMapper;
import com.value.decision.version.service.ScoreCardRecordVersionService;
import com.value.decision.version.vo.VersionControlVO;
import com.risksmart.common.core.web.AjaxResult;
import com.value.decision.common.security.LoginUser;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


/**
 * <p>
 * 评分卡主表 服务实现类
 * </p>
 *
 * @author hc
 * @since 2023-08-08
 */
@Log4j2
@Service
public class ScoreCardRecordServiceImpl extends ServiceImpl<ScoreCardRecordMapper, ScoreCardRecord> implements IScoreCardRecordService {

    @Resource
    private ScoreCardRecordMapper scoreCardRecordMapper;

    @Resource
    private ScoreCardRecordSnapshotMapper scoreCardRecordSnapshotMapper;


    @Resource
    private ScoreIndexRuleMapper scoreIndexRuleMapper;

    @Resource
    private ScorePrimaryIndexMapper scorePrimaryIndexMapper;

    @Resource
    private RateCardRecordMapper rateCardRecordMapper;

    @Resource
    private PriceCardRecordMapper priceCardRecordMapper;

    @Autowired
    private ModelVersionClassificationMapper modelVersionClassificationMapper;

    @Autowired
    private ScoreCardRecordVersionService scoreCardRecordVersionService;
    @Resource
    private RuoYiService ruoYiService;

    @Autowired
    private ScoreCardReuseMapper scoreCardReuseMapper;

    @Autowired
    private ScoreCardRecordVersionMapper scoreCardRecordVersionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult submit(ScoreCardRecord scoreCardRecord, LoginUser loginUser) {
        //同部门同业务场景同名验证
        LambdaQueryWrapper<ScoreCardRecord> eq = new LambdaQueryWrapper<ScoreCardRecord>()
                .eq(ScoreCardRecord::getDeptId, loginUser.getSysUser().getDeptId().intValue())
                .eq(ScoreCardRecord::getBusinessCode, scoreCardRecord.getBusinessCode())
                .eq(ScoreCardRecord::getScoreCard, scoreCardRecord.getScoreCard())
                .eq(ScoreCardRecord::getRuleCode, scoreCardRecord.getRuleCode())
                .eq(ScoreCardRecord::getProjectCode, scoreCardRecord.getProjectCode());
        if (scoreCardRecord.getId() != null) {
            eq.ne(ScoreCardRecord::getId, scoreCardRecord.getId());
        } else {
            eq.isNotNull(ScoreCardRecord::getId);
        }
        if (scoreCardRecordMapper.selectList(eq).size() != 0) {
            return AjaxResult.error("不能同部门同业务场景评分卡同名");
        }
        if (scoreCardRecord.getId() == null) {
//            scoreCardRecord.setDeptFlag(adminDeptId.equals(loginUser.getSysUser().getDeptId().toString()) ? 1 : 0);
            scoreCardRecord.setDeptFlag(ruoYiService.getStandardDepts().contains(loginUser.getSysUser().getDeptId()) ? 1 : 2);
            scoreCardRecord.setDeptId(loginUser.getSysUser().getDeptId().intValue());
            scoreCardRecord.setButtonState(1);
            scoreCardRecord.setCreateTime(LocalDateTime.now());
            scoreCardRecord.setCreateUserId(loginUser.getUserid().intValue());
            scoreCardRecord.setDataState(0);
            //最新版本号去通过版本生成方法生成修改后的版本号
            String versionNum = StringUtil.getVersionNum(null,scoreCardRecord.getProjectName(),scoreCardRecord.getBusinessName(),scoreCardRecord.getPersonOrCompany());
            scoreCardRecord.setVersionControl(versionNum);
            scoreCardRecord.setModelScore(scoreCardRecord.getModelScore());
            scoreCardRecordMapper.insert(scoreCardRecord);
            //插入评级卡
            RateCardRecord rateCardRecord = new RateCardRecord();
            rateCardRecord.setScoreCardId(scoreCardRecord.getId());
            rateCardRecord.setRateCard(scoreCardRecord.getScoreCard());
            rateCardRecord.setDataState(0);
            rateCardRecord.setProjectCode(scoreCardRecord.getProjectCode());
            rateCardRecord.setBusinessCode(scoreCardRecord.getBusinessCode());
            rateCardRecord.setRuleCode("2");
//            rateCardRecord.setDeptFlag(adminDeptId.equals(loginUser.getSysUser().getDeptId().toString()) ? 1 : 0);
            rateCardRecord.setDeptFlag(ruoYiService.getStandardDepts().contains(loginUser.getSysUser().getDeptId()) ? 1 : 2);
            rateCardRecord.setDeptId(loginUser.getSysUser().getDeptId().intValue());
            rateCardRecord.setButtonState(0);
            rateCardRecord.setCreateTime(LocalDateTime.now());
            rateCardRecord.setCreateUserId(loginUser.getUserid().intValue());
            rateCardRecord.setVersionControl(scoreCardRecord.getVersionControl());
            rateCardRecord.setDescription(scoreCardRecord.getDescription());
            rateCardRecordMapper.insert(rateCardRecord);

            //版本归类表插入新纪录 只更新版本号字段
            ModelVersionClassification modelVersionClassification = new ModelVersionClassification();
            modelVersionClassification.setModelId(scoreCardRecord.getId());
            modelVersionClassification.setModelName(scoreCardRecord.getModelName());
            modelVersionClassification.setVersionControl(versionNum);
            modelVersionClassification.setCreateTime(new Date());
            modelVersionClassification.setProjectCode(scoreCardRecord.getProjectCode());
            modelVersionClassification.setBusinessCode(scoreCardRecord.getBusinessCode());
            modelVersionClassification.setRuleCode(scoreCardRecord.getRuleCode());
            modelVersionClassification.setDeptId(loginUser.getSysUser().getDeptId().intValue());
            modelVersionClassification.setNewVersion(1);
            modelVersionClassificationMapper.insert(modelVersionClassification);
            //保留原版本
            VersionControlVO versionControlVO = new VersionControlVO();
            versionControlVO.setDeptId(loginUser.getSysUser().getDeptId().intValue());
            versionControlVO.setBusinessCode(scoreCardRecord.getBusinessCode());
            versionControlVO.setProjectCode(scoreCardRecord.getProjectCode());
            versionControlVO.setRuleCode(scoreCardRecord.getRuleCode());
            versionControlVO.setModelName(scoreCardRecord.getModelName());
            versionControlVO.setBusinessName(scoreCardRecord.getBusinessName());
            versionControlVO.setPersonOrCompany(scoreCardRecord.getPersonOrCompany());
            scoreCardRecordVersionService.versionReserveScore(versionControlVO);

        } else {
            RateCardRecord rateCardRecord1 = rateCardRecordMapper.selectOne(new LambdaQueryWrapper<RateCardRecord>()
                    .eq(RateCardRecord::getScoreCardId, scoreCardRecord.getId()));
            if (rateCardRecord1 != null) {
                //更新额度 定价
                QuotaCardRecord quotaCardRecord = new QuotaCardRecord();
                quotaCardRecord.setQuotaCard(rateCardRecord1.getRateCard());
                quotaCardRecord.setUpdateTime(LocalDateTime.now());
                quotaCardRecord.setVersionControl(rateCardRecord1.getVersionControl());
                quotaCardRecord.setDescription(rateCardRecord1.getDescription());
                quotaCardRecordMapper.update(quotaCardRecord, new LambdaQueryWrapper<QuotaCardRecord>().eq(QuotaCardRecord::getRateCardId, rateCardRecord1.getId()));

                PriceCardRecord priceCardRecord = new PriceCardRecord();
                priceCardRecord.setPriceCard(rateCardRecord1.getRateCard());
                priceCardRecord.setUpdateTime(LocalDateTime.now());
                priceCardRecord.setVersionControl(rateCardRecord1.getVersionControl());
                priceCardRecord.setDescription(rateCardRecord1.getDescription());
                priceCardRecordMapper.update(priceCardRecord, new LambdaQueryWrapper<PriceCardRecord>().eq(PriceCardRecord::getRateCardId, rateCardRecord1.getId()));
            }


            //更新评级卡
            RateCardRecord rateCardRecord = new RateCardRecord();
            rateCardRecord.setRateCard(scoreCardRecord.getScoreCard());
            rateCardRecord.setUpdateTime(LocalDateTime.now());
            rateCardRecord.setVersionControl(scoreCardRecord.getVersionControl());


            rateCardRecord.setDescription(scoreCardRecord.getDescription());
            rateCardRecordMapper.update(rateCardRecord, new LambdaQueryWrapper<RateCardRecord>()
                    .eq(RateCardRecord::getScoreCardId, scoreCardRecord.getId()));

            scoreCardRecord.setDataState(0);
            scoreCardRecord.setUpdateTime(LocalDateTime.now());
            scoreCardRecordMapper.updateById(scoreCardRecord);
        }
        return AjaxResult.success();
    }

    @Override
    public List<ScoreCardRecord> newlist(ScoreCardRecordVO scoreCardRecord, LoginUser loginUser) {
        LambdaQueryWrapper<ScoreCardRecord> scoreCardRecordLambdaQueryWrapper = new LambdaQueryWrapper<>();
        scoreCardRecordLambdaQueryWrapper.eq(ScoreCardRecord::getDeptId, loginUser.getSysUser().getDeptId())
                .eq(ScoreCardRecord::getProjectCode, scoreCardRecord.getProjectCode())
                .eq(ScoreCardRecord::getBusinessCode, scoreCardRecord.getBusinessCode())
                .eq(ScoreCardRecord::getDataState,0)
                .orderByDesc(ScoreCardRecord::getCreateTime);
        List<ScoreCardRecord> scoreCardRecordList = scoreCardRecordMapper.selectList(scoreCardRecordLambdaQueryWrapper);
        //关联表查询是否有引用标准评分卡
        LambdaQueryWrapper<ScoreCardReuse> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(ScoreCardReuse::getBuildBusinessCode,scoreCardRecord.getBusinessCode())
                .eq(ScoreCardReuse::getBuildProjectCode,scoreCardRecord.getProjectCode())
                .eq(ScoreCardReuse::getBuildRuleCode,scoreCardRecord.getRuleCode())
                .eq(ScoreCardReuse::getDeptId,loginUser.getSysUser().getDeptId())
                .eq(ScoreCardReuse::getDataStatus,0);
        List<ScoreCardReuse> scoreCardReuseList = scoreCardReuseMapper.selectList(wrapper);
        if (CollectionUtils.isNotEmpty(scoreCardReuseList)){
            scoreCardReuseList.stream().forEach(x ->{
                LambdaQueryWrapper<ScoreCardRecordVersion> wrapperVersion = Wrappers.lambdaQuery();
                wrapperVersion.eq(ScoreCardRecordVersion::getId,x.getParentCardId())
                        .eq(ScoreCardRecordVersion::getDeptFlag,1)
                        .eq(ScoreCardRecordVersion::getVersionControl,x.getVersionControl())
                        .eq(ScoreCardRecordVersion::getDataState,0);
                List<ScoreCardRecordVersion> scoreCardRecordVersionList = scoreCardRecordVersionMapper.selectList(wrapperVersion);
                ScoreCardRecord scoreCardRecordReuse = JSONObject.parseObject(JSON.toJSONString(scoreCardRecordVersionList.get(0)), ScoreCardRecord.class);
                scoreCardRecordReuse.setButtonState(x.getButtonState());
                scoreCardRecordList.add(scoreCardRecordReuse);
            });
        }

        //搜索
        if (StringUtils.isNotEmpty(scoreCardRecord.getScoreCard())){
            List<ScoreCardRecord> collect = scoreCardRecordList.stream().filter(x -> x.getScoreCard().contains(scoreCardRecord.getScoreCard())).collect(Collectors.toList());
            return collect;
        }

        return scoreCardRecordList;
    }


    @Override
    @Transactional
    public List<ScoreCardRecord> newBuildList(ScoreCardRecordVO scoreCardRecord, LoginUser loginUser) {
        LambdaQueryWrapper<ScoreCardRecord> scoreCardRecordLambdaQueryWrapper = new LambdaQueryWrapper<>();
        scoreCardRecordLambdaQueryWrapper.eq(ScoreCardRecord::getDeptId, loginUser.getSysUser().getDeptId())
                .eq(ScoreCardRecord::getProjectCode, scoreCardRecord.getProjectCode())
                .eq(ScoreCardRecord::getBusinessCode, scoreCardRecord.getBusinessCode())
                .eq(ScoreCardRecord::getDataState,0)
                .orderByDesc(ScoreCardRecord::getCreateTime);
        List<ScoreCardRecord> scoreCardRecordList = scoreCardRecordMapper.selectList(scoreCardRecordLambdaQueryWrapper);
        //关联表查询是否有引用标准评分卡
        LambdaQueryWrapper<ScoreCardReuse> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(ScoreCardReuse::getBuildBusinessCode,scoreCardRecord.getBusinessCode())
                .eq(ScoreCardReuse::getBuildProjectCode,scoreCardRecord.getProjectCode())
                .eq(ScoreCardReuse::getBuildRuleCode,1)
                .eq(ScoreCardReuse::getDeptId,loginUser.getSysUser().getDeptId())
                .eq(ScoreCardReuse::getDataStatus,0);
        List<ScoreCardReuse> scoreCardReuseList = scoreCardReuseMapper.selectList(wrapper);
        if (CollectionUtils.isNotEmpty(scoreCardReuseList)){
            scoreCardReuseList.stream().forEach(x ->{
                LambdaQueryWrapper<ScoreCardRecordVersion> wrapperVersion = Wrappers.lambdaQuery();
                wrapperVersion.eq(ScoreCardRecordVersion::getId,x.getParentCardId())
                        .eq(ScoreCardRecordVersion::getDeptFlag,1)
                        .eq(ScoreCardRecordVersion::getVersionControl,x.getVersionControl())
                        .eq(ScoreCardRecordVersion::getDataState,0);
                ScoreCardRecordVersion scoreCardRecordVersion = scoreCardRecordVersionMapper.selectOne(wrapperVersion);
                ScoreCardRecord scoreCardRecordReuse = JSONObject.parseObject(JSON.toJSONString(scoreCardRecordVersion), ScoreCardRecord.class);
                scoreCardRecordList.add(scoreCardRecordReuse);
            });
        }
        if (CollectionUtils.isNotEmpty(scoreCardRecord.getScoreCardDataList())){
            scoreCardRecordList.addAll(scoreCardRecord.getScoreCardDataList());
        }
        if (CollectionUtils.isNotEmpty(scoreCardRecord.getScoreCardDataDelList())){
            scoreCardRecordList.removeAll(scoreCardRecord.getScoreCardDataDelList());
        }
        if (StringUtils.isNotEmpty(scoreCardRecord.getScoreCard())){
            List<ScoreCardRecord> collect = scoreCardRecordList.stream().filter(x -> x.getScoreCard().contains(scoreCardRecord.getScoreCard())).collect(Collectors.toList());
            return collect;
        }
        return scoreCardRecordList;
    }

    @Override
    @Transactional
    public List<ScoreCardRecordSnapshot> newStandardList(ScoreCardRecordVO scoreCardRecordVO, LoginUser loginUser) {

        //根据传入的标准产品id,同业务场景id,同策略场景id查询出标准评分卡列表
        LambdaQueryWrapper<ScoreCardRecordSnapshot> scoreCardRecordLambdaQueryWrapper = new LambdaQueryWrapper<>();
        scoreCardRecordLambdaQueryWrapper.eq(ScoreCardRecordSnapshot::getDeptFlag, RuleConstants.RULE_STANDARD)
                .eq(ScoreCardRecordSnapshot::getProjectCode, scoreCardRecordVO.getProjectCode())
                .eq(ScoreCardRecordSnapshot::getBusinessCode, scoreCardRecordVO.getBusinessCode())
                .eq(ScoreCardRecordSnapshot::getDataState,0)
                .orderByDesc(ScoreCardRecordSnapshot::getCreateTime);
        List<ScoreCardRecordSnapshot> scoreCardRecordList = scoreCardRecordSnapshotMapper.selectList(scoreCardRecordLambdaQueryWrapper);

        //查询当前用户的复用关联表数据
        LambdaQueryWrapper<ScoreCardReuse> scoreCardReuseWrapper = Wrappers.lambdaQuery();
        scoreCardReuseWrapper.eq(ScoreCardReuse::getDeptId,loginUser.getSysUser().getDeptId().intValue())
                .eq(ScoreCardReuse::getBuildProjectCode,scoreCardRecordVO.getBuildProjectCode())
                .eq(ScoreCardReuse::getBuildBusinessCode,scoreCardRecordVO.getBuildBusinessCode())
                .eq(ScoreCardReuse::getBuildRuleCode,RuleConstants.SCORE_MODEL)
                .eq(ScoreCardReuse::getDataStatus,0);
        List<ScoreCardReuse> scoreCardReuseList = scoreCardReuseMapper.selectList(scoreCardReuseWrapper);

        //筛选出此用户下关联的标准评分卡id
        List<Integer> standardCardId = scoreCardReuseList.stream().map(ScoreCardReuse::getParentCardId).collect(Collectors.toList());

        //过滤掉此用户已引用的标准评分卡
        scoreCardRecordList = scoreCardRecordList.stream().filter(x -> !standardCardId.contains(x.getId())).collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(scoreCardRecordVO.getScoreCardStandardDataList())){
            scoreCardRecordList.addAll(scoreCardRecordVO.getScoreCardStandardDataList());
        }
        if (CollectionUtils.isNotEmpty(scoreCardRecordVO.getScoreCardStandardDataDelList())){
            scoreCardRecordList.removeAll(scoreCardRecordVO.getScoreCardStandardDataDelList());
        }
        //搜索
        if (StringUtils.isNotEmpty(scoreCardRecordVO.getScoreCard())){
            List<ScoreCardRecordSnapshot> collect = scoreCardRecordList.stream().filter(x -> x.getScoreCard().contains(scoreCardRecordVO.getScoreCard())).collect(Collectors.toList());
            //过滤出开启的
            collect = collect.stream().filter(x -> x.getButtonState() == 1).collect(Collectors.toList());
            return collect;
        }
        //过滤出开启的
        scoreCardRecordList = scoreCardRecordList.stream().filter(x -> x.getButtonState() == 1).collect(Collectors.toList());
        return scoreCardRecordList;
    }

    @Override
    public List<ScoreCardRecordSnapshot> rulePoolList(ScoreCardRecordVO scoreCardRecordVO) {
        LambdaQueryWrapper<ScoreCardRecordSnapshot> scoreCardRecordLambdaQueryWrapper = new LambdaQueryWrapper<>();
        scoreCardRecordLambdaQueryWrapper.eq(ScoreCardRecordSnapshot::getDeptFlag, RuleConstants.RULE_STANDARD)
                .eq(ScoreCardRecordSnapshot::getProjectCode, scoreCardRecordVO.getProjectCode())
                .eq(ScoreCardRecordSnapshot::getBusinessCode, scoreCardRecordVO.getBusinessCode())
                .eq(ScoreCardRecordSnapshot::getDataState,0)
                .orderByDesc(ScoreCardRecordSnapshot::getCreateTime);
        List<ScoreCardRecordSnapshot> scoreCardRecordList = scoreCardRecordSnapshotMapper.selectList(scoreCardRecordLambdaQueryWrapper);

        return scoreCardRecordList;
    }

    @Resource
    private QuotaCardRecordMapper quotaCardRecordMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult delete(Integer id, LoginUser loginUser) {
        //删除额度卡
        RateCardRecord rateCardRecord = rateCardRecordMapper.selectOne(new LambdaQueryWrapper<RateCardRecord>().eq(RateCardRecord::getScoreCardId, id));
        if (rateCardRecord != null) {
            quotaCardRecordMapper.delete(new LambdaQueryWrapper<QuotaCardRecord>().eq(QuotaCardRecord::getRateCardId, rateCardRecord.getId()));
        }
        //删除评级卡
        rateCardRecordMapper.delete(new LambdaQueryWrapper<RateCardRecord>().eq(RateCardRecord::getScoreCardId, id));
        //逻辑删除 评分卡
        scoreCardRecordMapper.deleteById(id);
        //删除版本归类表数据
        LambdaQueryWrapper<ModelVersionClassification> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(ModelVersionClassification::getModelId, id)
                .eq(ModelVersionClassification::getRuleCode, "1");
        modelVersionClassificationMapper.delete(wrapper);
        return AjaxResult.success();
    }

    @Override
    public AjaxResult get(Integer id, LoginUser loginUser) {
        return AjaxResult.success(scoreCardRecordMapper.selectOne(new LambdaQueryWrapper<ScoreCardRecord>().eq(ScoreCardRecord::getId, id)));
    }

    @Override
    public AjaxResult updateStatus(ScoreCardRecord scoreCardRecord, LoginUser loginUser) {
        if (scoreCardRecord.getId() == null) {
            return AjaxResult.error("主键id不可为空");
        }
        //先查询是否为标准评分卡
        LambdaQueryWrapper<ScoreCardRecord> wrapperRecord = Wrappers.lambdaQuery();
        wrapperRecord.eq(ScoreCardRecord::getId,scoreCardRecord.getId())
                .eq(ScoreCardRecord::getDeptId,loginUser.getSysUser().getDeptId().intValue());
       if (scoreCardRecordMapper.selectOne(wrapperRecord) != null){
           ScoreCardRecord scoreCardRecord1 = new ScoreCardRecord();
           scoreCardRecord1.setButtonState(scoreCardRecord.getButtonState());
           scoreCardRecordMapper.update(scoreCardRecord1, new LambdaQueryWrapper<ScoreCardRecord>().eq(ScoreCardRecord::getId, scoreCardRecord.getId()));
       }else {
           //普通用户下对标准评分卡的开启和停用
           ScoreCardReuse scoreCardReuse = new ScoreCardReuse();
           scoreCardReuse.setButtonState(scoreCardRecord.getButtonState());
           scoreCardReuseMapper.update(scoreCardReuse, new LambdaQueryWrapper<ScoreCardReuse>().eq(ScoreCardReuse::getParentCardId, scoreCardRecord.getId())
           .eq(ScoreCardReuse::getDeptId,loginUser.getSysUser().getDeptId().intValue())
           .eq(ScoreCardReuse::getBuildProjectCode,scoreCardRecord.getProjectCode())
           .eq(ScoreCardReuse::getBuildBusinessCode,scoreCardRecord.getBusinessCode())
           .eq(ScoreCardReuse::getBuildRuleCode,scoreCardRecord.getRuleCode())
           .eq(ScoreCardReuse::getVersionControl,scoreCardRecord.getVersionControl())
           .eq(ScoreCardReuse::getDataStatus,0));
       }

        return AjaxResult.success();
    }

    @Override
    public AjaxResult checkName(ScoreCardRecord scoreCardRecord, LoginUser loginUser) {
        if (scoreCardRecord.getBusinessCode() == null || scoreCardRecord.getScoreCard() == null
                || scoreCardRecord.getRuleCode() == null || scoreCardRecord.getProjectCode() == null) {
            return AjaxResult.error("参数缺失");
        }
        //同部门同业务场景同名验证
        LambdaQueryWrapper<ScoreCardRecord> eq = new LambdaQueryWrapper<ScoreCardRecord>()
                .eq(ScoreCardRecord::getDeptId, loginUser.getSysUser().getDeptId().intValue())
                .eq(ScoreCardRecord::getBusinessCode, scoreCardRecord.getBusinessCode())
                .eq(ScoreCardRecord::getScoreCard, scoreCardRecord.getScoreCard())
                .eq(ScoreCardRecord::getRuleCode, scoreCardRecord.getRuleCode())
                .eq(ScoreCardRecord::getProjectCode, scoreCardRecord.getProjectCode());
        if (scoreCardRecord.getId() != null) {
            eq.ne(ScoreCardRecord::getId, scoreCardRecord.getId());
        } else {
            eq.isNotNull(ScoreCardRecord::getId);
        }
        if (scoreCardRecordMapper.selectList(eq).size() != 0) {
            return AjaxResult.success(true);
        }
        return AjaxResult.success(false);
    }

    @Resource
    private IScoreCardRecordSnapshotService scoreCardRecordSnapshotService;

    @Autowired
    private ProcessNodeMapper processNodeMapper;

    @Resource
    private IScorePrimaryIndexSnapshotService scorePrimaryIndexSnapshotService;

    @Resource
    private IScoreIndexRuleSnapshotService scoreIndexRuleSnapshotService;

    @Resource
    private IScoreCardReuseService scoreCardReuseService;

    @Resource
    private IScoreCardReuseSnapshotService scoreCardReuseSnapshotService;

    @Autowired
    private ProcessPolicyMapper processPolicyMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult release(ScoreCardRecord scoreCardRecord, LoginUser loginUser) {
        if (scoreCardRecord.getProjectCode() == null || scoreCardRecord.getBusinessCode() == null || scoreCardRecord.getRuleCode() == null) {
            return AjaxResult.error("参数缺失");
        }
        Integer deptId = loginUser.getSysUser().getDeptId().intValue();
        LambdaQueryWrapper<ScoreCardRecord> eq = new LambdaQueryWrapper<ScoreCardRecord>()
                .eq(ScoreCardRecord::getDeptId, deptId)
                .eq(ScoreCardRecord::getProjectCode, scoreCardRecord.getProjectCode())
                .eq(ScoreCardRecord::getBusinessCode, scoreCardRecord.getBusinessCode())
                .eq(ScoreCardRecord::getRuleCode, scoreCardRecord.getRuleCode());
        LambdaQueryWrapper<ScoreCardRecordSnapshot> eqS = new LambdaQueryWrapper<ScoreCardRecordSnapshot>()
                .eq(ScoreCardRecordSnapshot::getDeptId, deptId)
                .eq(ScoreCardRecordSnapshot::getProjectCode, scoreCardRecord.getProjectCode())
                .eq(ScoreCardRecordSnapshot::getBusinessCode, scoreCardRecord.getBusinessCode())
                .eq(ScoreCardRecordSnapshot::getRuleCode, scoreCardRecord.getRuleCode());
        scoreCardRecordSnapshotService.remove(eqS);
        List<ScoreCardRecord> scoreCardRecordList = scoreCardRecordMapper.selectList(eq);
        List<ScoreCardRecordSnapshot> scoreCardRecordSnapshot = new ArrayList<>();
        scoreCardRecordSnapshot = JSON.parseArray(JSON.toJSONString(scoreCardRecordList), ScoreCardRecordSnapshot.class);
        if (scoreCardRecordList != null) {
            scoreCardRecordSnapshotService.saveBatch(scoreCardRecordSnapshot);
        }

        LambdaQueryWrapper<ScorePrimaryIndex> eq1 = new LambdaQueryWrapper<ScorePrimaryIndex>()
                .eq(ScorePrimaryIndex::getDeptId, deptId)
                .eq(ScorePrimaryIndex::getProjectCode, scoreCardRecord.getProjectCode())
                .eq(ScorePrimaryIndex::getBusinessCode, scoreCardRecord.getBusinessCode())
                .eq(ScorePrimaryIndex::getRuleCode, scoreCardRecord.getRuleCode());
        LambdaQueryWrapper<ScorePrimaryIndexSnapshot> eqS1 = new LambdaQueryWrapper<ScorePrimaryIndexSnapshot>()
                .eq(ScorePrimaryIndexSnapshot::getDeptId, deptId)
                .eq(ScorePrimaryIndexSnapshot::getProjectCode, scoreCardRecord.getProjectCode())
                .eq(ScorePrimaryIndexSnapshot::getBusinessCode, scoreCardRecord.getBusinessCode())
                .eq(ScorePrimaryIndexSnapshot::getRuleCode, scoreCardRecord.getRuleCode());
        scorePrimaryIndexSnapshotService.remove(eqS1);
        List<ScorePrimaryIndex> scorePrimaryIndices = scorePrimaryIndexMapper.selectList(eq1);
        List<ScorePrimaryIndexSnapshot> scorePrimaryIndexSnapshots = new ArrayList<>();
        scorePrimaryIndexSnapshots = JSON.parseArray(JSON.toJSONString(scorePrimaryIndices), ScorePrimaryIndexSnapshot.class);
        if (scorePrimaryIndices != null) {
            scorePrimaryIndexSnapshotService.saveBatch(scorePrimaryIndexSnapshots);
        }

        LambdaQueryWrapper<ScoreIndexRule> eq2 = new LambdaQueryWrapper<ScoreIndexRule>()
                .eq(ScoreIndexRule::getDeptId, deptId)
                .eq(ScoreIndexRule::getProjectCode, scoreCardRecord.getProjectCode())
                .eq(ScoreIndexRule::getBusinessCode, scoreCardRecord.getBusinessCode())
                .eq(ScoreIndexRule::getRuleCode, scoreCardRecord.getRuleCode());
        LambdaQueryWrapper<ScoreIndexRuleSnapshot> eqS2 = new LambdaQueryWrapper<ScoreIndexRuleSnapshot>()
                .eq(ScoreIndexRuleSnapshot::getDeptId, deptId)
                .eq(ScoreIndexRuleSnapshot::getProjectCode, scoreCardRecord.getProjectCode())
                .eq(ScoreIndexRuleSnapshot::getBusinessCode, scoreCardRecord.getBusinessCode())
                .eq(ScoreIndexRuleSnapshot::getRuleCode, scoreCardRecord.getRuleCode());
        scoreIndexRuleSnapshotService.remove(eqS2);
        List<ScoreIndexRule> scoreIndexRules = scoreIndexRuleMapper.selectList(eq2);
        List<ScoreIndexRuleSnapshot> scoreIndexRuleSnapshots = new ArrayList<>();
        scoreIndexRuleSnapshots = JSON.parseArray(JSON.toJSONString(scoreIndexRules), ScoreIndexRuleSnapshot.class);
        if (scorePrimaryIndices != null) {
            scoreIndexRuleSnapshotService.saveBatch(scoreIndexRuleSnapshots);
        }

        //关联表数据
        LambdaQueryWrapper<ScoreCardReuse> eq3 = new LambdaQueryWrapper<ScoreCardReuse>()
                .eq(ScoreCardReuse::getDeptId, deptId)
                .eq(ScoreCardReuse::getBuildProjectCode, scoreCardRecord.getProjectCode())
                .eq(ScoreCardReuse::getBuildBusinessCode, scoreCardRecord.getBusinessCode())
                .eq(ScoreCardReuse::getBuildRuleCode, scoreCardRecord.getRuleCode());
        LambdaQueryWrapper<ScoreCardReuseSnapshot> eqS3 = new LambdaQueryWrapper<ScoreCardReuseSnapshot>()
                .eq(ScoreCardReuseSnapshot::getDeptId, deptId)
                .eq(ScoreCardReuseSnapshot::getBuildProjectCode, scoreCardRecord.getProjectCode())
                .eq(ScoreCardReuseSnapshot::getBuildBusinessCode, scoreCardRecord.getBusinessCode())
                .eq(ScoreCardReuseSnapshot::getBuildRuleCode, scoreCardRecord.getRuleCode());
        scoreCardReuseSnapshotService.remove(eqS3);
        List<ScoreCardReuse> scoreCardReuses = scoreCardReuseMapper.selectList(eq3);
        List<ScoreCardReuseSnapshot> scoreCardReuseSnapshots = new ArrayList<>();
        scoreCardReuseSnapshots = JSON.parseArray(JSON.toJSONString(scoreCardReuses), ScoreCardReuseSnapshot.class);
        if (scoreCardReuses != null) {
            scoreCardReuseSnapshotService.saveBatch(scoreCardReuseSnapshots);
        }
        return AjaxResult.success();
    }

    /**
     * 检查评分卡是否被流程策略引用
     * @param scoreCardId 评分卡ID
     * @return 是否被引用
     */
    @Override
    public boolean checkIfScoreCardReferencedByProcessPolicy(Integer scoreCardId) {
        if (scoreCardId == null) {
            return false;
        }

        try {
            // 获取评分卡信息
            ScoreCardRecord scoreCardRecord = scoreCardRecordMapper.selectOne(
                    new LambdaQueryWrapper<ScoreCardRecord>()
                            .select(ScoreCardRecord::getButtonState)
                            .eq(ScoreCardRecord::getId, scoreCardId)
            );

            if (scoreCardRecord == null) {
                return false;
            }

            // 1. 首先检查流程节点表是否引用了该评分卡
            if (processNodeMapper.isScoreCardReferencedByActivePolicy(scoreCardId)) {
                log.info("评分卡(ID={})被有效的流程策略引用", scoreCardId);
                return true;
            }

            // 2. 只有当评分卡的buttonState为1（启用状态）时，才检查评级卡引用
            if (scoreCardRecord.getButtonState() != null && scoreCardRecord.getButtonState() == 1) {
                // 检查是否有评级卡引用了该评分卡 - 使用自定义方法
                if (rateCardRecordMapper.isScoreCardReferencedByActiveRateCard(scoreCardId)) {
                    log.info("评分卡(ID={})被有效的评级卡引用", scoreCardId);
                    return true;
                }
            } else {
                log.info("评分卡(ID={})的buttonState={}，跳过评级卡引用检查",
                        scoreCardId, scoreCardRecord.getButtonState());
            }

            return false;
        } catch (Exception e) {
            log.error("检查评分卡是否被流程策略引用时发生异常", e);
            return false;
        }
    }
}
