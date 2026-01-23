package com.value.decision.version.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.value.decision.version.domain.ScorePrimaryIndexVersion;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 一级指标存储表 Mapper 接口
 * </p>
 *
 * @author hc
 * @since 2023-08-10
 */
public interface ScorePrimaryIndexVersionMapper extends BaseMapper<ScorePrimaryIndexVersion> {

    //批量插入
    public void insertBatch(@Param("primaryIndexList") List<ScorePrimaryIndexVersion> primaryIndexList);
}
