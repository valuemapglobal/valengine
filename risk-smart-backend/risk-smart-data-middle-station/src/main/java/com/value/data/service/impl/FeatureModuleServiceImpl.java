package com.value.data.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.value.data.domain.entity.FeatureAttributeEntity;
import com.value.data.domain.entity.FeatureModuleEntity;
import com.value.data.mapper.FeatureAttributeMapper;
import com.value.data.mapper.FeatureModuleMapper;
import com.value.data.service.FeatureModuleService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * <p>
 * 特征变量模块表 服务实现类
 * </p>
 *
 * @author Vida
 * @since 2025-04-15
 */
@Service
@AllArgsConstructor
public class FeatureModuleServiceImpl
        extends ServiceImpl<FeatureModuleMapper, FeatureModuleEntity>
        implements FeatureModuleService {
    private final FeatureAttributeMapper featureAttributeMapper;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void deleteFeatureModule(Long id) {
        removeById(id);
        featureAttributeMapper.delete(
                Wrappers.<FeatureAttributeEntity>lambdaQuery().eq(FeatureAttributeEntity::getModuleId, id)
        );
    }

    @Override
    public boolean isNameOrCodeExists(String name, String code,Long moduleId,Long deptId) {
        final Long count = baseMapper.selectCount(Wrappers.<FeatureModuleEntity>lambdaQuery()
                .eq(FeatureModuleEntity::getDeptId,deptId)
                .ne(!Objects.isNull(moduleId),FeatureModuleEntity::getId,moduleId)
                .and(query -> {query.eq(StrUtil.isNotBlank(name),FeatureModuleEntity::getName, name)
                        .or(StrUtil.isNotBlank(code),or -> or.eq(FeatureModuleEntity::getCode, code));
                }));
        return count>0;
    }

    @Override
    public boolean updateModuleMetadata(Long moduleId) {
        final List<FeatureAttributeEntity> attributes = featureAttributeMapper.selectList(Wrappers.lambdaQuery(FeatureAttributeEntity.class)
                .eq(FeatureAttributeEntity::getModuleId, moduleId)
        );
        if (CollectionUtil.isEmpty(attributes)){ return true; }
        final Map<String,String> map = new HashMap<>();
        for (FeatureAttributeEntity attribute : attributes) {
            switch (attribute.getThresholdType()){
                case 0:
                    final String configThreshold = attribute.getConfigThreshold();
                    map.putAll(parseConfigThreshold(configThreshold));
                    break;
                case 1:
                    final String configCompute = attribute.getConfigCompute();
                    map.putAll(parseConfigCompute(configCompute));
                    break;
            }
        }
        List<List<String>> metadata = new ArrayList<>(map.size());
        for (Map.Entry<String, String> entry : map.entrySet()) {
            metadata.add(Arrays.asList(entry.getValue(),entry.getKey()));
        }
        final String metadataStr;
        try {
            metadataStr = objectMapper.writeValueAsString(metadata);
        } catch (JsonProcessingException e) {
            log.error("序列化失败",e);
            return false;
        }
        return this.lambdaUpdate()
                .eq(FeatureModuleEntity::getId, moduleId)
                .set(FeatureModuleEntity::getMetadata, metadataStr)
                .update();
    }

    /**
     * 解析阈值配置
     * @param configThreshold 阈值配置（JSON格式）
     * @return key=接口编号，value=供应商编号
     */
    private Map<String,String> parseConfigThreshold(String configThreshold){
        final Map<String,String> map = new HashMap<>();
        if (!JSONUtil.isJson(configThreshold)){ return map; }
        final JsonNode jsonArray;
        try {
            jsonArray = objectMapper.readTree(configThreshold);
        } catch (JsonProcessingException e) {
            log.error("阈值配置解析失败",e);
            return map;
        }
        if (jsonArray.isEmpty()){ return map; }
        for (JsonNode jsonArray2 : jsonArray) {
            for (JsonNode jsonNode : jsonArray2) {
                final JsonNode selectObj = jsonNode.path("selectObj");
                map.put(selectObj.get(1).asText(), selectObj.get(0).asText());
            }
        }
        return map;
    }

    /**
     * 解析计算配置
     * @param configCompute 计算配置（JSON格式）
     * @return key=接口编号，value=供应商编号
     */
    private Map<String,String> parseConfigCompute(String configCompute){
        final Map<String,String> map = new HashMap<>();
        if (!JSONUtil.isJson(configCompute)){ return map; }
        final JsonNode json;
        try {
            json = objectMapper.readTree(configCompute);
        } catch (JsonProcessingException e) {
            log.error("计算配置解析失败",e);
            return map;
        }
        final JsonNode selectObj = json.path("selectObj");
        map.put(selectObj.get(1).asText(), selectObj.get(0).asText());
        return map;
    }

    @Override
    public boolean checkModuleDept(Long moduleId, Long deptId) {
        return this.lambdaQuery()
                .eq(FeatureModuleEntity::getId,moduleId)
                .eq(FeatureModuleEntity::getDeptId,deptId)
                .count()>0;
    }
}
