package com.value.data.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.value.data.domain.entity.MetricsAttributeEntity;
import com.value.data.domain.entity.MetricsModuleEntity;
import com.value.data.mapper.MetricsAttributeMapper;
import com.value.data.mapper.MetricsModuleMapper;
import com.value.data.service.MetricsModuleService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 分析指标模块表 服务实现类
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@Service
@AllArgsConstructor
public class MetricsModuleServiceImpl extends ServiceImpl<MetricsModuleMapper, MetricsModuleEntity> implements MetricsModuleService {
    private final MetricsAttributeMapper attributeMapper;
    private final ObjectMapper objectMapper;

    @Override
    public boolean isNameOrCodeExists(String name, String code,Long moduleId,Long deptId) {
        return this.lambdaQuery().ne(!Objects.isNull(moduleId),MetricsModuleEntity::getId,moduleId)
                .eq(MetricsModuleEntity::getDeptId,deptId)
                .and(query -> query.eq(StrUtil.isNotBlank(name),MetricsModuleEntity::getName, name)
                        .or(StrUtil.isNotBlank(code),or -> or.eq(MetricsModuleEntity::getCode, code))
                )
                .count() > 0;
    }

    @Override
    public boolean updateModuleAssociation(Long moduleId) {
        final List<MetricsAttributeEntity> attributes = attributeMapper.selectList(
                Wrappers.lambdaQuery(MetricsAttributeEntity.class).eq(MetricsAttributeEntity::getModuleId, moduleId)
        );
        if (CollectionUtil.isEmpty(attributes)){ return true; }
        final Map<String,String> map = new HashMap<>();
        for (MetricsAttributeEntity attribute : attributes) {
            final String variable = attribute.getVariable();
            map.putAll(parseVariable(variable));
        }
        final List<List<String>> association = new ArrayList<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            association.add(Arrays.asList(entry.getValue(),entry.getKey()));
        }
        final String associationStr;
        try {
            associationStr = objectMapper.writeValueAsString(association);
        } catch (JsonProcessingException e) {
            log.error("association序列化失败",e);
            return false;
        }
        return this.lambdaUpdate()
                .eq(MetricsModuleEntity::getId, moduleId)
                .set(MetricsModuleEntity::getAssociation, associationStr)
                .update();
    }

    /**
     * 解析预变量
     * @param variable 预变量定义（JSON格式）
     * @return key=接口编号，value=供应商编号
     */
    private Map<String,String> parseVariable(String variable){
        final Map<String,String> map = new HashMap<>();
        if (!JSONUtil.isJsonObj(variable)){ return  map; }
        final JsonNode jsonNode;
        try {
            jsonNode = objectMapper.readTree(variable);
        } catch (JsonProcessingException e) {
            log.error("预变量反序列化异常");
            return map;
        }
        final Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
        while (fields.hasNext()){
            final Map.Entry<String, JsonNode> entry = fields.next();
            final JsonNode selectObj = entry.getValue();
            map.put(selectObj.get(1).asText(),selectObj.get(0).asText());
        }
        return map;
    }

    @Override
    public boolean checkModuleDept(Long moduleId, Long deptId) {
        return this.lambdaQuery().eq(MetricsModuleEntity::getId,moduleId)
               .eq(MetricsModuleEntity::getDeptId,deptId)
               .count() > 0;
    }
}
