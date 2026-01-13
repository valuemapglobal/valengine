package com.value.data.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.risksmart.common.core.utils.FormulaUtil;
import com.value.data.domain.entity.InterfaceFieldIdManage;
import com.value.data.domain.entity.MetricsAttributeEntity;
import com.value.data.mapper.InterfaceFieldIdManageMapper;
import com.value.data.mapper.MetricsAttributeMapper;
import com.value.data.service.MetricsAttributeService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 分析指标属性表 服务实现类
 * </p>
 *
 * @author Vida
 * @since 2025-04-23
 */
@Service
@AllArgsConstructor
@Slf4j
public class MetricsAttributeServiceImpl extends ServiceImpl<MetricsAttributeMapper, MetricsAttributeEntity> implements MetricsAttributeService {
    private final ObjectMapper objectMapper;
    private final InterfaceFieldIdManageMapper fieldMapper;

    @Override
    public boolean isNameOrCodeExists(Long moduleId,String name, String code) {
        return this.lambdaQuery()
                .eq(MetricsAttributeEntity::getModuleId, moduleId)
                .and(wrapper -> wrapper
                        .eq(MetricsAttributeEntity::getName, name)
                        .or()
                        .eq(MetricsAttributeEntity::getCode, code)
                )
                .count() > 0;
    }

    @Override
    public boolean isNameExists(Long moduleId,String name) {
        return this.lambdaQuery()
                .eq(MetricsAttributeEntity::getModuleId, moduleId)
                .eq(MetricsAttributeEntity::getName, name)
                .count() > 0;
    }

    @Override
    public boolean isNameExistsExcludingId(Long moduleId,String name, Long id) {
        return this.lambdaQuery()
                .eq(MetricsAttributeEntity::getModuleId, moduleId)
                .eq(MetricsAttributeEntity::getName, name)
                .ne(MetricsAttributeEntity::getId, id)
                .count() > 0;
    }

    @Override
    public boolean isNameOrCodeExistsExcludingId(Long moduleId,String name, String code, Long id) {
        final Long count = baseMapper.selectCount(Wrappers.<MetricsAttributeEntity>lambdaQuery()
                .eq(MetricsAttributeEntity::getModuleId, moduleId)
                .ne(MetricsAttributeEntity::getId, id)
                .and(wrapper -> wrapper
                        .eq(MetricsAttributeEntity::getName, name)
                        .or()
                        .eq(MetricsAttributeEntity::getCode, code)
                )
        );
        return count > 0;
    }

    @Override
    public boolean isCodeExists(Long moduleId,String code) {
        return this.lambdaQuery()
                .eq(MetricsAttributeEntity::getModuleId, moduleId)
                .eq(MetricsAttributeEntity::getCode, code)
                .count() > 0;
    }

    @Override
    public boolean isCodeExistsExcludingId(Long moduleId,String code, Long id) {
        return this.lambdaQuery()
                .eq(MetricsAttributeEntity::getModuleId, moduleId)
                .eq(MetricsAttributeEntity::getCode, code)
                .ne(MetricsAttributeEntity::getId, id)
                .count() > 0;
    }

    @Override
    public boolean checkFormula(MetricsAttributeEntity attribute) {
        final String variable = attribute.getVariable();
        if (!JSONUtil.isJsonObj(variable)){
            log.error("非法预属性！！");
            return false;
        }
        //执行公式
        try{
            String formula = formulaReplace(attribute.getFormula(),variable);
            FormulaUtil.evaluateFormula(formula);
        }catch (Exception e){
            log.error("公式预执行失败",e);
            return false;
        }
        return true;
    }

    private String formulaReplace(String formula,String variable) throws JsonProcessingException {
        String newFormula = formula;
        //解析预属性变量，替换为实际的接口返回值
        final JsonNode variableNode = objectMapper.readTree(variable);;

        final Iterator<Map.Entry<String, JsonNode>> fieldIterator = variableNode.fields();
        Set<String> fieldNoSet = new HashSet<>();
        while (fieldIterator.hasNext()){ fieldNoSet.add(fieldIterator.next().getValue().path(2).asText("")); }
        final List<InterfaceFieldIdManage> fields = fieldMapper.selectList(
                Wrappers.lambdaQuery(InterfaceFieldIdManage.class)
                        .in(InterfaceFieldIdManage::getInterfaceFieldIdManage,fieldNoSet)
        );

        if (CollectionUtil.isEmpty(fields)){ return newFormula; }
        final Set<String> variables = fields.stream()
                .map(f -> Optional.ofNullable(f.getInterfaceFieldIdAlias()).orElse(f.getInterfaceFieldIdName()))
                .collect(Collectors.toSet());

        //替换公式中的变量
        for (String v : variables) { newFormula = newFormula.replaceAll(v,"1"); }
        return newFormula;
    }

    @Override
    public boolean checkAttributeDept(Long id, Long deptId) {
        return this.lambdaQuery()
                .eq(MetricsAttributeEntity::getId,id)
                .eq(MetricsAttributeEntity::getDeptId,deptId)
                .count() > 0;
    }
}
