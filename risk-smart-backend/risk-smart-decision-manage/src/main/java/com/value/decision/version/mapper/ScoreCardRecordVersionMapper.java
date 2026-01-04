package com.value.decision.version.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.value.decision.version.domain.ScoreCardRecordVersion;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 评分卡主表 Mapper 接口
 * </p>
 *
 * @author hc
 * @since 2023-08-10
 */
public interface ScoreCardRecordVersionMapper extends BaseMapper<ScoreCardRecordVersion> {

    //批量插入
    public void insertBatch(@Param("cardRecordList") List<ScoreCardRecordVersion> cardRecordList);
}
