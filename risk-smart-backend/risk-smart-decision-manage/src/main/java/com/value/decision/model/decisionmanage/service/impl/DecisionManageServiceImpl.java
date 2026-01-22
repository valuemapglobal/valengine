package com.value.decision.model.decisionmanage.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.value.decision.common.converter.ModelVersionClassificationConverter;
import com.risksmart.common.core.exception.ServiceException;
import com.value.decision.model.decisionmanage.controller.feign.DataMiddleStationFeign;
import com.value.decision.model.decisionmanage.mapper.BusinessMapper;
import com.value.decision.model.decisionmanage.mapper.ProductMapper;
import com.value.decision.model.decisionmanage.model.Business;
import com.value.decision.model.decisionmanage.model.Product;
import com.value.decision.model.decisionmanage.model.dto.ImportRuleWithJsonDTO;
import com.value.decision.model.decisionmanage.model.dto.InterfaceQueryDTO;
import com.value.decision.model.decisionmanage.model.vo.HttpResult;
import com.value.decision.model.decisionmanage.service.DecisionManageService;
import com.value.decision.model.decisionmanage.service.RuoYiService;
import com.value.decision.model.rdenew.domain.RdeModelAntiFraud;
import com.value.decision.model.rdenew.domain.RdeModelAntiFraudRuleGroup;
import com.value.decision.model.rdenew.domain.RdeModelAntiFraudRuleRecord;
import com.value.decision.model.rdenew.domain.RdeModelDecisionCodeLevel;
import com.value.decision.model.rdenew.mapper.RdeModelAntiFraudMapper;
import com.value.decision.model.rdenew.mapper.RdeModelAntiFraudRuleGroupMapper;
import com.value.decision.model.rdenew.mapper.RdeModelAntiFraudRuleRecordMapper;
import com.value.decision.model.rdenew.mapper.RdeModelDecisionCodeLevelMapper;
import com.value.decision.version.common.util.StringUtil;
import com.value.decision.version.domain.ModelVersionClassification;
import com.value.decision.version.mapper.ModelVersionClassificationMapper;
import com.value.decision.common.security.LoginUser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 决策管理服务实现类
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@Service
@Slf4j
@AllArgsConstructor
public class DecisionManageServiceImpl implements DecisionManageService {
    private final RdeModelAntiFraudMapper rmafm;
    private final RdeModelAntiFraudRuleGroupMapper rmafrgm;
    private final RdeModelAntiFraudRuleRecordMapper rmafrrm;
    private final RdeModelDecisionCodeLevelMapper rmdclm;
    private final ObjectMapper objectMapper;
    private final ProductMapper productMapper;
    private final BusinessMapper businessMapper;
    private final DataMiddleStationFeign dataMiddleStationFeign;
    private final ModelVersionClassificationMapper mvcm;
    private final RuoYiService ruoYiService;

    @Override
    public String getRuleJson(Integer strategyId) throws JsonProcessingException {
        //获取规则
        final RdeModelAntiFraud rmaf = rmafm.selectOne(
                Wrappers.lambdaQuery(RdeModelAntiFraud.class).eq(RdeModelAntiFraud::getId, strategyId)
        );
        if (Objects.isNull(rmaf)){ return "{}"; }

        //获取规则组
        final List<RdeModelAntiFraudRuleGroup> rmafrgs = rmafrgm.selectList(
                Wrappers.lambdaQuery(RdeModelAntiFraudRuleGroup.class)
                        .eq(RdeModelAntiFraudRuleGroup::getModelId,rmaf.getId())
        );
        if (CollectionUtil.isEmpty(rmafrgs)){ return objectMapper.writeValueAsString(rmaf); }
        //处理规则和规则组之间的关系
        rmaf.setGroups(rmafrgs);

        //规则
        final List<RdeModelAntiFraudRuleRecord> rmafrrs = rmafrrm.selectList(
                Wrappers.lambdaQuery(RdeModelAntiFraudRuleRecord.class)
                        .eq(RdeModelAntiFraudRuleRecord::getModelId,rmaf.getId())
        );
        if (CollectionUtil.isEmpty(rmafrrs)){ return objectMapper.writeValueAsString(rmaf); }
        //处理规则组和规则之间的关系
        final Map<Integer, List<RdeModelAntiFraudRuleRecord>> rmafrrMap = rmafrrs.stream()
                .collect(Collectors.groupingBy(RdeModelAntiFraudRuleRecord::getGroupId));
        rmafrgs.stream().forEach(g -> g.setRules(rmafrrMap.get(g.getId())));

        //规则详情
        final List<RdeModelDecisionCodeLevel> rmdcls = rmdclm.selectList(
                Wrappers.lambdaQuery(RdeModelDecisionCodeLevel.class)
                        .in(
                                RdeModelDecisionCodeLevel::getId,
                                rmafrrs.stream().map(RdeModelAntiFraudRuleRecord::getCodeId).collect(Collectors.toList())
                        )
        );
        if (CollectionUtil.isEmpty(rmdcls)){ return objectMapper.writeValueAsString(rmaf); }
        //处理规则和规则详情之间的关系
        final Map<Integer, List<RdeModelDecisionCodeLevel>> rmdclMap = rmdcls.stream().collect(Collectors.groupingBy(RdeModelDecisionCodeLevel::getId));
        rmafrrMap.values().stream().flatMap(List::stream).forEach(r -> Optional.ofNullable(rmdclMap.get(r.getCodeId())).ifPresent(rd -> r.setRuleDetail(rd.get(0))));

        return objectMapper.writeValueAsString(rmaf);
    }

