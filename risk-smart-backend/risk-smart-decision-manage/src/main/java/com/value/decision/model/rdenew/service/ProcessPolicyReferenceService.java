package com.value.decision.model.rdenew.service;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.value.decision.process.mapper.ProcessNodeMapper;
import com.value.decision.process.model.ProcessNode;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Austin
 * Create by 2025/6/18 11:59
 */
@Log4j2
@Service
public class ProcessPolicyReferenceService {

    @Autowired
    private ProcessNodeMapper processNodeMapper;

    /**
     * 检查对象是否被流程策略引用
     * @param codeId 对象ID（可以是模型ID、规则组ID或规则明细ID）
     * @return 是否被引用
     */
    public boolean checkIfReferencedByProcessPolicy(Integer codeId) {
        if (codeId == null) {
            return false;
        }

        try {
            LambdaQueryWrapper<ProcessNode> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ProcessNode::getRuleCode, codeId)
                    .eq(ProcessNode::getDataStatus, 0)
                    .last("limit 1");
            Long count = processNodeMapper.selectCount(queryWrapper);

            return count > 0;
        } catch (Exception e) {
            log.error("检查对象是否被流程策略引用时发生异常", e);
            return false;
        }
    }
}
