package com.value.decision.model.decisionmanage.mapper;

import com.value.decision.model.decisionmanage.model.ScoreIndexRuleSnapshot;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 指标规则表 Mapper 接口
 * </p>
 *
 * @author hc
 * @since 2023-08-10
 */
public interface ScoreIndexRuleSnapshotMapper extends BaseMapper<ScoreIndexRuleSnapshot> {

    @Select("select default_rule as value,count(1) as count from score_index_rule where score_primary_id = #{scorePrimaryId} and data_state = 0 group by default_rule ")
    List<HashMap<String,Object>> selectCoungByDefaultRule(@Param("scorePrimaryId") Integer scorePrimaryId);

    @Update("update score_index_rule set level = #{sort} where id = #{id}")
    void updateSortById(@Param("id") Integer id, @Param("sort") Integer sort);

    List<Integer> getPrimaryCardById(@Param("scoreCard") String scoreCard);

}
