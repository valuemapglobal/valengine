package com.value.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.value.data.domain.dto.InterfaceChargingDTO;
import com.value.data.domain.entity.InterfaceManage;
import com.value.data.domain.vo.InterfaceChargingListVo;
import com.value.data.domain.vo.TreeInterfaceChildrenVO;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 接口管理表 Mapper 接口
 * </p>
 *
 * @author bing
 * @since 2023-08-14
 */
public interface InterfaceManageMapper extends BaseMapper<InterfaceManage> {

    List<InterfaceChargingListVo> queryInterfaceChargingList(InterfaceChargingDTO interfaceChargingDTO);

    //查询今日接口计费总额
    List<BigDecimal> queryTodayChargingCount(@Param("deptId") Long deptId);

    //查询今日接口计费更新时间
    String queryTodayUpdateTime(@Param("deptId") Long deptId);

    //查询总的接口计费总额
    List<BigDecimal>  queryAllChargingCount(@Param("deptId")Long deptId);

    //查询总的接口计费更新时间
    String queryAllUpdateTime(@Param("deptId")Long deptId);

    List<TreeInterfaceChildrenVO> listBySourceNo(String interfaceSourceNo);

    /**
     * 查询所有未同步到数据平台的接口接口
     */
    List<InterfaceManage> listNotSync();
}
