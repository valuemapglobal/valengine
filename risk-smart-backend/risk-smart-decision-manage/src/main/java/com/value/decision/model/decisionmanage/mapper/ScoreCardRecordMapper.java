package com.value.decision.model.decisionmanage.mapper;

import com.value.decision.model.decisionmanage.model.ScoreCardRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 评分卡主表 Mapper 接口
 * </p>
 *
 * @author hc
 * @since 2023-08-08
 */
public interface ScoreCardRecordMapper extends BaseMapper<ScoreCardRecord> {

    //批量修改
    void updateVersionList(ScoreCardRecord fraudList);

    //批量插入
    public void insertBatch(@Param("cardRecordList") List<ScoreCardRecord> cardRecordList);

}
