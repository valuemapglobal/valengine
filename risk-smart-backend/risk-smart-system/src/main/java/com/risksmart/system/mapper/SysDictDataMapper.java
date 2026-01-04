package com.risksmart.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.risksmart.system.domain.SysDictData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 字典数据 数据层
 */
@Mapper
public interface SysDictDataMapper extends BaseMapper<SysDictData> {

    /**
     * 根据字典类型查询字典数据
     */
    @Select("SELECT * FROM sys_dict_data WHERE dict_type = #{dictType} AND status = '0' ORDER BY dict_sort ASC")
    List<SysDictData> selectDictDataByType(@Param("dictType") String dictType);
}
