package com.value.decision.version.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.value.decision.version.domain.ScoreIndexRuleVersion;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 指标规则表 Mapper 接口
 * </p>
 *
 * @author hc
 * @since 2023-08-10
 */
public interface ScoreIndexRuleVersionMapper extends BaseMapper<ScoreIndexRuleVersion> {

    //批量插入
    public void insertBatch(@Param("indexRuleList") List<ScoreIndexRuleVersion> indexRuleList);

    @Select(" select score_primary_id from score_index_rule_version where scord_card_id = #{scoreCard} \n" +
            "                                                        and data_state = 0 and button_state = 1 \n" +
            "        group by score_primary_id")
    List<Integer> getPrimaryCardById(@Param("scoreCard") String scoreCard);
}
