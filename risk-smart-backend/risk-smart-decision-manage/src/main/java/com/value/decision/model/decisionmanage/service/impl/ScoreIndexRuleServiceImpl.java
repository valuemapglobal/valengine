package com.value.decision.model.decisionmanage.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.value.decision.model.decisionmanage.common.GenerateRuleUtil;
import com.value.decision.model.decisionmanage.mapper.ScoreIndexRuleMapper;
import com.value.decision.model.decisionmanage.mapper.ScoreIndexRuleSnapshotMapper;
import com.value.decision.model.decisionmanage.model.ScoreIndexRule;
import com.value.decision.model.decisionmanage.model.ScoreIndexRuleSnapshot;
import com.value.decision.model.decisionmanage.service.IScoreIndexRuleService;
import com.value.decision.model.decisionmanage.service.RuoYiService;
import com.risksmart.common.core.web.AjaxResult;
import com.value.decision.common.security.LoginUser;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.value.decision.model.rdenew.mapper.RdeModelRuleRecordMapper;
import com.value.decision.model.rdenew.service.RdeModelDecisionCodeLevelService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;

/**
 * <p>
 * 指标规则表 服务实现类
 * </p>
 *
 * @author hc
 * @since 2023-08-08
 */
@Service
public class ScoreIndexRuleServiceImpl extends ServiceImpl<ScoreIndexRuleMapper, ScoreIndexRule> implements IScoreIndexRuleService {

    @Resource
    private ScoreIndexRuleMapper scoreIndexRuleMapper;
    @Resource
    private ScoreIndexRuleSnapshotMapper scoreIndexRuleSnapshotMapper;

    @Resource
    private GenerateRuleUtil parsingRules;

    @Resource
    private RuoYiService ruoYiService;

    @Override
    public AjaxResult newList(ScoreIndexRule scoreIndexRule, LoginUser loginUser) {
        if (scoreIndexRule.getScorePrimaryId() == null) {
            return AjaxResult.error("指标卡id不可为空");
        }
        //根据指标卡id查询指标规则 没有返还默认指标规则
        LambdaQueryWrapper<ScoreIndexRule> eq = new LambdaQueryWrapper<ScoreIndexRule>()
                .eq(ScoreIndexRule::getScorePrimaryId, scoreIndexRule.getScorePrimaryId());
        List<ScoreIndexRule> scoreIndexRules = scoreIndexRuleMapper.selectList(eq);
        LambdaQueryWrapper<ScoreIndexRule> eq2 = new LambdaQueryWrapper<ScoreIndexRule>()
                .eq(ScoreIndexRule::getScorePrimaryId, scoreIndexRule.getScorePrimaryId())
                .eq(ScoreIndexRule::getDefaultRule, 1);
        if (scoreIndexRuleMapper.selectCount(eq2) == 0) {
            ScoreIndexRule scoreIndexRule1 = new ScoreIndexRule();
            scoreIndexRule1.setIndexRule("默认指标规则");
            scoreIndexRule1.setScore(null);
            scoreIndexRule1.setDescription("未匹配到其他指标规则时，取默认评分");
            scoreIndexRule1.setButtonState(0);
            scoreIndexRule1.setLevel(0);
            scoreIndexRule1.setScorePrimaryId(scoreIndexRule.getScorePrimaryId());
            scoreIndexRules.add(scoreIndexRule1);
        }
        Collections.sort(scoreIndexRules, Comparator.comparingInt(ScoreIndexRule::getLevel));

        // 为每个规则解析conditionArray并设置到conditionArrayJSON
        for (ScoreIndexRule rule : scoreIndexRules) {
            if (StrUtil.isNotBlank(rule.getConditionArray())) {
                rule.setConditionArrayJSON(JSON.parseArray(rule.getConditionArray()));
            }
        }

        return AjaxResult.success(scoreIndexRules);
    }

