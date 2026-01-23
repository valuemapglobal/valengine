package com.value.decision.process.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.value.decision.process.model.ProcessNode;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 流程节点表 Mapper 接口
 * </p>
 *
 * @author hc
 * @since 2023-08-16
 */
public interface ProcessNodeMapper extends BaseMapper<ProcessNode> {
    /**
     * 检查评分卡是否被有效的流程策略引用
     * @param scoreCardId 评分卡ID
     * @return 是否被引用
     */
    boolean isScoreCardReferencedByActivePolicy(@Param("scoreCardId") Integer scoreCardId);
}
