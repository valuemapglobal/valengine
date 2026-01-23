package com.value.decision.model.decisionmanage.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.value.decision.model.decisionmanage.mapper.ScoreCardReuseMapper;
import com.value.decision.model.decisionmanage.mapper.ScoreIndexRuleMapper;
import com.value.decision.model.decisionmanage.mapper.ScorePrimaryIndexMapper;
import com.value.decision.model.decisionmanage.mapper.ScorePrimaryIndexSnapshotMapper;
import com.value.decision.model.decisionmanage.model.ScoreCardReuse;
import com.value.decision.model.decisionmanage.model.ScoreIndexRule;
import com.value.decision.model.decisionmanage.model.ScorePrimaryIndex;
import com.value.decision.model.decisionmanage.model.ScorePrimaryIndexSnapshot;
import com.value.decision.model.decisionmanage.service.IScorePrimaryIndexService;
import com.value.decision.model.decisionmanage.service.RuoYiService;
import com.value.decision.version.domain.ScorePrimaryIndexVersion;
import com.value.decision.version.mapper.ScorePrimaryIndexVersionMapper;
import com.risksmart.common.core.web.AjaxResult;
import com.value.decision.common.security.LoginUser;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 一级指标存储表 服务实现类
 * </p>
 *
 * @author hc
 * @since 2023-08-08
 */
@Service
public class ScorePrimaryIndexServiceImpl extends ServiceImpl<ScorePrimaryIndexMapper, ScorePrimaryIndex> implements IScorePrimaryIndexService {

    @Resource
    private ScorePrimaryIndexMapper scorePrimaryIndexMapper;
    @Resource
    private ScoreIndexRuleMapper scoreIndexRuleMapper;
    @Resource
    private RuoYiService ruoYiService;
    @Resource
    private ScorePrimaryIndexSnapshotMapper scorePrimaryIndexSnapshotMapper;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult submit(ScorePrimaryIndex scorePrimaryIndex, LoginUser loginUser) {
        //同部门同业务场景同名验证
        LambdaQueryWrapper<ScorePrimaryIndex> eq = new LambdaQueryWrapper<ScorePrimaryIndex>()
                .eq(ScorePrimaryIndex::getDeptId, loginUser.getSysUser().getDeptId().intValue())
                .eq(ScorePrimaryIndex::getBusinessCode, scorePrimaryIndex.getBusinessCode())
                .eq(ScorePrimaryIndex::getPrimaryIndex, scorePrimaryIndex.getPrimaryIndex())
                .eq(ScorePrimaryIndex::getProjectCode, scorePrimaryIndex.getProjectCode())
                .eq(ScorePrimaryIndex::getScoreCardId, scorePrimaryIndex.getScoreCardId());
        if (scorePrimaryIndex.getParentCardId() == null) {
            eq.isNull(ScorePrimaryIndex::getParentCardId);
        } else {
            eq.eq(ScorePrimaryIndex::getParentCardId, scorePrimaryIndex.getParentCardId());
        }
        if (scorePrimaryIndex.getId() != null) {
            eq.ne(ScorePrimaryIndex::getId, scorePrimaryIndex.getId());
        } else {
            eq.isNotNull(ScorePrimaryIndex::getId);
        }
        if (scorePrimaryIndexMapper.selectList(eq).size() != 0) {
            return AjaxResult.error("指标名称不可重复");
        }
        if (scorePrimaryIndex.getId() == null) {
//            scorePrimaryIndex.setDeptFlag(adminDeptId.equals(loginUser.getSysUser().getDeptId().toString()) ? 1 : 0);
            scorePrimaryIndex.setDeptFlag(ruoYiService.getStandardDepts().contains(loginUser.getSysUser().getDeptId()) ? 1 : 2);
            scorePrimaryIndex.setDeptId(loginUser.getSysUser().getDeptId().intValue());
            scorePrimaryIndex.setButtonState(1);
            scorePrimaryIndex.setCreateTime(LocalDateTime.now());
            scorePrimaryIndex.setCreateUserId(loginUser.getUserid().intValue());
            scorePrimaryIndex.setDataState(0);
            //设置默认叶子节点 当父指标id不为空时 更新父指标节点为非叶子节点
            if (scorePrimaryIndex.getParentCardId() != null && StrUtil.isNotBlank(scorePrimaryIndex.getParentCardId().toString())) {
                ScorePrimaryIndex scorePrimaryIndexFather = new ScorePrimaryIndex();
                scorePrimaryIndexFather.setLeafNode(1);
                scorePrimaryIndexMapper.update(scorePrimaryIndexFather, new LambdaQueryWrapper<ScorePrimaryIndex>()
                        .eq(ScorePrimaryIndex::getId, scorePrimaryIndex.getParentCardId()));
            }
            scorePrimaryIndex.setLeafNode(0);
            scorePrimaryIndexMapper.insert(scorePrimaryIndex);
        } else {
            scorePrimaryIndex.setUpdateTime(LocalDateTime.now());
            scorePrimaryIndexMapper.updateById(scorePrimaryIndex);
        }
        //规则迁移
        if (scorePrimaryIndex.getParentCardId() != null) {
            //当父指标卡id不为空时 查询父指标卡下是否有规则 存在规则进行迁移
            List<ScoreIndexRule> scoreIndexRules = scoreIndexRuleMapper.selectList(new LambdaQueryWrapper<ScoreIndexRule>().eq(ScoreIndexRule::getScorePrimaryId, scorePrimaryIndex.getParentCardId()));
            if (CollUtil.isNotEmpty(scoreIndexRules)) {
                String scord_primary_ids = scorePrimaryIndex.getParentCardIds() + ";" + scorePrimaryIndex.getId();
                scoreIndexRules.forEach(x -> {
                    x.setScordPrimaryIds(scord_primary_ids);
                    x.setScorePrimaryId(scorePrimaryIndex.getId());
                    x.setUpdateTime(LocalDateTime.now());
                    scoreIndexRuleMapper.update(x,new LambdaQueryWrapper<ScoreIndexRule>().eq(ScoreIndexRule::getId,x.getId()));
                });
            }
        }
        return AjaxResult.success();
    }

