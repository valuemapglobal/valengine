package com.value.data.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.value.data.common.utils.StringUtils;
import com.value.data.constant.ServiceConstant;
import com.value.data.domain.entity.*;
import com.value.data.domain.vo.InterfaceInfo;
import com.value.data.mapper.*;
import com.value.data.service.IntegrationService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Vida
 * @date 2023年08月21日 11:55
 * @description
 */
@Service
public class IntegrationServiceImpl implements IntegrationService {
    private final ServiceConstant serviceConstant;
    private final RdeRiskVariableThemeMapper themeMapper;
    private final RdeRiskVariableGroupMapper groupMapper;
    private final RdeRiskVariableRecordMapper recordMapper;

    public IntegrationServiceImpl(ServiceConstant serviceConstant, RdeRiskVariableThemeMapper themeMapper, RdeRiskVariableGroupMapper groupMapper, RdeRiskVariableRecordMapper recordMapper) {
        this.serviceConstant = serviceConstant;
        this.themeMapper = themeMapper;
        this.groupMapper = groupMapper;
        this.recordMapper = recordMapper;
    }

    @Override
    public Map<String,Object> tree(Long deptId) {
        List<Map<String,Object>> metadataTree = metadataTree(deptId);

        Map<String,Object> tree = new HashMap<>();
        if (metadataTree!=null && !metadataTree.isEmpty()){
            tree.put("metadata",metadataTree);
        }

        return tree;
    }

    @Override
    public List<Map<String,Object>> metadataTree(Long deptId) {
        // 1. 批量查询所有数据（优化N+1问题，原来每层循环都查数据库）
        List<RdeRiskVariableTheme> themes = themeMapper.selectList(
                Wrappers.lambdaQuery(RdeRiskVariableTheme.class).eq(RdeRiskVariableTheme::getDeptId, deptId)
        );
        if (themes == null || themes.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> themeNos = themes.stream().map(RdeRiskVariableTheme::getThemeNo).collect(Collectors.toList());

        // 批量查询所有groups
        List<RdeRiskVariableGroup> allGroups = groupMapper.selectList(
                Wrappers.lambdaQuery(RdeRiskVariableGroup.class)
                        .in(RdeRiskVariableGroup::getThemeNo, themeNos)
                        .eq(RdeRiskVariableGroup::getDeptId, deptId)
        );

        List<String> groupNos = allGroups.stream().map(RdeRiskVariableGroup::getGroupNo).collect(Collectors.toList());

        // 批量查询所有records
        List<RdeRiskVariableRecord> allRecords = Collections.emptyList();
        if (!groupNos.isEmpty()) {
            allRecords = recordMapper.selectList(
                    Wrappers.lambdaQuery(RdeRiskVariableRecord.class)
                            .in(RdeRiskVariableRecord::getGroupNo, groupNos)
                            .eq(RdeRiskVariableRecord::getDeptId, deptId)
            );
        }

        // 批量查询所有接口信息
        List<String> interfaceManageNos = allGroups.stream()
                .map(RdeRiskVariableGroup::getInterfaceManageNo)
                .filter(StringUtils::isNotEmpty)
                .distinct()
                .collect(Collectors.toList());

        Map<String, InterfaceManage> interfaceManageMap = Collections.emptyMap();
        if (!interfaceManageNos.isEmpty()) {
            List<InterfaceManage> allInterfaces = serviceConstant.interfaceManageService.list(
                    Wrappers.lambdaQuery(InterfaceManage.class)
                            .in(InterfaceManage::getInterfaceManageNo, interfaceManageNos)
                            .eq(InterfaceManage::getDeptId, deptId)
            );
            interfaceManageMap = allInterfaces.stream()
                    .collect(Collectors.toMap(InterfaceManage::getInterfaceManageNo, i -> i, (a, b) -> a));
        }

        // 2. 按层级分组建立索引
        Map<String, List<RdeRiskVariableGroup>> groupsByTheme = allGroups.stream()
                .collect(Collectors.groupingBy(RdeRiskVariableGroup::getThemeNo));

        Map<String, List<RdeRiskVariableRecord>> recordsByGroup = allRecords.stream()
                .collect(Collectors.groupingBy(RdeRiskVariableRecord::getGroupNo));

        // 3. 内存组装树结构
        final Map<String, InterfaceManage> finalInterfaceMap = interfaceManageMap;

        return themes.stream().map(theme -> {
            Map<String, Object> themeTree = new HashMap<>();
            themeTree.put("value", theme.getThemeNo());
            themeTree.put("label", theme.getName());
            themeTree.put("code", theme.getKeycode());

            List<RdeRiskVariableGroup> groups = groupsByTheme.get(theme.getThemeNo());
            if (groups != null && !groups.isEmpty()) {
                List<Map<String, Object>> groupTrees = groups.stream().map(group -> {
                    Map<String, Object> groupTree = new HashMap<>();
                    groupTree.put("value", group.getGroupNo());
                    groupTree.put("label", group.getName());
                    groupTree.put("code", group.getKeycode());
                    groupTree.put("objectName", group.getName());
                    groupTree.put("anotherName", group.getKeycode());

                    // 从Map获取接口信息
                    if (StringUtils.isNotEmpty(group.getInterfaceManageNo())) {
                        InterfaceManage interfaceManage = finalInterfaceMap.get(group.getInterfaceManageNo());
                        if (interfaceManage != null) {
                            InterfaceInfo interfaceInfo = new InterfaceInfo();
                            interfaceInfo.setSourceNo(interfaceManage.getInterfaceSourceNo());
                            interfaceInfo.setManageNo(interfaceManage.getInterfaceManageNo());
                            interfaceInfo.setInterfaceNo(interfaceManage.getInterfaceNo());
                            interfaceInfo.setInterfaceType(interfaceManage.getInterfaceType());
                            interfaceInfo.setReturnType(interfaceManage.getReturnType());
                            groupTree.put("objectFlag", interfaceManage.getReturnType());
                            groupTree.put("interface", Collections.singletonList(interfaceInfo));
                        }
                    }

                    // 从Map获取records
                    List<RdeRiskVariableRecord> records = recordsByGroup.get(group.getGroupNo());
                    if (records != null && !records.isEmpty()) {
                        List<Map<String, Object>> recordTrees = records.stream().map(record -> {
                            Map<String, Object> recordTree = new HashMap<>();
                            recordTree.put("value", record.getRecordNo());
                            recordTree.put("label", record.getName());
                            recordTree.put("code", record.getCode());
                            recordTree.put("stats", record.getName());
                            recordTree.put("dataType", "1");
                            recordTree.put("resultType", record.getType());
                            return recordTree;
                        }).collect(Collectors.toList());
                        groupTree.put("children", recordTrees);
                    }
                    return groupTree;
                }).collect(Collectors.toList());
                themeTree.put("children", groupTrees);
            }
            return themeTree;
        }).collect(Collectors.toList());
    }
}
