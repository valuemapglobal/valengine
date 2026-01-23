package com.value.decision.model.decisionmanage.mapper;

import com.value.decision.model.decisionmanage.model.ScorePrimaryIndex;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 一级指标存储表 Mapper 接口
 * </p>
 *
 * @author hc
 * @since 2023-08-08
 */
public interface ScorePrimaryIndexMapper extends BaseMapper<ScorePrimaryIndex> {

    //批量修改
    void updateVersionList(ScorePrimaryIndex fraudList);

    //批量插入
    public void insertBatch(@Param("primaryIndexList") List<ScorePrimaryIndex> primaryIndexList);

}
