package com.value.data.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.risksmart.common.core.utils.StringUtils;
import com.value.data.common.constant.InterfaceFieldIdTypeEnum;
import com.value.data.common.model.LoginUser;
import com.value.data.common.service.IdGeneratorService;
import com.value.data.domain.entity.InterfaceFieldIdManage;
import com.value.data.domain.entity.RdeRiskVariableGroup;
import com.value.data.domain.entity.RdeRiskVariableRecord;
import com.value.data.mapper.InterfaceFieldIdManageMapper;
import com.value.data.mapper.InterfaceManageMapper;
import com.value.data.mapper.RdeRiskVariableGroupMapper;
import com.value.data.mapper.RdeRiskVariableRecordMapper;
import com.value.data.service.InterfaceFieldIdManageService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 接口参数管理表 服务实现类
 * </p>
 *
 * @author bing
 * @since 2023-08-16
 */
@Service
@AllArgsConstructor
public class InterfaceFieldIdManageServiceImpl extends ServiceImpl<InterfaceFieldIdManageMapper, InterfaceFieldIdManage> implements InterfaceFieldIdManageService {
    private final IdGeneratorService idGeneratorService;
    private final InterfaceManageMapper interfaceManageMapper;
    private final RdeRiskVariableRecordMapper recordMapper;
    private final RdeRiskVariableGroupMapper groupMapper;
    private final TransactionTemplate transactionTemplate;

    @Override
    public Boolean validInterfaceFieldIdNo(String fieldIdNo) {
        LambdaQueryWrapper<InterfaceFieldIdManage> lambdaQuery = Wrappers.lambdaQuery();
        lambdaQuery.eq(InterfaceFieldIdManage::getInterfaceFieldIdManage, fieldIdNo);
        long count = count(lambdaQuery);
        return count > 0;
    }

    @Override
    public Boolean validManageNo(String manageNo) {
        LambdaQueryWrapper<InterfaceFieldIdManage> lambdaQuery = Wrappers.lambdaQuery();
        lambdaQuery.eq(InterfaceFieldIdManage::getInterfaceManageNo, manageNo);
        long count = count(lambdaQuery);
        return count > 0;
    }

    @Override
    public Boolean validName(String manageNo, String interfaceFieldIdName, Integer interfaceFieldIdType,String parent,String fieldNo) {
        LambdaQueryWrapper<InterfaceFieldIdManage> lambdaQuery = Wrappers.lambdaQuery();
        lambdaQuery.eq(InterfaceFieldIdManage::getInterfaceManageNo, manageNo)
                .eq(InterfaceFieldIdManage::getInterfaceFieldIdName, interfaceFieldIdName)
                .eq(InterfaceFieldIdManage::getInterfaceFieldIdType, interfaceFieldIdType);
        if (parent!=null)
            lambdaQuery.eq(InterfaceFieldIdManage::getInterfaceFieldIdFather,parent);
        else
            lambdaQuery.isNull(InterfaceFieldIdManage::getInterfaceFieldIdFather);
        List<InterfaceFieldIdManage> list = list(lambdaQuery);
        if (list==null || list.isEmpty())
            return false;
        if (StringUtils.isNotEmpty(fieldNo)){
            for (InterfaceFieldIdManage interfaceFieldIdManage : list) {
                if (!interfaceFieldIdManage.getInterfaceFieldIdManage().equals(fieldNo))
                    return true;
            }
            return false;
        }
        return true;
    }

    @Override
    public Boolean validAlias(String manageNo, String interfaceFieldIdAlias, Integer interfaceFieldIdType,String parent,String fieldNo) {
        LambdaQueryWrapper<InterfaceFieldIdManage> lambdaQuery = Wrappers.lambdaQuery();
        lambdaQuery.eq(InterfaceFieldIdManage::getInterfaceManageNo, manageNo)
                .eq(InterfaceFieldIdManage::getInterfaceFieldIdAlias, interfaceFieldIdAlias)
                .eq(InterfaceFieldIdManage::getInterfaceFieldIdType, interfaceFieldIdType);
        if (parent!=null)
            lambdaQuery.eq(InterfaceFieldIdManage::getInterfaceFieldIdFather,parent);
        else
            lambdaQuery.isNull(InterfaceFieldIdManage::getInterfaceFieldIdFather);
        List<InterfaceFieldIdManage> list = list(lambdaQuery);
        if (list==null || list.isEmpty())
            return false;
        if (StringUtils.isNotEmpty(fieldNo)){
            for (InterfaceFieldIdManage interfaceFieldIdManage : list) {
                if (!interfaceFieldIdManage.getInterfaceFieldIdManage().equals(fieldNo))
                    return true;
            }
            return false;
        }
        return true;
    }

    @Override
    public String getInterfaceField(String interfaceFieldIdManage) {
        final InterfaceFieldIdManage fieldId = this.getOne(
                Wrappers.lambdaQuery(InterfaceFieldIdManage.class)
                        .eq(InterfaceFieldIdManage::getInterfaceFieldIdManage, interfaceFieldIdManage)
        );
        return Objects.isNull(fieldId)?null:fieldId.getInterfaceFieldIdAlias();
    }

