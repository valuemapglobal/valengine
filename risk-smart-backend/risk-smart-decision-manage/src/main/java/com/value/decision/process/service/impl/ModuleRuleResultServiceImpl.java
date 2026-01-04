package com.value.decision.process.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.util.StringUtil;
import com.value.decision.process.mapper.ModuleRuleResultMapper;
import com.value.decision.process.mapper.ProcessPolicyTaskMapper;
import com.value.decision.process.model.ModuleRuleResult;
import com.value.decision.process.service.IModuleRuleResultService;
import com.value.decision.process.vo.ProcesPolicyDTO;
import com.value.decision.process.vo.ProcesPolicyVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 模块任务结果表 服务实现类
 * </p>
 *
 * @author hc
 * @since 2023-08-16
 */
@Service
public class ModuleRuleResultServiceImpl extends ServiceImpl<ModuleRuleResultMapper, ModuleRuleResult> implements IModuleRuleResultService {

    @Autowired
    private ModuleRuleResultMapper moduleRuleResultMapper;

    @Autowired
    private ProcessPolicyTaskMapper processPolicyTaskMapper;

    /**
     * 回调任务接口 -- 额度结果
     * 0生成中 1生成完成 2审批结果拒绝
     */
    public ProcesPolicyDTO quotaResult(ProcesPolicyVO procesPolicyVO){

        Map<String,Object> map = new HashMap<>();

        //流程策略结果详情
        procesPolicyVO.setModuleId(3);
        ProcesPolicyDTO procesPolicyDTO1 = processPolicyTaskMapper.limitTask(procesPolicyVO);

        //节点模块结果表数据
        LambdaQueryWrapper<ModuleRuleResult> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(ModuleRuleResult::getModuleId,3)
                .eq(ModuleRuleResult::getTaskNumber,procesPolicyVO.getTaskNumber());
        ModuleRuleResult moduleRuleResult = moduleRuleResultMapper.selectOne(wrapper);

        ProcesPolicyDTO procesPolicyDTO = new ProcesPolicyDTO();
        if (StringUtil.isEmpty(moduleRuleResult.getModuleResult())){

            procesPolicyDTO.setFlag(2); //拒绝
            procesPolicyDTO.setTaskNumber(procesPolicyVO.getTaskNumber());
            procesPolicyDTO.setDetail("0");
            return procesPolicyDTO;
        }else if ("拒绝".equals(procesPolicyDTO1.getResult())){
            procesPolicyDTO.setFlag(2); //拒绝
            procesPolicyDTO.setTaskNumber(procesPolicyVO.getTaskNumber());
            procesPolicyDTO.setDetail("0");
            procesPolicyDTO.setResult(procesPolicyDTO1.getResult());
            procesPolicyDTO.setProcessStrategy(procesPolicyDTO1.getProcessStrategy());
            return procesPolicyDTO;
        }else if ("待审核".equals(procesPolicyDTO1.getResult())){
            procesPolicyDTO.setFlag(0); //生成中
            procesPolicyDTO.setTaskNumber(procesPolicyVO.getTaskNumber());
            procesPolicyDTO.setDetail("0");
            procesPolicyDTO.setResult(procesPolicyDTO1.getResult());
            procesPolicyDTO.setProcessStrategy(procesPolicyDTO1.getProcessStrategy());
            return procesPolicyDTO;
        }else if ("通过".equals(procesPolicyDTO1.getResult())){
            procesPolicyDTO.setFlag(1); //通过
            procesPolicyDTO.setTaskNumber(procesPolicyVO.getTaskNumber());
            procesPolicyDTO.setDetail(procesPolicyDTO1.getDetail());
            procesPolicyDTO.setResult(procesPolicyDTO1.getResult());
            procesPolicyDTO.setProcessStrategy(procesPolicyDTO1.getProcessStrategy());
            return procesPolicyDTO;
        }else if (StringUtil.isNotEmpty(moduleRuleResult.getModuleResult())){
            procesPolicyDTO.setFlag(0); //通过
            procesPolicyDTO.setTaskNumber(procesPolicyVO.getTaskNumber());
            procesPolicyDTO.setDetail(moduleRuleResult.getModuleResult());
            return procesPolicyDTO;
        }

        return procesPolicyDTO;
    }

}