    @Override
    public AjaxResult newList(ScorePrimaryIndex scorePrimaryIndex, LoginUser loginUser) {
        if (scorePrimaryIndex.getScoreCardId() == null) {
            return AjaxResult.error("评分卡Id不可为空");
        }
        LambdaQueryWrapper<ScorePrimaryIndex> scorePrimaryIndexLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (scorePrimaryIndex.getParentCardId() == null) {
            scorePrimaryIndexLambdaQueryWrapper.isNull(ScorePrimaryIndex::getParentCardId)
                    .eq(ScorePrimaryIndex::getScoreCardId, scorePrimaryIndex.getScoreCardId())
                    .eq(ScorePrimaryIndex::getDataState,0)
                    .orderByDesc(ScorePrimaryIndex::getCreateTime);
        } else {
            scorePrimaryIndexLambdaQueryWrapper.eq(ScorePrimaryIndex::getParentCardId, scorePrimaryIndex.getParentCardId())
                    .eq(ScorePrimaryIndex::getScoreCardId, scorePrimaryIndex.getScoreCardId())
                    .eq(ScorePrimaryIndex::getDataState,0)
                    .orderByDesc(ScorePrimaryIndex::getCreateTime);
        }
        List<ScorePrimaryIndex> scorePrimaryIndices = scorePrimaryIndexMapper.selectList(scorePrimaryIndexLambdaQueryWrapper);
        scorePrimaryIndices.forEach(x->{
            x.setPriFlag(scorePrimaryIndexMapper.selectCount(new LambdaQueryWrapper<ScorePrimaryIndex>().eq(ScorePrimaryIndex::getParentCardId,x.getId())) > 0 ?1:0);
        });

        return AjaxResult.success(scorePrimaryIndices);
    }

