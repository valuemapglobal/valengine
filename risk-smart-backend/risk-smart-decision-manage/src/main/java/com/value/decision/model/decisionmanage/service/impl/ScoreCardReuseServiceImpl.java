package com.value.decision.model.decisionmanage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.value.decision.model.decisionmanage.constants.RuleConstants;
import com.value.decision.model.decisionmanage.mapper.ScoreCardRecordMapper;
import com.value.decision.model.decisionmanage.model.ScoreCardRecord;
import com.value.decision.model.decisionmanage.model.ScoreCardReuse;
import com.value.decision.model.decisionmanage.mapper.ScoreCardReuseMapper;
import com.value.decision.model.decisionmanage.model.dto.model.ScoreCardReuseVO;
import com.value.decision.model.decisionmanage.service.IScoreCardReuseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.value.decision.version.domain.ScoreCardRecordVersion;
import com.value.decision.version.mapper.ScoreCardRecordVersionMapper;
import com.value.decision.common.security.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 评分卡复用关联表 服务实现类
 * </p>
 *
 * @author dianne
 * @since 2024-11-28
 */
@Service
public class ScoreCardReuseServiceImpl extends ServiceImpl<ScoreCardReuseMapper, ScoreCardReuse> implements IScoreCardReuseService {

    @Autowired
    private ScoreCardRecordVersionMapper scoreCardRecordVersionMapper;

    @Autowired
    private ScoreCardReuseMapper scoreCardReuseMapper;

    /**
     * 评分卡复用
     * @param scoreCardReuseVO
     */
    @Transactional
    public void addStandardModel(ScoreCardReuseVO scoreCardReuseVO,LoginUser loginUser) {
        //将标准评分卡信息与当前用户信息绑定存入关联表
        if (CollectionUtils.isNotEmpty(scoreCardReuseVO.getParentCardData())){
            //查询出已关联的数据
            LambdaQueryWrapper<ScoreCardReuse> wrapper = Wrappers.lambdaQuery();
            wrapper.eq(ScoreCardReuse::getBuildProjectCode,scoreCardReuseVO.getBuildProjectCode())
                    .eq(ScoreCardReuse::getBuildBusinessCode,scoreCardReuseVO.getBuildBusinessCode())
                    .eq(ScoreCardReuse::getDeptId,loginUser.getSysUser().getDeptId().intValue())
                    .eq(ScoreCardReuse::getDataStatus,0);
            List<ScoreCardReuse> cardReuseList = this.list(wrapper);
            List<Integer> collect3 = cardReuseList.stream().map(ScoreCardReuse::getId).collect(Collectors.toList());
            this.removeByIds(collect3);

            List<ScoreCardReuse> scoreCardReuseList = new ArrayList<>();
            scoreCardReuseVO.getParentCardData().forEach(x ->{
                ScoreCardReuse scoreCardReuse = new ScoreCardReuse();
                scoreCardReuse.setButtonState(1);
                scoreCardReuse.setBuildBusinessCode(scoreCardReuseVO.getBuildBusinessCode());
                scoreCardReuse.setBuildProjectCode(scoreCardReuseVO.getBuildProjectCode());
                scoreCardReuse.setBuildRuleCode(RuleConstants.SCORE_MODEL);
                scoreCardReuse.setDeptId(loginUser.getSysUser().getDeptId().intValue());
                scoreCardReuse.setParentCardId(x.getId());
                scoreCardReuse.setVersionControl(x.getVersionControl());
                scoreCardReuseList.add(scoreCardReuse);
            });
            this.saveBatch(scoreCardReuseList);


//            List<Integer> parentCardIdList = cardReuseList.stream().map(ScoreCardReuse::getParentCardId).collect(Collectors.toList());
//            List<ScoreCardRecord> collect = scoreCardReuseVO.getParentCardData().stream().filter(x -> !parentCardIdList.contains(x.getId())).collect(Collectors.toList());
//
//            List<ScoreCardReuse> scoreCardReuseList = new ArrayList<>();
//            collect.stream().forEach(x ->{
//                ScoreCardReuse scoreCardReuse = new ScoreCardReuse();
//                scoreCardReuse.setButtonState(1);
//                scoreCardReuse.setBuildBusinessCode(scoreCardReuseVO.getBuildBusinessCode());
//                scoreCardReuse.setBuildProjectCode(scoreCardReuseVO.getBuildProjectCode());
//                scoreCardReuse.setBuildRuleCode(RuleConstants.SCORE_MODEL);
//                scoreCardReuse.setDeptId(loginUser.getSysUser().getDeptId().intValue());
//                scoreCardReuse.setParentCardId(x.getId());
//                scoreCardReuse.setVersionControl(x.getVersionControl());
//                scoreCardReuseList.add(scoreCardReuse);
//            });
//            this.saveBatch(scoreCardReuseList);
//
//            //对比数据库已经移除的
//            List<Integer> collect1 = scoreCardReuseVO.getParentCardData().stream().map(ScoreCardRecord::getId).collect(Collectors.toList());
//            List<ScoreCardReuse> collect2 = cardReuseList.stream().filter(x -> !collect1.contains(x.getId())).collect(Collectors.toList());
//            collect2.stream().forEach(x ->{
//                LambdaQueryWrapper<ScoreCardReuse> wrapperDel = Wrappers.lambdaQuery();
//                wrapperDel.eq(ScoreCardReuse::getDeptId,loginUser.getSysUser().getDeptId().intValue())
//                        .eq(ScoreCardReuse::getBuildBusinessCode,x.getBuildBusinessCode())
//                        .eq(ScoreCardReuse::getBuildProjectCode,x.getBuildProjectCode())
//                        .eq(ScoreCardReuse::getParentCardId,x.getParentCardId())
//                        .eq(ScoreCardReuse::getVersionControl,x.getVersionControl());
//                this.remove(wrapperDel);
//            });
        }else {  //传入引用标准的为空时移除所有关联的id
            LambdaQueryWrapper<ScoreCardReuse> wrapperDel = Wrappers.lambdaQuery();
            wrapperDel.eq(ScoreCardReuse::getDeptId,loginUser.getSysUser().getDeptId().intValue())
                    .eq(ScoreCardReuse::getBuildBusinessCode,scoreCardReuseVO.getBuildBusinessCode())
                    .eq(ScoreCardReuse::getBuildProjectCode,scoreCardReuseVO.getBuildProjectCode());
            this.remove(wrapperDel);
        }
    }