    @Override
    public List<InterfaceFieldIdManage> analyzeInterfaceField(String interfaceManageNo,
                                                            String interfaceNo,
                                                            InterfaceFieldIdTypeEnum fieldIdType,
                                                            JsonNode jsonNode,
                                                            LoginUser user) {
        Iterator<String> fieldNameIterator = jsonNode.fieldNames();
        int index = 0;
        final List<InterfaceFieldIdManage> list = new ArrayList<>();
        while (fieldNameIterator.hasNext()){
            String fieldName = fieldNameIterator.next();
            InterfaceFieldIdManage field = new InterfaceFieldIdManage()
                    .setInterfaceFieldIdManage(String.valueOf(idGeneratorService.generateUniqueId()))
                    .setInterfaceManageNo(interfaceManageNo)
                    .setInterfaceNo(interfaceNo)
                    .setInterfaceFieldIdName(fieldName)
                    .setInterfaceFieldIdAlias(fieldName)
                    .setInterfaceFieldIdDescription(fieldName)
                    .setInterfaceFieldIdType(fieldIdType.getCode())
                    .setInterfaceFieldIdDataType(getDataType(jsonNode.get(fieldName)))
                    .setInterfaceFieldIdRequired(0)
                    .setInterfaceFieldIdIndex(index)
                    .setInterfaceFieldIdRemark(fieldName)
                    .setCreateBy(user.getUsername())
                    .setUpdateBy(user.getUsername())
                    .setUserId(user.getUserid())
                    .setDeptId(user.getSysUser().getDeptId());
            list.add(field);
            index++;
        }
        return list;
    }
    private int getDataType(JsonNode jsonNode){
        if (jsonNode.isDouble()){
            return 7;
        }
        if (jsonNode.isInt()){
            return 0;
        }
        if (jsonNode.isTextual()){
            return 1;
        }
        if (jsonNode.isBoolean()){
            return 6;
        }
        if (jsonNode.isArray()){
            return 4;
        }
        if (jsonNode.isObject()){
            return 3;
        }
        return -1;
    }

    @Override
    public int saveFields(List<InterfaceFieldIdManage> fields) {
        final List<String> manageNos = fields.stream()
                .map(InterfaceFieldIdManage::getInterfaceManageNo)
                .distinct()
                .collect(Collectors.toList());
        final Map<String, RdeRiskVariableGroup> groupMap = groupMapper.selectList(
                Wrappers.lambdaQuery(RdeRiskVariableGroup.class)
                        .in(RdeRiskVariableGroup::getInterfaceManageNo, manageNos)
        ).stream().collect(Collectors.toMap(RdeRiskVariableGroup::getInterfaceManageNo, g -> g));
        List<RdeRiskVariableRecord> records = fields.stream().map(f -> {
            RdeRiskVariableRecord record = new RdeRiskVariableRecord();
            record.setCode(StrUtil.isBlank(f.getInterfaceFieldIdAlias()) ? f.getInterfaceFieldIdName() : f.getInterfaceFieldIdAlias())
                    .setName(f.getInterfaceFieldIdDescription())
                    .setType(f.getInterfaceFieldIdDataType().toString())
                    .setRemark(f.getInterfaceFieldIdDescription())
                    .setCreateUserId(f.getUserId().intValue())
                    .setGroupNo(groupMap.get(f.getInterfaceManageNo()).getGroupNo())
                    .setRecordNo(String.valueOf(idGeneratorService.generateUniqueId()))
                    .setParentNo(f.getInterfaceFieldIdFather())
                    .setInterfaceFieldIdManage(f.getInterfaceFieldIdManage())
                    .setUserId(f.getUserId())
                    .setDeptId(f.getDeptId());
            return record;
        }).collect(Collectors.toList());
        transactionTemplate.execute(status -> {
            this.saveBatch(fields);
            records.forEach(recordMapper::insert);
            return null;
        });
        return fields.size();
    }


    @Override
    public boolean checkInterfaceField(InterfaceFieldIdManage field) {
        LambdaQueryWrapper<InterfaceFieldIdManage> query = Wrappers.lambdaQuery(InterfaceFieldIdManage.class)
                .eq(InterfaceFieldIdManage::getInterfaceFieldIdName, field.getInterfaceFieldIdName())
                .eq(InterfaceFieldIdManage::getInterfaceFieldIdAlias, field.getInterfaceFieldIdAlias())
                .eq(InterfaceFieldIdManage::getInterfaceFieldIdType,field.getInterfaceFieldIdType())
                .eq(InterfaceFieldIdManage::getInterfaceManageNo, field.getInterfaceManageNo());
        if (field.getId()!=null){
            query.ne(InterfaceFieldIdManage::getId, field.getId());
        }
        InterfaceFieldIdManage f = baseMapper.selectOne(query);
        return f==null;
    }

    @Override
    public List<InterfaceFieldIdManage> listNotSync() {
        return baseMapper.listNotSync();
    }
}
