package com.value.data.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.value.data.common.constant.FeatureOperatorEnum;
import com.value.data.common.service.RedisService;
import com.value.data.common.utils.DataStationUtil;
import com.value.data.common.utils.LocalDateTimeUtil;
import com.value.data.config.FeatureProperties;
import com.value.data.domain.entity.FeatureAttributeEntity;
import com.value.data.domain.entity.FeatureModuleEntity;
import com.value.data.service.*;
import delight.nashornsandbox.NashornSandbox;
import org.openjdk.nashorn.api.scripting.ScriptObjectMirror;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import jakarta.annotation.PreDestroy;
import javax.script.Bindings;
import java.util.*;
import java.util.function.Function;

/**
 * 特征变量服务实现类
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FeatureVariableServiceImpl implements FeatureVariableService {
    private final InterfaceManageService interfaceManageService;
    private final InterfaceFieldIdManageService interfaceFieldIdManageService;
    private final FeatureProperties properties;
    private final NashornSandbox jsEngine;
    private final FeatureModuleService featureModuleService;
    private final FeatureAttributeService featureAttributeService;
    private final ObjectMapper objectMapper;
    private final RedisService redisService;

    @PreDestroy
    public void destroy() {
        jsEngine.getExecutor().shutdown();
    }

    @Override
    public Map<String, String> getScripts(FeatureModuleEntity featureModuleEntity) {
        if (featureModuleEntity==null){ return null; }
        final List<FeatureAttributeEntity> attributeEntities = featureAttributeService.list(
                Wrappers.lambdaQuery(FeatureAttributeEntity.class)
                        .eq(FeatureAttributeEntity::getModuleId, featureModuleEntity.getId())
                        .eq(FeatureAttributeEntity::getDeptId, featureModuleEntity.getDeptId())
        );
        if (attributeEntities==null){ return null; }

        final Map<String, String> scriptMap = new HashMap<>();
        for (FeatureAttributeEntity attributeEntity : attributeEntities) {
            Optional.ofNullable(parseScript(attributeEntity))
                    .ifPresent(script -> scriptMap.put(attributeEntity.getCode(), script));
        }
        return scriptMap;
    }
    protected String parseScript(FeatureAttributeEntity attributeEntity) {
        final Integer thresholdType = attributeEntity.getThresholdType();
        final String configThreshold = attributeEntity.getConfigThreshold();
        final String configCompute = attributeEntity.getConfigCompute();

        if (thresholdType==2){ return attributeEntity.getConfigScript(); }
        if (thresholdType==1 && JSONUtil.isJson(configCompute)){ return parseConfigCompute(configCompute); }
        if (thresholdType==0 && JSONUtil.isJson(configThreshold)){ return parseConfigThreshold(configThreshold); }
        return null;
    }
    private String parseConfigThreshold(String configThreshold){
        final JsonNode jsonNode;
        try {
            jsonNode = objectMapper.readTree(configThreshold);
        }catch (Exception e){
            log.error("阈值配置解析失败",e);
            return null;
        }
        if (!jsonNode.isArray()){
            log.warn("格式错误");
            return null;
        }

        final Function<JsonNode,String> parser = (condition) -> {
            final JsonNode selectObj = condition.path("selectObj");
            final JsonNode operator = condition.path("operator");
            final JsonNode value = condition.path("value");
            final FeatureOperatorEnum featureOperatorEnum = FeatureOperatorEnum.getBySymbol(operator.asText());
            if (Objects.isNull(featureOperatorEnum)){
                log.error("不支持的操作符：{}",operator.asText());
                return null;
            }
            final String conditionStr = featureOperatorEnum.getParser().apply(
                    Arrays.asList(
                            interfaceManageService.getInterfaceNo(selectObj.get(1).asText()),
                            interfaceFieldIdManageService.getInterfaceField(selectObj.get(2).asText())
                    ),
                    value.asText()
            );
            return conditionStr;
        };
        final StringBuilder andStr = new StringBuilder();
        for (JsonNode arrayNode : jsonNode) {
            if (!arrayNode.isArray() || arrayNode.isEmpty()){ continue; }
            final StringBuilder orStr = new StringBuilder();
            for (JsonNode condition : arrayNode) {
                final String conditionStr = parser.apply(condition);
                if (StrUtil.isBlank(conditionStr)){ continue; }
                if (StrUtil.isBlank(orStr.toString())){
                    orStr.append("( ").append(conditionStr);
                }else {
                    orStr.append(" || ").append(conditionStr);
                }
            }
            if (StrUtil.isNotBlank(orStr.toString())){ orStr.append(" )"); }

            if (StrUtil.isBlank(andStr.toString())){
                andStr.append(orStr);
            }else {
                andStr.append(" && ").append(orStr);
            }
        }
        if (StrUtil.isBlank(andStr.toString())){ return andStr.toString(); }
        return String.format("(function (){return %s})()", andStr);
    }
    private String parseConfigCompute(String configCompute){
        final JsonNode jsonNode;
        try {
            jsonNode = objectMapper.readTree(configCompute);
        }catch (Exception e){
            log.error("计算配置解析失败",e);
            return null;
        }
        final JsonNode selectObj = jsonNode.path("selectObj");
        final JsonNode operator = jsonNode.path("operator");
        final FeatureOperatorEnum featureOperatorEnum = FeatureOperatorEnum.getBySymbol(operator.asText());

        if (Objects.isNull(featureOperatorEnum)){
            log.error("不支持的操作符：{}",operator.asText());
            return null;
        }
        final String conditionStr = featureOperatorEnum.getParser().apply(
                Arrays.asList(
                        interfaceManageService.getInterfaceNo(selectObj.get(1).asText()),
                        interfaceFieldIdManageService.getInterfaceField(selectObj.get(2).asText())
                ),
                null
        );
        return conditionStr;
    }

    @Override
    public Object entryPoint(Map<String, Object> params, String name) {
        final DataStationUtil dataStationUtil = new DataStationUtil(properties.getUrl(), params, interfaceManageService,redisService);

        final FeatureModuleEntity featureModuleEntity = featureModuleService.getOne(
                Wrappers.lambdaQuery(FeatureModuleEntity.class)
                        .eq(FeatureModuleEntity::getCode, name)
                        .eq(FeatureModuleEntity::getDeptId,dataStationUtil.getUser().getDeptId())
        );
        final Map<String, String> scripts = getScripts(featureModuleEntity);
        Assert.isTrue(!Objects.isNull(scripts) && !scripts.isEmpty(),"脚本为空");

        final Map<String, Object> result = new HashMap<>();
        for (String field : scripts.keySet()) {
            final Bindings bindings = jsEngine.createBindings();
            bindings.put("dataUtil", dataStationUtil);
            bindings.put("dateTimeUtil",new LocalDateTimeUtil());
            Object jsResult = null;
            try {
                log.info("开始执行脚本{}={}", field, scripts.get(field));
                jsResult = jsEngine.eval(scripts.get(field), bindings);
            }catch (Exception e){
                log.error("特征变量脚本执行异常",e);
                throw new RuntimeException(String.format("特征变量执行异常(%s-%s)：%s",  name, field, e.getMessage()));
            }
            final Object jsResult2 = bindings.get("result");
            jsResult = Objects.isNull(jsResult2)?jsResult:jsResult2;
            if (jsResult instanceof ScriptObjectMirror){
                jsResult = convertR((ScriptObjectMirror) jsResult);
            }
            result.put(field,jsResult);
            bindings.clear();
        }
        return merge(result,featureModuleEntity);
    }

    protected Object merge(Map<String, Object> result,FeatureModuleEntity module){
        if (module.getType()==1){
            final List list = new ArrayList();
            list.add(result);
            return list;
        }
        return result;
    }

    /**
     * 将 ScriptObjectMirror 转换为 Java 对象（支持循环引用）
     */
    protected Object convertR(ScriptObjectMirror source) {
        // 创建一个集合来记录已访问的对象（防止循环引用）
        Set<Object> visited = new HashSet<>();
        return convertR(source, visited);
    }
    private Object convertR(ScriptObjectMirror source, Set<Object> visited) {
        // 检查是否已经访问过该对象
        if (visited.contains(source)) {
            // 返回一个占位符，表示循环引用
            return "$CircularReference";
        }
        // 将当前对象标记为已访问
        visited.add(source);

        // 处理数组
        if (source.isArray()) {
            final Collection<Object> values = source.values();
            final List<Object> list = new ArrayList<>();
            for (Object value : values) {
                if (value instanceof ScriptObjectMirror) {
                    list.add(convertR((ScriptObjectMirror) value, visited));
                } else {
                    list.add(value);
                }
            }
            return list;
        }
        // 处理普通对象
        final Set<Map.Entry<String, Object>> entries = source.entrySet();
        Map<String, Object> map = new HashMap<>();
        for (Map.Entry<String, Object> entry : entries) {
            final Object value = entry.getValue();
            if (value instanceof ScriptObjectMirror) {
                map.put(entry.getKey(), convertR((ScriptObjectMirror) value, visited));
            } else {
                map.put(entry.getKey(), value);
            }
        }

        return map;
    }
}
