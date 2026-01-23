package com.value.decision.model.decisionmanage.mapper;

import com.value.decision.model.decisionmanage.model.DataCalling;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 数据调用表 Mapper 接口
 * </p>
 *
 * @author hc
 * @since 2023-11-13
 */
public interface DataCallingMapper extends BaseMapper<DataCalling> {

    @Select("select * from sheet1 s inner join card c on s.sfzh = c.idcard where flag is null ")
    List<Map<String,Object>> selectIdCard();

    @Update("update sheet1 set result = #{result} and flag =1  where sfzh = #{sfzh} ")
    void updateIdCard(@Param("sfzh") Object sfzh,@Param("result")  String result);

    /**
     * 根据任务编号查询历史调用数据
     */
    @Select("SELECT * FROM data_calling WHERE orderNo = #{taskNo} AND callStatus = '查得' ORDER BY createTime ASC")
    List<DataCalling> selectByTaskNo(@Param("taskNo") String taskNo);

    /**
     * 根据任务编号和接口类型查询历史数据
     */
    @Select("SELECT * FROM data_calling WHERE orderNo = #{taskNo} AND interfaceType = #{interfaceName} AND callStatus = '查得' LIMIT 1")
    DataCalling selectByTaskNoAndInterface(@Param("taskNo") String taskNo, @Param("interfaceName") String interfaceName);
}
