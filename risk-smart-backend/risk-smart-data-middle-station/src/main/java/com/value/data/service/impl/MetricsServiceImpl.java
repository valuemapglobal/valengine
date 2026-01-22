package com.value.data.service.impl;

import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.value.data.common.service.RedisService;
import com.value.data.common.utils.DataStationUtil;
import com.risksmart.common.core.utils.FormulaUtil;
import com.value.data.config.FeatureProperties;
import com.value.data.domain.entity.MetricsAttributeEntity;
import com.value.data.domain.entity.MetricsModuleEntity;
import com.value.data.service.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.*;

/**
 * 分析指标服务实现类
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@Service
@Slf4j
@AllArgsConstructor
public class MetricsServiceImpl implements MetricsService {
    private final MetricsModuleService moduleService;
    private final MetricsAttributeService attributeService;
    private final FeatureProperties properties;
    private final InterfaceManageService interfaceManageService;
    private final InterfaceFieldIdManageService interfaceFieldIdManageService;
    private final ObjectMapper objectMapper;
    private final RedisService redisService;

    @Override
    public Object entryPoint(Map<String, Object> params, String name) {
        final DataStationUtil dataStationUtil = new DataStationUtil(properties.getUrl(), params, interfaceManageService,redisService);

        final MetricsModuleEntity module = moduleService.lambdaQuery()
                .eq(MetricsModuleEntity::getCode, name)
                .eq(MetricsModuleEntity::getDeptId,dataStationUtil.getUser().getDeptId())
                .one();
        final List<MetricsAttributeEntity> attributes = attributeService.lambdaQuery()
                .eq(MetricsAttributeEntity::getModuleId, module.getId())
                .list();
        final Map<String, Object> result = new HashMap<>();
        for (MetricsAttributeEntity attribute : attributes) {
            Optional.ofNullable(parseAttribute(attribute,dataStationUtil))
                    .ifPresent(v -> result.put(attribute.getCode(), v));
        }
        return result;
    }

    /**
     * 解析分析指标属性，根据配置计算出结果
     */
    private Object parseAttribute(MetricsAttributeEntity attribute,DataStationUtil dataStationUtil){
        final String variable = attribute.getVariable();
        Assert.isTrue(JSONUtil.isJsonObj(variable),"非法预属性！！");

        //解析预属性变量，替换为实际的接口返回值
        final JsonNode variableNode;
        try {
            variableNode = objectMapper.readTree(variable);
        } catch (JsonProcessingException e) {
            log.error("预属性反序列化失败",e);
            return null;
        }
        final Iterator<Map.Entry<String, JsonNode>> fieldIterator = variableNode.fields();
        final Map<String,Object> variableMap = new HashMap<>();

        while (fieldIterator.hasNext()){
            final Map.Entry<String, JsonNode> next = fieldIterator.next();
            final JsonNode value = next.getValue();

            final String interfaceNo = interfaceManageService.getInterfaceNo(value.get(1).asText());
            final String fieldName = interfaceFieldIdManageService.getInterfaceField(value.get(2).asText());
            Optional.ofNullable(dataStationUtil.respObj(interfaceNo))
                    .ifPresent(m -> variableMap.put(fieldName,m.get(fieldName)));
        }

        //替换公式中的变量
        String formula = attribute.getFormula();
        for (Map.Entry<String, Object> entry : variableMap.entrySet()) {
            formula = formula.replaceAll(entry.getKey(),entry.getValue().toString());
        }

        //执行公式
        final double result = FormulaUtil.evaluateFormula(formula);
        return result;
    }
}