    @Override
    public int exportRuleToExcel(Integer strategyId, Path excel) {
        //获取规则
        final RdeModelAntiFraud rmaf = rmafm.selectOne(
                Wrappers.lambdaQuery(RdeModelAntiFraud.class).eq(RdeModelAntiFraud::getId, strategyId)
        );
        //获取规则组
        final List<RdeModelAntiFraudRuleGroup> rmafrgs = rmafrgm.selectList(
                Wrappers.lambdaQuery(RdeModelAntiFraudRuleGroup.class)
                        .eq(RdeModelAntiFraudRuleGroup::getModelId,rmaf.getId())
        );
        //规则
        final List<RdeModelAntiFraudRuleRecord> rmafrrs = rmafrrm.selectList(
                Wrappers.lambdaQuery(RdeModelAntiFraudRuleRecord.class)
                        .eq(RdeModelAntiFraudRuleRecord::getModelId,rmaf.getId())
        );
        //规则详情
        final List<RdeModelDecisionCodeLevel> rmdcls = rmdclm.selectList(
                Wrappers.lambdaQuery(RdeModelDecisionCodeLevel.class)
                        .in(
                                RdeModelDecisionCodeLevel::getId,
                                rmafrrs.stream().map(RdeModelAntiFraudRuleRecord::getCodeId).collect(Collectors.toList())
                        )
        );
        return writeExcel(excel, rmaf, rmafrgs, rmafrrs, rmdcls);
    }

    private int writeExcel(Path excel,RdeModelAntiFraud rmaf,List<RdeModelAntiFraudRuleGroup> rmafrgs,
                           List<RdeModelAntiFraudRuleRecord> rmafrrs,List<RdeModelDecisionCodeLevel> rmdcls
    ){
        int count = 0;

        final ExcelWriter writer = EasyExcel.write(excel.toFile()).build();
        try{
            //导出规则
            if (rmaf!=null){
                final WriteSheet sheet1 = EasyExcel.writerSheet(0, "RdeModelAntiFraud").head(RdeModelAntiFraud.class).build();
                writer.write(Collections.singletonList(rmaf),sheet1);
                count++;
            }
            //导出规则组
            if (CollectionUtil.isNotEmpty(rmafrgs)){
                final WriteSheet sheet2 = EasyExcel.writerSheet(1, "RdeModelAntiFraudRuleGroup").head(RdeModelAntiFraudRuleGroup.class).build();
                writer.write(rmafrgs,sheet2);
                count += rmafrgs.size();
            }
            //导出规则
            if (CollectionUtil.isNotEmpty(rmafrrs)){
                final WriteSheet sheet3 = EasyExcel.writerSheet(2, "RdeModelAntiFraudRuleRecord").head(RdeModelAntiFraudRuleRecord.class).build();
                writer.write(rmafrrs,sheet3);
                count += rmafrrs.size();
            }
            //导出规则详情
            if (CollectionUtil.isNotEmpty(rmdcls)){
                final WriteSheet sheet4 = EasyExcel.writerSheet(3, "RdeModelDecisionCodeLevel").head(RdeModelDecisionCodeLevel.class).build();
                writer.write(rmdcls,sheet4);
                count += rmdcls.size();
            }
        }catch (Exception e){
            log.error("导出规则到excel失败",e);
            throw new ServiceException("excel导出失败："+e.getMessage());
        }finally {
            writer.finish();
        }
        return count;
    }

