package com.value.data.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.value.data.constant.ServiceConstant;
import com.value.data.converter.WrapperConverter;
import com.value.data.domain.dto.InterfaceQueryDTO;
import com.value.data.domain.entity.*;
import com.value.data.mapper.RdeRiskVariableGroupMapper;
import com.value.data.mapper.RdeRiskVariableRecordMapper;
import com.value.data.service.DecisionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 决策服务实现类
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@Service
@AllArgsConstructor
@Slf4j
public class DecisionServiceImpl implements DecisionService {
    private final ServiceConstant serviceConstant;
    private final RdeRiskVariableGroupMapper rdeRiskVariableGroupMapper;
    private final RdeRiskVariableRecordMapper rdeRiskVariableRecordMapper;

    @Override
    public Map<String, Map<String,Object>> decisionQI(InterfaceQueryDTO dto) {
        final List<InterfaceManage> iList = serviceConstant.interfaceManageService.list(WrapperConverter.convert(dto));
        if (CollectionUtil.isEmpty(iList)){
            return null;
        }
        final List<InterfaceSourceManage> sList = serviceConstant.interfaceSourceManageService.list(
                Wrappers.lambdaQuery(InterfaceSourceManage.class)
                        .eq(InterfaceSourceManage::getDeptId,dto.getDeptId())
                        .in(InterfaceSourceManage::getInterfaceSourceNo,
                            iList.stream().map(InterfaceManage::getInterfaceSourceNo).collect(Collectors.toList())
                        )
        );
        final Map<String, List<InterfaceSourceManage>> sMap = sList.stream()
                .collect(Collectors.groupingBy(InterfaceSourceManage::getInterfaceSourceNo));
        final Map<String, Map<String,Object>> map = new HashMap<>();
        for (InterfaceManage interfaceManage : iList) {
            Map<String,Object> fMap = new HashMap<>();
            fMap.put("INFO", interfaceManage);
            final List<InterfaceSourceManage> source = sMap.get(interfaceManage.getInterfaceSourceNo());
            if (CollectionUtil.isEmpty(source)){
                continue;
            }
            handleMetadata(interfaceManage,fMap);
            map.put(interfaceManage.getInterfaceNo(),fMap);
        }
        return map;
    }
    /**
     * 处理元数据
     */
    private boolean handleMetadata(InterfaceManage interfaceManage,Map<String,Object> fMap){
        final RdeRiskVariableGroup group = rdeRiskVariableGroupMapper.selectOne(Wrappers.lambdaQuery(RdeRiskVariableGroup.class)
                .eq(RdeRiskVariableGroup::getInterfaceManageNo, interfaceManage.getInterfaceManageNo())
        );
        if (group == null){ return false; }
        final List<RdeRiskVariableRecord> records = rdeRiskVariableRecordMapper.selectList(Wrappers.lambdaQuery(RdeRiskVariableRecord.class)
                .eq(RdeRiskVariableRecord::getGroupNo, group.getGroupNo())
        );
        if (CollectionUtil.isEmpty(records)){ return false; }
        for (RdeRiskVariableRecord record : records) {
            fMap.put(record.getCode(), new HashMap<String,String>(){{
                put("l1",group.getThemeNo());
                put("l2",record.getGroupNo());
                put("l3",record.getRecordNo());
            }});
        }
        return true;
    }
}
