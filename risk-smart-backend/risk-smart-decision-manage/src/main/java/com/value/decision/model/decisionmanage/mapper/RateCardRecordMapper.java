package com.value.decision.model.decisionmanage.mapper;

import com.value.decision.model.decisionmanage.model.RateCardRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 评级卡主表 Mapper 接口
 * </p>
 *
 * @author hc
 * @since 2023-08-10
 */
public interface RateCardRecordMapper extends BaseMapper<RateCardRecord> {
    /**
     * 检查评分卡是否被有效的评级卡引用
     * @param scoreCardId 评分卡ID
     * @return 是否被引用
     */
    boolean isScoreCardReferencedByActiveRateCard(@Param("scoreCardId") Integer scoreCardId);
}