    @Override
    public void deleteParentCard(ScoreCardReuseVO scoreCardReuseVO, LoginUser loginUser) {
        LambdaQueryWrapper<ScoreCardReuse> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(ScoreCardReuse::getDeptId,loginUser.getSysUser().getDeptId().intValue())
                .eq(ScoreCardReuse::getBuildBusinessCode,scoreCardReuseVO.getBuildBusinessCode())
                .eq(ScoreCardReuse::getBuildProjectCode,scoreCardReuseVO.getBuildProjectCode())
                .eq(ScoreCardReuse::getParentCardId,scoreCardReuseVO.getParentCardId())
                .eq(ScoreCardReuse::getVersionControl,scoreCardReuseVO.getVersionControl());
        this.remove(wrapper);
    }

    @Override
    public List<ScoreCardRecordVersion> selectStandardModel(ScoreCardReuseVO scoreCardReuseVO, LoginUser loginUser) {
        LambdaQueryWrapper<ScoreCardReuse> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(ScoreCardReuse::getDeptId,loginUser.getSysUser().getDeptId().intValue())
                .eq(ScoreCardReuse::getBuildProjectCode,scoreCardReuseVO.getBuildProjectCode())
                .eq(ScoreCardReuse::getBuildBusinessCode,scoreCardReuseVO.getBuildBusinessCode())
                .eq(ScoreCardReuse::getDataStatus,0);
        List<ScoreCardReuse> cardReuseList = this.list(wrapper);
        List<ScoreCardRecordVersion> scoreCardRecordList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(cardReuseList)){
            //评分模型版本表
            cardReuseList.stream().forEach(x ->{
                LambdaQueryWrapper<ScoreCardRecordVersion> wrapper1 = Wrappers.lambdaQuery();
                wrapper1.eq(ScoreCardRecordVersion::getId,x.getParentCardId())
                        .eq(ScoreCardRecordVersion::getVersionControl,x.getVersionControl())
                        .eq(ScoreCardRecordVersion::getDataState,0);
                ScoreCardRecordVersion scoreCardRecordVersion = scoreCardRecordVersionMapper.selectOne(wrapper1);
                scoreCardRecordList.add(scoreCardRecordVersion);
            });
        }
        return scoreCardRecordList;
    }

}