    private RdeModelAntiFraud oldToNew(RdeModelAntiFraud rdeModelAntiFraud,
                                       String version,
                                       String deptFlag,
                                       ImportRuleWithJsonDTO params,
                                       LoginUser loginUser){
        rdeModelAntiFraud.setId(null);
        rdeModelAntiFraud.setVersionControl(version);
        rdeModelAntiFraud.setCreateUserId(loginUser.getUserid().intValue());
        rdeModelAntiFraud.setDeptId(loginUser.getSysUser().getDeptId().intValue());
        rdeModelAntiFraud.setDeptFlag(String.valueOf(deptFlag));
        rdeModelAntiFraud.setProjectCode(params.getProductId().toString());//产品
        rdeModelAntiFraud.setBusinessCode(params.getBusinessId().toString());//业务场景
        rdeModelAntiFraud.setRuleCode(params.getModelType());//模型类型
        return rdeModelAntiFraud;
    }
    private RdeModelAntiFraudRuleGroup oldToNew(RdeModelAntiFraudRuleGroup g,
                                                String version,
                                                String deptFlag,
                                                ImportRuleWithJsonDTO params,
                                                LoginUser loginUser,
                                                Integer modelId){
        g.setId(null);
        g.setVersionControl(version);
        g.setModelId(modelId);
        g.setCreateUserId(loginUser.getUserid().intValue());
        g.setDeptId(loginUser.getSysUser().getDeptId().intValue());
        g.setDeptFlag(String.valueOf(deptFlag));
        g.setProjectCode(params.getProductId().toString());//产品
        g.setBusinessCode(params.getBusinessId().toString());//业务场景
        g.setRuleCode(params.getModelType());//模型类型
        return g;
    }
    private RdeModelDecisionCodeLevel oldToNew(RdeModelDecisionCodeLevel d,
                                               String version,
                                               String deptFlag,
                                               ImportRuleWithJsonDTO params,
                                               LoginUser loginUser){
        d.setId(null);
        d.setDeptId(loginUser.getSysUser().getDeptId().intValue());
        d.setDeptFlag(String.valueOf(deptFlag));
        d.setVersionControl(version);
        d.setProjectCode(params.getProductId().toString());//产品
        d.setBusinessCode(params.getBusinessId().toString());//业务场景
        d.setRuleCode(params.getModelType());//模型类型
        return d;
    }
    private RdeModelAntiFraudRuleRecord oldToNew(RdeModelAntiFraudRuleRecord r,
                                                 String version,
                                                 String deptFlag,
                                                 ImportRuleWithJsonDTO params,
                                                 LoginUser loginUser,
                                                 Integer modelId,
                                                 Integer groupId){
        r.setId(null);
        r.setDeptId(loginUser.getSysUser().getDeptId().intValue());
        r.setDeptFlag(String.valueOf(deptFlag));
        r.setModelId(modelId);
        r.setGroupId(groupId);
        r.setCodeId(null);
        Optional.ofNullable(r.getRuleDetail()).ifPresent(d -> r.setCodeId(d.getId()));
        r.setVersionControl(version);
        r.setProjectCode(params.getProductId().toString());//产品
        r.setBusinessCode(params.getBusinessId().toString());//业务场景
        r.setRuleCode(params.getModelType());//模型类型
        return r;
    }
    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public int importRuleByJson(ImportRuleWithJsonDTO params, LoginUser loginUser) {
        //读取文件
        final String json ;
        try { json = readJson(params.getFile()); }catch (ServiceException e){ return -1; }
        if (!JSONUtil.isJsonObj(json)){ return -2; }
        //反序列化
        final RdeModelAntiFraud rdeModelAntiFraud;
        try {
            rdeModelAntiFraud = objectMapper.readValue(json, RdeModelAntiFraud.class);
        } catch (JsonProcessingException e) {
            log.error("JSON反序列化失败",e);
            return -3;
        }
        //导入策略
        final int deptFlag = ruoYiService.getStandardDepts().contains(loginUser.getSysUser().getDeptId()) ? 1 : 2;
        final String version = initVersion(params.getProductId(), params.getBusinessId());
        if (rdeModelAntiFraud==null){ return -4; }
        rmafm.insert(oldToNew(rdeModelAntiFraud,version,String.valueOf(deptFlag),params,loginUser));
        //导入规则组
        List<RdeModelAntiFraudRuleGroup> groups = rdeModelAntiFraud.getGroups();
        if (CollectionUtil.isEmpty(groups)){ return 0; }
        groups.stream().forEach(
                g -> oldToNew(g,version,String.valueOf(deptFlag),params,loginUser,rdeModelAntiFraud.getId())
        );
        rmafrgm.insertBatch(groups);
        //导入规则详情
        final List<RdeModelDecisionCodeLevel> ruleDetails = handler(groups,loginUser);
        ruleDetails.stream().forEach(d -> oldToNew(d,version,String.valueOf(deptFlag),params,loginUser));
        rmdclm.insertBatch(ruleDetails);
        //导入规则
        final List<RdeModelAntiFraudRuleRecord> rules = groups.stream()
            .filter(g -> CollectionUtil.isNotEmpty(g.getRules()))
            .peek(
                g -> g.getRules().stream().forEach(
                    r -> oldToNew(r,version,String.valueOf(deptFlag),params,loginUser,rdeModelAntiFraud.getId(),g.getId())
                )
            )
            .map(RdeModelAntiFraudRuleGroup::getRules)
            .flatMap(List::stream)
            .collect(Collectors.toList());
        rmafrrm.insertBatch(rules);
        //插入版本表
        final ModelVersionClassification mvc = ModelVersionClassificationConverter.convert(rdeModelAntiFraud);
        mvcm.insert(mvc);
        return rules.size();
    }
    private String readJson(MultipartFile file){
        final String json;
        try {
            json = new String(file.getBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("文件读取失败",e);
            throw new ServiceException("文件读取失败："+e.getMessage());
        }
        return json;
    }
    private String initVersion(Integer productId,Long businessId){
        if (productId==null||businessId==null){ return null; }
        final Product product = productMapper.selectById(productId);
        final Business business = businessMapper.selectById(businessId);
        if (product==null||business==null){ return null; }
        return StringUtil.getVersionNum(null,product.getName(),business.getName(),"C");
    }
    private List<RdeModelDecisionCodeLevel> handler(List<RdeModelAntiFraudRuleGroup> groups,LoginUser loginUser){
        final List<RdeModelDecisionCodeLevel> ruleDetails = new ArrayList<>();
        for (RdeModelAntiFraudRuleGroup g : groups) {
            final List<RdeModelAntiFraudRuleRecord> rules = g.getRules();
            if (CollectionUtil.isEmpty(rules)){ continue; }
            for (RdeModelAntiFraudRuleRecord r : rules) {
                Optional.ofNullable(r.getRuleDetail()).ifPresent(ruleDetails::add);
            }
        }
        //调用数据中台进行接口替换
        final Map<String,Map<String,Map<String,String>>> interfaceMap = new HashMap<>();
        ruleDetails.forEach(d -> {
            d.setDataModule(handleDataModule(d,d.getDataModule(),interfaceMap,loginUser));
            d.setConditions(handleConditions(d,d.getConditions(),interfaceMap,loginUser));
        });
        return ruleDetails;
    }

    private String handleDataModule(RdeModelDecisionCodeLevel d,String dataModule,Map<String,Map<String,Map<String,String>>> interfaceMap,LoginUser loginUser){
        if (!JSONUtil.isJsonArray(dataModule)){ return dataModule; }
        final JsonNode jsonNode;
        try {
            jsonNode = objectMapper.readTree(dataModule);
        } catch (JsonProcessingException e) {
            log.error("反序列化异常",e);
            throw new ServiceException(d.getCode()+".dataModule错误:"+"反序列化异常");
        }
        for (JsonNode node : jsonNode) {
            final String interfaceNo = node.get("interfaceNo").asText();
            Map<String, Map<String, String>> iMap = interfaceMap.get(interfaceNo);
            if (iMap==null){ iMap = addInterfaceInfo(interfaceMap,interfaceNo,loginUser); }
            if (iMap==null){ throw new ServiceException(d.getCode()+".dataModule错误:"+"未匹配到接口-"+interfaceNo); }
            if (!(node instanceof ObjectNode)){
                throw new ServiceException(d.getCode()+".dataModule错误:"+"node不是ObjectNode的实例");
            }
            final ObjectNode objectNode = (ObjectNode) node;
            objectNode.put("manageNo",iMap.get("INFO").get("interfaceManageNo"));
            objectNode.put("sourceNo",iMap.get("INFO").get("interfaceSourceNo"));
        }
        try {
            return objectMapper.writeValueAsString(jsonNode);
        } catch (JsonProcessingException e) {
            log.error("序列化异常",e);
            throw new ServiceException(d.getCode()+".dataModule错误:"+"序列化异常");
        }
    }
    private String handleConditions(RdeModelDecisionCodeLevel d,String conditions,Map<String,Map<String,Map<String,String>>> interfaceMap,LoginUser loginUser){
        if (!JSONUtil.isJsonArray(conditions)){ return conditions; }
        final JsonNode jsonNode;
        try {
            jsonNode = objectMapper.readTree(conditions);
        } catch (JsonProcessingException e) {
            log.error("反序列化异常",e);
            throw new ServiceException(d.getCode()+".conditions错误:"+"反序列化异常");
        }
        for (JsonNode node : jsonNode) {
            final JsonNode condition = node.path("condition");
            if (!(condition instanceof ArrayNode)){
                throw new ServiceException(d.getCode()+".conditions错误:"+"node不是ArrayNode的实例");
            }
            for (JsonNode cNode : condition) {
                if (!(cNode instanceof ObjectNode)){ throw new ServiceException(d.getCode()+".conditions错误:"+"cNode不是ObjectNode的实例"); }
                final JsonNode objectLevel = cNode.path("objectLevel");
                final JsonNode selectObj = cNode.path("selectObj");

                final String interfaceNo = selectObj.path("interfaceNo").asText();
                final String stats = selectObj.path("stats").asText();

                Map<String, Map<String, String>> iMap = interfaceMap.get(interfaceNo);
                if (iMap==null){ iMap = addInterfaceInfo(interfaceMap,interfaceNo,loginUser); }
                if (iMap==null){ throw new ServiceException(d.getCode()+".conditions错误:"+"未匹配到接口-"+interfaceNo); }
                final Map<String, String> interfaceInfo = iMap.get("INFO");
                if (!(selectObj instanceof ObjectNode)){ throw new ServiceException(d.getCode()+".conditions错误:"+"selectObj不是ObjectNode的实例"); }
                final ObjectNode selectObjON = (ObjectNode) selectObj;
                selectObjON.put("sourceNo",interfaceInfo.get("interfaceSourceNo"));
                selectObjON.put("manageNo",interfaceInfo.get("interfaceManageNo"));
                if (!(objectLevel instanceof ArrayNode)){ throw new ServiceException(d.getCode()+".conditions错误:"+"objectLevel不是ArrayNode的实例"); }

                final Map<String, String> statsMap = iMap.get(stats);
                if (statsMap==null){ throw new ServiceException(d.getCode()+".conditions错误:"+"未匹配到属性字段-"+stats); }
                final ArrayNode objectLevelAN = (ArrayNode) objectLevel;
                objectLevelAN.removeAll();
                objectLevelAN.insert(0,statsMap.get("l1"));
                objectLevelAN.insert(1,statsMap.get("l2"));
                objectLevelAN.insert(2,statsMap.get("l3"));
            }
        }

        try {
            return objectMapper.writeValueAsString(jsonNode);
        } catch (JsonProcessingException e) {
            log.error("序列化异常",e);
            throw new ServiceException(d.getCode()+".conditions错误:"+"序列化异常");
        }
    }
    private Map<String,Map<String,String>> addInterfaceInfo(Map<String,Map<String,Map<String,String>>> interfaceMap, String interfaceNo,LoginUser loginUser){
        final HttpResult<Map<String, Map<String, Map<String, String>>>> result =
                dataMiddleStationFeign.decisionQI(new InterfaceQueryDTO()
                        .setInterfaceNos(Collections.singletonList(interfaceNo))
                        .setDeptId(loginUser.getSysUser().getDeptId())
                );
        if (result.getCode()==200&& result.getData()!=null){
            interfaceMap.putAll(result.getData());
            return interfaceMap.get(interfaceNo);
        }
        return null;
    }
}