    @Override
    public List<ScoreIndexRuleSnapshot> rulePoolList(ScoreIndexRule scoreIndexRule) {
        LambdaQueryWrapper<ScoreIndexRuleSnapshot> eq = new LambdaQueryWrapper<ScoreIndexRuleSnapshot>()
                .eq(ScoreIndexRuleSnapshot::getScorePrimaryId, scoreIndexRule.getScorePrimaryId());
        List<ScoreIndexRuleSnapshot> scoreIndexRules = scoreIndexRuleSnapshotMapper.selectList(eq);
        LambdaQueryWrapper<ScoreIndexRuleSnapshot> eq2 = new LambdaQueryWrapper<ScoreIndexRuleSnapshot>()
                .eq(ScoreIndexRuleSnapshot::getScorePrimaryId, scoreIndexRule.getScorePrimaryId())
                .eq(ScoreIndexRuleSnapshot::getDefaultRule, 1);
        if (scoreIndexRuleSnapshotMapper.selectCount(eq2) == 0) {
            ScoreIndexRuleSnapshot scoreIndexRule1 = new ScoreIndexRuleSnapshot();
            scoreIndexRule1.setIndexRule("默认指标规则");
            scoreIndexRule1.setScore(null);
            scoreIndexRule1.setDescription("未匹配到其他指标规则时，取默认评分");
            scoreIndexRule1.setButtonState(0);
            scoreIndexRule1.setLevel(0);
            scoreIndexRule1.setScorePrimaryId(scoreIndexRule.getScorePrimaryId());
            scoreIndexRules.add(scoreIndexRule1);
        }
        Collections.sort(scoreIndexRules, Comparator.comparingInt(ScoreIndexRuleSnapshot::getLevel));
        return scoreIndexRules;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult submit(ScoreIndexRule scoreIndexRule, LoginUser loginUser) {
        //同部门同业务场景同名验证
        LambdaQueryWrapper<ScoreIndexRule> eq = new LambdaQueryWrapper<ScoreIndexRule>()
                .eq(ScoreIndexRule::getDeptId, loginUser.getSysUser().getDeptId().intValue())
                .eq(ScoreIndexRule::getBusinessCode, scoreIndexRule.getBusinessCode())
                .eq(ScoreIndexRule::getScorePrimaryId, scoreIndexRule.getScorePrimaryId())
                .eq(ScoreIndexRule::getProjectCode, scoreIndexRule.getProjectCode())
                .eq(ScoreIndexRule::getIndexRule, scoreIndexRule.getIndexRule());
        if (scoreIndexRuleMapper.selectList(eq).size() != 0) {
            return AjaxResult.error("不能同部门同业务场景评分指标规则同名");
        }
        if (scoreIndexRule.getTakeEffect() == null || StrUtil.isBlank(scoreIndexRule.getTakeEffect().toString())) {
            return AjaxResult.error("是否生效不能为空");
        }
        //根据指标卡id查询该规则下是否由默认规则 没有设置优先级为1 默认指标规则 0 否 1是
        List<HashMap<String, Object>> hashMap = scoreIndexRuleMapper.selectCoungByDefaultRule(scoreIndexRule.getScorePrimaryId());
        HashMap<String, Object> map = new HashMap<>();
        for (HashMap<String, Object> stringObjectHashMap : hashMap) {
            map.put(stringObjectHashMap.get("value").toString(), stringObjectHashMap.get("count"));
        }
        if (map.size() == 0) {
            scoreIndexRule.setLevel(1);
        } else if (map.get("0") != null) {
            int i = Integer.parseInt(map.get("0").toString());
            scoreIndexRule.setLevel(++i);
        } else if (map.get("0") == null) {
            scoreIndexRule.setLevel(1);
        }

        //新增的规则都是非默认规则
        scoreIndexRule.setDefaultRule(0);


//        scoreIndexRule.setDeptFlag(adminDeptId.equals(loginUser.getSysUser().getDeptId().toString()) ? 1 : 0);
        scoreIndexRule.setDeptFlag(ruoYiService.getStandardDepts().contains(loginUser.getSysUser().getDeptId()) ? 1 : 2);
        scoreIndexRule.setDeptId(loginUser.getSysUser().getDeptId().intValue());
        scoreIndexRule.setButtonState(1);
        scoreIndexRule.setCreateTime(LocalDateTime.now());
        scoreIndexRule.setCreateUserId(loginUser.getUserid().intValue());
        scoreIndexRule.setDataState(0);
        if (scoreIndexRule.getGenerateRuleVO() != null) {
            scoreIndexRule.getGenerateRuleVO().setCode(scoreIndexRule.getIndexRule());
            scoreIndexRule.setConditionArray(JSON.toJSONString(scoreIndexRule.getGenerateRuleVO().getConditionArray()));
            Map<String, Object> stringObjectMap = parsingRules.generateRule(scoreIndexRule.getGenerateRuleVO());
            if (stringObjectMap!=null) {
                scoreIndexRule.setTermRule(stringObjectMap.get("codeDrl").toString());
                scoreIndexRule.setDataModule(stringObjectMap.get("objList").toString());
            }
        }
        scoreIndexRuleMapper.insert(scoreIndexRule);

        return AjaxResult.success(scoreIndexRule.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult newUpdate(ScoreIndexRule scoreIndexRule, LoginUser loginUser) {
        //同部门同业务场景同名验证
        LambdaQueryWrapper<ScoreIndexRule> eq = new LambdaQueryWrapper<ScoreIndexRule>()
                .eq(ScoreIndexRule::getDeptId, loginUser.getSysUser().getDeptId().intValue())
                .eq(ScoreIndexRule::getBusinessCode, scoreIndexRule.getBusinessCode())
                .eq(ScoreIndexRule::getScorePrimaryId, scoreIndexRule.getScorePrimaryId())
                .eq(ScoreIndexRule::getProjectCode, scoreIndexRule.getProjectCode())
                .eq(ScoreIndexRule::getIndexRule, scoreIndexRule.getIndexRule());
        if (scoreIndexRule.getId() != null) {
            eq.ne(ScoreIndexRule::getId, scoreIndexRule.getId());
        } else {
            eq.isNotNull(ScoreIndexRule::getId);
        }
        if (scoreIndexRuleMapper.selectList(eq).size() != 0) {
            return AjaxResult.error("不能同部门同业务场景评分指标规则同名");
        }

        //当规则id为空时 为默认指标的新增操作 其他的为修改操作
        if (scoreIndexRule.getId() == null) {
            //查询该指标卡下是否含有默认指标 存在 不允许 新增默认指标
            if (scoreIndexRuleMapper.selectCount(new LambdaQueryWrapper<ScoreIndexRule>()
                    .eq(ScoreIndexRule::getScorePrimaryId, scoreIndexRule.getScorePrimaryId())
                    .eq(ScoreIndexRule::getDefaultRule, 1)
            ) != 0) {
               return AjaxResult.error("默认指标不能重复！");
            }

            //默认规则  0 否 1是
            scoreIndexRule.setDefaultRule(1);
//            scoreIndexRule.setDeptFlag(adminDeptId.equals(loginUser.getSysUser().getDeptId().toString()) ? 1 : 0);
            scoreIndexRule.setDeptFlag(ruoYiService.getStandardDepts().contains(loginUser.getSysUser().getDeptId()) ? 1 : 2);
            scoreIndexRule.setDeptId(loginUser.getSysUser().getDeptId().intValue());
            scoreIndexRule.setButtonState(1);
            scoreIndexRule.setCreateTime(LocalDateTime.now());
            scoreIndexRule.setCreateUserId(loginUser.getUserid().intValue());
            scoreIndexRule.setDataState(0);
            scoreIndexRule.setLevel(0);
            scoreIndexRule.setDescription("未匹配到其他指标规则时，取默认评分");
            scoreIndexRuleMapper.insert(scoreIndexRule);
            return AjaxResult.success(scoreIndexRule.getId());
        } else {
            if(scoreIndexRule.getTakeEffect() == null){
                return AjaxResult.error("是否生效不可为空");
            }
            //包装规则
            if (scoreIndexRule.getGenerateRuleVO() != null && scoreIndexRule.getGenerateRuleVO().getConditionArray()!=null) {
                scoreIndexRule.getGenerateRuleVO().setCode(scoreIndexRule.getIndexRule());
                scoreIndexRule.setConditionArray(JSON.toJSONString(scoreIndexRule.getGenerateRuleVO().getConditionArray()));
                Map<String, Object> stringObjectMap = parsingRules.generateRule(scoreIndexRule.getGenerateRuleVO());
                if (stringObjectMap!=null) {
                    scoreIndexRule.setTermRule(stringObjectMap.get("codeDrl").toString());
                    scoreIndexRule.setDataModule(stringObjectMap.get("objList").toString());
                }
            }else{
                scoreIndexRule.setTermRule(null);
                scoreIndexRule.setDataModule(null);
                scoreIndexRule.setConditionArray(null);
            }
        }

        scoreIndexRule.setUpdateTime(LocalDateTime.now());
        scoreIndexRuleMapper.updateById(scoreIndexRule);
        return AjaxResult.success(scoreIndexRule.getId());
    }

    @Override
    public AjaxResult checkCode(ScoreIndexRule scoreIndexRule, LoginUser loginUser) {
        if (scoreIndexRule.getScorePrimaryId() == null || scoreIndexRule.getIndexRule() == null) {
            return AjaxResult.error("参数缺失");
        }
        LambdaQueryWrapper<ScoreIndexRule> eq = new LambdaQueryWrapper<>();
        eq.eq(ScoreIndexRule::getScorePrimaryId, scoreIndexRule.getScorePrimaryId())
                .eq(ScoreIndexRule::getIndexRule, scoreIndexRule.getIndexRule());
        if (scoreIndexRule.getId() != null) {
            eq.ne(ScoreIndexRule::getId, scoreIndexRule.getId());
        } else {
            eq.isNotNull(ScoreIndexRule::getId);
        }
        if (scoreIndexRuleMapper.selectList(eq).size() != 0) {
            return AjaxResult.success(true);
        }
        return AjaxResult.success(false);
    }

    @Override
    public AjaxResult delete(Integer id, LoginUser loginUser) {
        //判断当前规则是否默认规则  默认规则不允许删除
        ScoreIndexRule scoreIndexRule = scoreIndexRuleMapper.selectOne(new LambdaQueryWrapper<ScoreIndexRule>().eq(ScoreIndexRule::getId, id));
        if(scoreIndexRule!=null){
            if (scoreIndexRule.getDefaultRule()!=null && scoreIndexRule.getDefaultRule() == 1 ) {
                return AjaxResult.error("默认规则不可删除");
            }
        }
        scoreIndexRuleMapper.deleteById(id);
        return AjaxResult.success();
    }

    @Override
    public AjaxResult setSort(List<Map<String, String>> list, LoginUser loginUser) {
        for (Map<String, String> map : list) {
            scoreIndexRuleMapper.updateSortById(Integer.valueOf(map.get("id")), Integer.valueOf( map.get("sort")));
        }
        return AjaxResult.success();
    }

    @Override
    public AjaxResult updateStatus(ScoreIndexRule scoreIndexRule, LoginUser loginUser) {
        if (scoreIndexRule.getId() == null || StrUtil.isBlank(scoreIndexRule.getId().toString())) {
            return AjaxResult.error("未编辑评分指标，不可使用");
        }
        LambdaQueryWrapper<ScoreIndexRule> scoreIndexRuleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        scoreIndexRuleLambdaQueryWrapper.eq(ScoreIndexRule::getId, scoreIndexRule.getId());
        ScoreIndexRule scoreIndexRule1 = new ScoreIndexRule();
        scoreIndexRule1.setButtonState(scoreIndexRule.getButtonState());
        scoreIndexRuleMapper.update(scoreIndexRule1, scoreIndexRuleLambdaQueryWrapper);
        return AjaxResult.success();
    }

    @Override
    public AjaxResult get(ScoreIndexRule scoreIndexRule, LoginUser loginUser) {
        ScoreIndexRule scoreIndexRule1 = new ScoreIndexRule();
        if (scoreIndexRule.getId() == null) {
            scoreIndexRule1.setIndexRule("默认指标规则");
            scoreIndexRule1.setScoreInit("-");
            scoreIndexRule1.setDescription("未匹配到其他指标规则时，取默认评分");
            scoreIndexRule1.setButtonState(0);
            scoreIndexRule1.setLevel(0);
            return AjaxResult.success(scoreIndexRule1);
        }

        scoreIndexRule1 = scoreIndexRuleMapper.selectOne(new LambdaQueryWrapper<ScoreIndexRule>().eq(ScoreIndexRule::getId, scoreIndexRule.getId()));
        scoreIndexRule1.setConditionArrayJSON(JSON.parseArray(scoreIndexRule1.getConditionArray()));
        return AjaxResult.success(scoreIndexRule1);
    }


}