    @Override
    public List<ScorePrimaryIndexSnapshot> rulePoolList(ScorePrimaryIndex scorePrimaryIndex) {
        LambdaQueryWrapper<ScorePrimaryIndexSnapshot> scorePrimaryIndexLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (scorePrimaryIndex.getParentCardId() == null) {
            scorePrimaryIndexLambdaQueryWrapper.isNull(ScorePrimaryIndexSnapshot::getParentCardId)
                    .eq(ScorePrimaryIndexSnapshot::getScoreCardId, scorePrimaryIndex.getScoreCardId())
                    .eq(ScorePrimaryIndexSnapshot::getDataState,0)
                    .orderByDesc(ScorePrimaryIndexSnapshot::getCreateTime);
        } else {
            scorePrimaryIndexLambdaQueryWrapper.eq(ScorePrimaryIndexSnapshot::getParentCardId, scorePrimaryIndex.getParentCardId())
                    .eq(ScorePrimaryIndexSnapshot::getScoreCardId, scorePrimaryIndex.getScoreCardId())
                    .eq(ScorePrimaryIndexSnapshot::getDataState,0)
                    .orderByDesc(ScorePrimaryIndexSnapshot::getCreateTime);
        }
        List<ScorePrimaryIndexSnapshot> scorePrimaryIndices = scorePrimaryIndexSnapshotMapper.selectList(scorePrimaryIndexLambdaQueryWrapper);
        scorePrimaryIndices.forEach(x->{
            x.setPriFlag(scorePrimaryIndexSnapshotMapper.selectCount(new LambdaQueryWrapper<ScorePrimaryIndexSnapshot>().eq(ScorePrimaryIndexSnapshot::getParentCardId,x.getId())) > 0 ?1:0);
        });

        return scorePrimaryIndices;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult delete(Integer id, Object parentId, LoginUser loginUser) {
        //根据父id查询该指标卡下是否只存在一条记录  一条 删除后更新父指标卡为叶子节点
        if (scorePrimaryIndexMapper.selectList(new LambdaUpdateWrapper<ScorePrimaryIndex>().eq(ScorePrimaryIndex::getParentCardId, parentId)).size() == 1) {
            ScorePrimaryIndex scorePrimaryIndex = new ScorePrimaryIndex();
            scorePrimaryIndex.setLeafNode(0);
            scorePrimaryIndexMapper.update(scorePrimaryIndex, new LambdaQueryWrapper<ScorePrimaryIndex>().eq(ScorePrimaryIndex::getId, parentId));
        }
        //删除指标卡
        scorePrimaryIndexMapper.delete(new LambdaQueryWrapper<ScorePrimaryIndex>().eq(ScorePrimaryIndex::getId, id));
        //同时删除评分规则卡
        scoreIndexRuleMapper.delete(new LambdaQueryWrapper<ScoreIndexRule>().like(ScoreIndexRule::getScordPrimaryIds,id));
        return AjaxResult.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult deleteCard(Integer id, LoginUser loginUser) {
        ScorePrimaryIndex scorePrimaryIndex = new ScorePrimaryIndex();
        scorePrimaryIndex.setLeafNode(0);
        scorePrimaryIndexMapper.update(scorePrimaryIndex, new LambdaQueryWrapper<ScorePrimaryIndex>().eq(ScorePrimaryIndex::getId, id));
        scorePrimaryIndexMapper.delete(new LambdaQueryWrapper<ScorePrimaryIndex>().eq(ScorePrimaryIndex::getParentCardId, id));
        return AjaxResult.success();
    }

    @Override
    public AjaxResult get(Integer id, LoginUser loginUser) {
        return AjaxResult.success(scorePrimaryIndexMapper.selectOne(new LambdaQueryWrapper<ScorePrimaryIndex>().eq(ScorePrimaryIndex::getId, id)));
    }

    @Override
    public AjaxResult updateStatus(ScorePrimaryIndex scorePrimaryIndex, LoginUser loginUser) {
        if (scorePrimaryIndex.getId() == null || scorePrimaryIndex.getButtonState() == null) {
            return AjaxResult.error("参数不为空");
        }
        ScorePrimaryIndex scorePrimaryIndex1 = new ScorePrimaryIndex();
        scorePrimaryIndex1.setButtonState(scorePrimaryIndex.getButtonState());
        scorePrimaryIndexMapper.update(scorePrimaryIndex1, new LambdaQueryWrapper<ScorePrimaryIndex>().eq(ScorePrimaryIndex::getId, scorePrimaryIndex.getId()));
        //同时更新规则表
        ScoreIndexRule scoreIndexRule = new ScoreIndexRule();
        scoreIndexRule.setButtonState(scorePrimaryIndex.getButtonState());
        scoreIndexRuleMapper.update(scoreIndexRule,new LambdaQueryWrapper<ScoreIndexRule>().like(ScoreIndexRule::getScordPrimaryIds,scorePrimaryIndex.getId()));
        return AjaxResult.success();
    }

    @Override
    public AjaxResult checkName(ScorePrimaryIndex scorePrimaryIndex, LoginUser loginUser) {
        if(scorePrimaryIndex.getBusinessCode()==null||scorePrimaryIndex.getPrimaryIndex()==null
                ||scorePrimaryIndex.getRuleCode()==null|| scorePrimaryIndex.getProjectCode()==null){
            return AjaxResult.error("参数缺失");
        }
        //同部门同业务场景同名验证
        LambdaQueryWrapper<ScorePrimaryIndex> eq = new LambdaQueryWrapper<ScorePrimaryIndex>()
                .eq(ScorePrimaryIndex::getDeptId, loginUser.getSysUser().getDeptId().intValue())
                .eq(ScorePrimaryIndex::getBusinessCode, scorePrimaryIndex.getBusinessCode())
                .eq(ScorePrimaryIndex::getPrimaryIndex, scorePrimaryIndex.getPrimaryIndex())
                .eq(ScorePrimaryIndex::getProjectCode, scorePrimaryIndex.getProjectCode())
                .eq(ScorePrimaryIndex::getRuleCode,scorePrimaryIndex.getRuleCode())
                .eq(ScorePrimaryIndex::getScoreCardId, scorePrimaryIndex.getScoreCardId());
        if (scorePrimaryIndex.getParentCardId() == null) {
            eq.isNull(ScorePrimaryIndex::getParentCardId);
        } else {
            eq.eq(ScorePrimaryIndex::getParentCardId, scorePrimaryIndex.getParentCardId());
        }
        if (scorePrimaryIndex.getId() != null) {
            eq.ne(ScorePrimaryIndex::getId, scorePrimaryIndex.getId());
        } else {
            eq.isNotNull(ScorePrimaryIndex::getId);
        }
        if (scorePrimaryIndexMapper.selectList(eq).size() != 0) {
            return AjaxResult.success(true);
        }
        return AjaxResult.success(false);
    }

}
