package com.value.decision.model.decisionmanage.common;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.risksmart.common.core.web.AjaxResult;
import com.value.decision.model.decisionmanage.mapper.*;
import com.value.decision.model.decisionmanage.model.*;
import com.value.decision.model.decisionmanage.service.IDataCallingService;
import com.value.decision.process.mapper.NodeRequestDataMapper;
import com.value.decision.process.mapper.ProcessNodeResultMapper;
import com.value.decision.process.mapper.ProcessPolicyMapper;
import com.value.decision.process.model.*;
import com.value.decision.process.service.IProcessNodeService;
import com.value.decision.snapshot.domain.RdeModelAntiFraudRuleGroupSnapshot;
import com.value.decision.snapshot.domain.RdeModelAntiFraudSnapshot;
import com.value.decision.snapshot.mapper.RdeModelAntiFraudRuleGroupSnapshotMapper;
import com.value.decision.snapshot.mapper.RdeModelAntiFraudRuleRecordSnapshotMapper;
import com.value.decision.snapshot.mapper.RdeModelAntiFraudSnapshotMapper;
import com.value.decision.common.dto.EncryptDTO;
import com.value.decision.common.utils.EncryptBodyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description：任务方法
 * @author： andera
 * @create： 2023/8/16 20:12
 */
@Component
public class TxdRuleFunctionNew {

    @Autowired
    private ScoreCardRecordSnapshotMapper scoreCardRecordSnapshotMapper;

    @Autowired
    private ScorePrimaryIndexSnapshotMapper scorePrimaryIndexMapper;

    @Autowired
    private ScoreIndexRuleSnapshotMapper scoreIndexRuleMapper;

    @Autowired
    private PriceCardRecordSnapshotMapper priceCardRecordMapper;
    @Autowired
    private PriceCardRadiusSnapshotMapper priceCardRadiusMapper;
    @Autowired
    private QuotaCardRecordSnapshotMapper quotaCardRecordMapper;
    @Autowired
    private QuotaCardRadiusSnapshotMapper quotaCardRadiusMapper;
    @Autowired
    private RateCardRecordSnapshotMapper rateCardRecordMapper;
    @Autowired
    private RateCardRadiusSnapshotMapper rateCardRadiusMapper;
    @Autowired
    private ProcessNodeResultMapper processNodeResultMapper;
    @Autowired
    private RdeModelAntiFraudSnapshotMapper rdeModelAntiFraudMapper;

    @Autowired
    private RdeModelAntiFraudRuleGroupSnapshotMapper rdeModelAntiFraudRuleGroupMapper;

    @Autowired
    private RdeModelAntiFraudRuleRecordSnapshotMapper rdeModelAntiFraudRuleRecordMapper;

    @Value("${service.engine.ruleUrl}")
    private String ruleUrl;

    @Value("${service.engine.ruleflUrl}")
    private String ruleflUrl;

    @Value("${service.interfaceManage.interfaceRequestUrl}")
    private String interfaceRequestUrl;

    @Value("${service.interfaceManage.apiToken}")
    private String apiToken;

    @Value("${appkey}")
    private String appkey;

    @Value("${sercet}")
    private String secret;

    @Resource
    private NodeRequestDataMapper nodeRequestDataMapper;
    @Resource
    private SysRunLogMapper sysRunLogMapper;
    @Resource
    private ProcessPolicyMapper processPolicyMapper;

    public AjaxResult startRule(ProcessNode node, ProcessPolicyTask processPolicyTask) throws UnsupportedEncodingException {

        AjaxResult ajaxResult = new AjaxResult();
        //模块id  1:评分 2:评级 3:额度 4:定价 5:规则 6:分类
        switch (node.getModuleId()) {
            case 1:
                //根据基本信息获取请求决策信息
                try {
                    getRequestData(node, processPolicyTask);
                } catch (Exception e) {
                    e.printStackTrace();
                    processPolicyTask.setRequestData(null);
                    //异常推送飞书消息
//                    CommonUtil.sendBotMessage(""+e);
                }
                try {
                    ajaxResult = scoreTask(processPolicyTask, node);
                } catch (Exception e) {
                    e.printStackTrace();
                    ajaxResult = AjaxResult.success(new JSONObject());
                    //异常推送飞书消息
//                    CommonUtil.sendBotMessage("决策命中接口调用失败，返回数据格式有误,请管理员查看！");
                }
                break;
            case 2:
                ajaxResult = rateTask(processPolicyTask, node);
                break;
            case 3:
                ajaxResult = quotaTask(processPolicyTask, node);
                break;
            case 4:
                ajaxResult = priceTask(processPolicyTask, node);
                break;
            case 5:
                //根据基本信息获取请求决策信息
                try {
                    getRequestData(node, processPolicyTask);
                } catch (Exception e) {
                    e.printStackTrace();
                    processPolicyTask.setRequestData(null);
                    //异常推送飞书消息
//                    CommonUtil.sendBotMessage(""+e);
                }
                try {
                    ajaxResult = ruleTask(processPolicyTask, node);
                } catch (Exception e) {
                    e.printStackTrace();
                    ajaxResult = AjaxResult.success(new JSONObject());
                    //异常推送飞书消息
//                    CommonUtil.sendBotMessage("决策命中接口调用失败，返回数据格式有误,请管理员查看！");
                }
                break;
            case 6:
                //根据基本信息获取请求决策信息
                try {
                    getRequestData(node, processPolicyTask);
                } catch (Exception e) {
                    e.printStackTrace();
                    processPolicyTask.setRequestData(null);
                    //异常推送飞书消息
//                    CommonUtil.sendBotMessage(""+e);
                }
                try {
                    ajaxResult = classificationTask(processPolicyTask, node);
                } catch (Exception e) {
                    e.printStackTrace();
                    ajaxResult = AjaxResult.success(new JSONObject());
                    //异常推送飞书消息
//                    CommonUtil.sendBotMessage("决策命中接口调用失败，返回数据格式有误,请管理员查看！");
                }
                break;
        }
        ;
        return ajaxResult;
    }



    /**
     * 获取决策接口请求参数
     *
     * @param processNode
     * @param processPolicyTask
     */
    private void getRequestData(ProcessNode processNode, ProcessPolicyTask processPolicyTask) {
        switch (processNode.getModuleId()) {
            case 1:
                List<Map<String, Object>> mapList = new ArrayList<>();
                List<ScoreIndexRuleSnapshot> scoreIndexRuleSnapshots = scoreIndexRuleMapper.selectList(new LambdaQueryWrapper<ScoreIndexRuleSnapshot>()
                        .eq(ScoreIndexRuleSnapshot::getScordCardId, processNode.getRuleCode())
                        .eq(ScoreIndexRuleSnapshot::getButtonState, 1)
                        .eq(ScoreIndexRuleSnapshot::getDataState,0));
                scoreIndexRuleSnapshots.forEach(map -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    try {
                        if (StrUtil.isNotBlank(map.getDataModule())) {
                            List<Map<String, Object>> list = objectMapper.readValue(map.getDataModule(), new TypeReference<List<Map<String, Object>>>() {
                            });
                            mapList.addAll(list);
                        }
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                });
                List<Map<String, Object>> collect = mapList.stream().distinct().collect(Collectors.toList());
                getRequestDataByInterface(collect,processPolicyTask);
                break;
            case 5:
            case 6:
                List<Map<String, Object>> mapList2 = new ArrayList<>();
                List<RdeModelAntiFraudRuleGroupSnapshot> rdeModelAntiFraudRuleGroups = rdeModelAntiFraudRuleGroupMapper.selectList(new LambdaQueryWrapper<RdeModelAntiFraudRuleGroupSnapshot>()
                        .eq(RdeModelAntiFraudRuleGroupSnapshot::getModelId, processNode.getRuleCode())
                        .eq(RdeModelAntiFraudRuleGroupSnapshot::getStatus, 1)
                        .eq(RdeModelAntiFraudRuleGroupSnapshot::getDataStatus,0));
                rdeModelAntiFraudRuleGroups.forEach(map -> {
                    if ("1".equals(map.getStatus())) {
                        rdeModelAntiFraudRuleRecordMapper.newList2(map.getId()).forEach(map2 -> {
                            if ("1".equals(map2.getStatus())) {
                                ObjectMapper objectMapper = new ObjectMapper();
                                try {
                                    if (StrUtil.isNotBlank(map2.getDataModule())) {
                                        List<Map<String, Object>> list = objectMapper.readValue(map2.getDataModule(), new TypeReference<List<Map<String, Object>>>() {
                                        });
                                        mapList2.addAll(list);
                                    }
                                } catch (JsonProcessingException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });

                List<Map<String, Object>> collect2 = mapList2.stream().distinct().collect(Collectors.toList());
                getRequestDataByInterface(collect2,processPolicyTask);
                break;
        }

    }

    @Autowired
    private IDataCallingService dataCallingService;

    @Value("${service.interfaceManage.interfaceDetailUrl}")
    private String interfaceDetailUrl;

    public void saveDataCelling(ProcessPolicyTask processPolicyTask, String manageNo, String sourceNo, String callStatus,String responseBody){
        //存储数据至 数据调用表
        DataCalling dataCalling = new DataCalling();
        dataCalling.setCreateTime(LocalDateTime.now());
        dataCalling.setOrderNo(processPolicyTask.getOrderNo());
        dataCalling.setManageNo(manageNo);
        dataCalling.setCallStatus(callStatus);
        dataCalling.setModelApplication(1);
        dataCalling.setResponseBody(responseBody);
        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("manageNo",manageNo);
        hashMap.put("sourceNo",sourceNo);
        //接口类型
        String detail = HttpUtil.post(interfaceDetailUrl, JSON.toJSONString(hashMap));
        if (JSONUtil.isJson(detail) ) {
            cn.hutool.json.JSONObject jsonObject = JSONUtil.parseObj(detail);
            if(jsonObject.containsKey("data") & jsonObject.getJSONObject("data").containsKey("interfaceTag")){
                dataCalling.setInterfaceType(jsonObject.getJSONObject("data").getStr("interfaceTag"));
            }
        }
        dataCallingService.save(dataCalling);
    }

    //通过manage获取接口入参
    private List<Map<String,Object>> getEnteredGinsengByManageNo(String manageNo){
        String result = null;
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("manageNo", manageNo);
        result = HttpUtil.post(interfaceRequestUrl+"/listInputParameter", JSON.toJSONString(hashMap));
        if (JSONUtil.isJson(result)) {
            JSONObject data = JSONObject.parseObject(result);
            JSONArray jsonArray = data.getJSONArray("data");
            if (jsonArray != null && jsonArray.size()!=0) {
                List<Map<String, Object>> list = new ArrayList<>();
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Map<String, Object> map = new HashMap<>();
                    for (String key : jsonObject.keySet()) {
                        Object value = jsonObject.get(key);
                        map.put(key, value);
                    }
                    list.add(map);
                }
                return list;
            }
        }
        return null;
    }

    //通过数据中台获取决策请求数据
    private void getRequestDataByInterface(List<Map<String, Object>> collect,ProcessPolicyTask processPolicyTask){
        //请求参数
        JSONObject resultMap2 = new JSONObject();
        JSONObject hashMapRule = new JSONObject();

        collect.forEach(map -> {
            HashMap<String, Object> hashMap = new HashMap<>();
            //改造动态获取入参
//            List<Map<String, Object>> manageNoList = getEnteredGinsengByManageNo(map.get("manageNo").toString());
//            if (manageNoList!=null) {
//                for (Map<String, Object> stringObjectMap : manageNoList) {
//                    if (processPolicyTask.getMap()!=null && processPolicyTask.getMap().get(stringObjectMap.get("interfaceFieldIdName").toString())!= null) {
//                        hashMap.put(stringObjectMap.get("interfaceFieldIdName").toString(), processPolicyTask.getMap().get(stringObjectMap.get("interfaceFieldIdName").toString()));
//                    }
//                }
//            }

            hashMap.put("ckey", processPolicyTask.getCkey());
            hashMap.put("companyName", processPolicyTask.getEnterpriseName());
            hashMap.put("number", processPolicyTask.getIdNumber());
            hashMap.put("mobile", processPolicyTask.getMobilePhone());
            hashMap.put("name", processPolicyTask.getPersonalName());
            hashMap.put("orderNo",processPolicyTask.getOrderNo());
            hashMap.put("systemFlag", processPolicyTask.getSystemFlag());
            map.put("apiToken", apiToken);
            map.put("paramData", hashMap);
            String result = null;
            EncryptDTO encryptBody = null;
            try {
                encryptBody = EncryptBodyUtil.createEncryptBody(appkey, secret, map.get("manageNo").toString()
                        , map.get("sourceNo").toString(), map.get("interfaceNo").toString(), processPolicyTask.getOrderNo(), hashMap);

                result = HttpUtil.post(interfaceRequestUrl, JSON.toJSONString(encryptBody));
                String status = "未查得";
                JSONObject jsonObject = JSONObject.parseObject(result);
                if (jsonObject != null && jsonObject.get("data") != null && !jsonObject.isEmpty()) {
                    String code = jsonObject.getString("code");
                    if ("200".equals(code) ||  "00".equals(code)) {
                        status = "查得";
                    }
                }
                saveDataCelling(processPolicyTask,map.get("manageNo").toString(),map.get("sourceNo").toString(),status,result);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("数据中台接口调用失败，返回数据格式有误。请求参数："+ JSON.toJSONString(encryptBody)+",返还参数："+result+";"+e.getMessage());
            }

            JSONObject jsonObject2;
            if (JSONUtil.isJson(result) && "200".equals(JSONObject.parseObject(result).getString("code"))) {
                jsonObject2 = JSONObject.parseObject(result);
            }else {
                throw new RuntimeException("数据中台接口调用失败，返回数据格式有误。请求参数："+ JSON.toJSONString(encryptBody)+",返还参数："+result);
            }

            if ("0".equals(map.get("objectFlag"))) {
                resultMap2.put(map.get("objectName").toString(), jsonObject2.get("data"));
            } else if ("5".equals(map.get("objectFlag"))) {
                hashMapRule.put(map.get("objectName").toString(),jsonObject2.get("data"));
                resultMap2.put("companyModules", hashMapRule);
            }
        });
        resultMap2.put("companyModules", hashMapRule);
        processPolicyTask.setRequestData(resultMap2);
    }

    private AjaxResult classificationTask(ProcessPolicyTask processPolicyTask, ProcessNode node) throws UnsupportedEncodingException {
        //1.根据rulecode获取规则卡 2.获取规则卡下 所有规则组  3。获取规则组下所有规则以及对象标识集合 4.请求数据接口
        RdeModelAntiFraudSnapshot rdeModelAntiFraud = rdeModelAntiFraudMapper.selectById(node.getRuleCode());
        if (rdeModelAntiFraud == null) {
            return AjaxResult.error("分类不存在");
        }

        if (rdeModelAntiFraud.getBusinessCode().equals("0")) {
            return AjaxResult.error("分类策略未开启");
        }
        List<RdeModelAntiFraudRuleGroupSnapshot> rdeModelAntiFraudRuleGroups = rdeModelAntiFraudRuleGroupMapper.selectList(new LambdaQueryWrapper<RdeModelAntiFraudRuleGroupSnapshot>().eq(RdeModelAntiFraudRuleGroupSnapshot::getModelId, rdeModelAntiFraud.getId()));
        ArrayList<String> resultList = new ArrayList<>();
        rdeModelAntiFraudRuleGroups.forEach(map -> {
            if (map.getStatus().equals("1")) {
                rdeModelAntiFraudRuleRecordMapper.newList2(map.getId()).forEach(map2 -> {
                    if (map2.getStatus().equals("1")) {
                        resultList.add(map2.getTermRule().replaceAll("&gt;",">").replaceAll("&lt;","<"));
                    }
                });
            }
        });
        //特殊处理 对规则进行排序
        //车险缴费
        List<String> cxjfList = new ArrayList<>();
        //停车场
        List<String> tccList = new ArrayList<>();
        //高速公路
        List<String> gsglList = new ArrayList<>();
        //新能源充电
        List<String> xnycdList = new ArrayList<>();
        //其他行业画像
        List<String> qthyList = new ArrayList<>();
        //个人风险画像
        List<String> grfxList = new ArrayList<>();
        //修车记录
        List<String> xcList = new ArrayList<>();
        //汽贸行业
        List<String> qmList = new ArrayList<>();
        //信贷风险
        List<String> xdList = new ArrayList<>();
        for (String result : resultList) {
            if (result.contains("车险缴费")) {
                cxjfList.add(result);
            }
            if (result.contains("停车场")) {
                tccList.add(result);
            }
            if (result.contains("高速公路")) {
                gsglList.add(result);
            }
            if (result.contains("新能源充电")) {
                xnycdList.add(result);
            }
            if (result.contains("其他行业画像")) {
                qthyList.add(result);
            }
            if (result.contains("个人风险画像")) {
                grfxList.add(result);
            }
            if (result.contains("修车记录")) {
                xcList.add(result);
            }
            if (result.contains("汽贸行业")) {
                qmList.add(result);
            }
            if (result.contains("信贷风险")) {
                xdList.add(result);
            }
        }

        ArrayList<String> resultList2 = new ArrayList<>();
        resultList2.addAll(cxjfList);
        resultList2.addAll(tccList);
        resultList2.addAll(gsglList);
        resultList2.addAll(xnycdList);
        resultList2.addAll(qthyList);
        resultList2.addAll(grfxList);
        resultList2.addAll(xcList);
        resultList2.addAll(qmList);
        resultList2.addAll(xdList);

        JSONObject ruleCode = getRuleCode(processPolicyTask, resultList2, node);
        //命中code集合
        JSONObject objectJSONObject = ruleCode.getJSONObject("data");

        JSONObject codeList = objectJSONObject.getJSONObject("jsonObject");
        if (codeList == null || codeList.size() == 0) {
            return AjaxResult.success("规则未命中");
        }
        return AjaxResult.success(codeList);
    }


    private AjaxResult priceTask(ProcessPolicyTask processPolicyTask, ProcessNode node) {
        PriceCardRecordSnapshot priceCardRecord = priceCardRecordMapper.selectOne(new LambdaQueryWrapper<PriceCardRecordSnapshot>().eq(PriceCardRecordSnapshot::getId, node.getRuleCode()));
        if (priceCardRecord == null) {
            return AjaxResult.error("定价不存在");
        }
        if (priceCardRecord.getButtonState() == 0) {
            return AjaxResult.error("定价未开启");
        }
        //获取评级结果
        ProcessNodeResult processNodeResult = processNodeResultMapper.selectOne(new LambdaQueryWrapper<ProcessNodeResult>().eq(ProcessNodeResult::getProcessId, node.getProcessStrategyId())
                .eq(ProcessNodeResult::getRuleCode, priceCardRecord.getRateCardId())
                .eq(ProcessNodeResult::getModuleId, 2)
                .eq(ProcessNodeResult::getTaskId,processPolicyTask.getId()));
        String detail = processNodeResult.getDetail();
        if(StrUtil.isBlank(detail)){
            return  AjaxResult.error("评分未命中");
        }
        List<PriceCardRadiusSnapshot> priceCardRadii = priceCardRadiusMapper.selectList(new LambdaQueryWrapper<PriceCardRadiusSnapshot>().eq(PriceCardRadiusSnapshot::getPriceCardId, priceCardRecord.getId()));
        for (PriceCardRadiusSnapshot radius : priceCardRadii) {
            if (radius.getStandardName().equals(detail)) {
                return AjaxResult.success("操作成功", radius.getPriceRange()!=null?new BigDecimal(radius.getPriceRange()).divide(BigDecimal.valueOf(100)):null);
            }
        }
        return AjaxResult.error("定价不在范围");
    }

    @Resource
    private QuotaEnterpriseMapper quotaEnterpriseMapper;

    private AjaxResult quotaTask(ProcessPolicyTask processPolicyTask, ProcessNode node) {
        QuotaCardRecordSnapshot quotaCardRecord = quotaCardRecordMapper.selectOne(new LambdaQueryWrapper<QuotaCardRecordSnapshot>().eq(QuotaCardRecordSnapshot::getId, node.getRuleCode()));
        if (quotaCardRecord == null) {
            return AjaxResult.error("额度不存在");
        }
        if (quotaCardRecord.getButtonState() == 0) {
            return AjaxResult.error("额度未开启");
        }

        //获取评级结果
        ProcessNodeResult processNodeResult = processNodeResultMapper.selectOne(new LambdaQueryWrapper<ProcessNodeResult>().eq(ProcessNodeResult::getProcessId, node.getProcessStrategyId())
                .eq(ProcessNodeResult::getRuleCode, quotaCardRecord.getRateCardId())
                .eq(ProcessNodeResult::getModuleId, 2)
                .eq(ProcessNodeResult::getTaskId,processPolicyTask.getId()));
        String detail = processNodeResult.getDetail();
        if(StrUtil.isBlank(detail)){
            return  AjaxResult.error("评级未命中");
        }
        //特殊处理  个人标识0 企业标识 1 当flag为企业标识时 计算逻辑改变
        if (processPolicyTask.getFlag().equals("1")) {
            double enterpriseScore = Double.parseDouble(processPolicyTask.getEnterpriseScore());
            if(enterpriseScore<=700){
                QuotaEnterprise quotaEnterprise = quotaEnterpriseMapper.selectOne(new LambdaQueryWrapper<QuotaEnterprise>()
                        .eq(QuotaEnterprise::getRate, detail));
                if (quotaEnterprise ==null) {
                    return AjaxResult.error("额度不在范围");
                }
                return AjaxResult.success("操作成功",BigDecimal.valueOf( quotaEnterprise.getUnder700()).multiply(new BigDecimal(quotaEnterprise.getBenchmarkLimit())));
            }else if(enterpriseScore > 700 && enterpriseScore<=750){
                QuotaEnterprise quotaEnterprise = quotaEnterpriseMapper.selectOne(new LambdaQueryWrapper<QuotaEnterprise>()
                        .eq(QuotaEnterprise::getRate, detail));
                if (quotaEnterprise ==null) {
                    return AjaxResult.error("额度不在范围");
                }
                return AjaxResult.success("操作成功",BigDecimal.valueOf( quotaEnterprise.getBetween700750()).multiply(new BigDecimal(quotaEnterprise.getBenchmarkLimit())));
            }else if(enterpriseScore > 750 && enterpriseScore<=800){
                QuotaEnterprise quotaEnterprise = quotaEnterpriseMapper.selectOne(new LambdaQueryWrapper<QuotaEnterprise>()
                        .eq(QuotaEnterprise::getRate, detail));
                if (quotaEnterprise ==null) {
                    return AjaxResult.error("额度不在范围");
                }
                return AjaxResult.success("操作成功",BigDecimal.valueOf( quotaEnterprise.getBetween750800()).multiply(new BigDecimal(quotaEnterprise.getBenchmarkLimit())));
            }else if(enterpriseScore > 800 && enterpriseScore<=850){
                QuotaEnterprise quotaEnterprise = quotaEnterpriseMapper.selectOne(new LambdaQueryWrapper<QuotaEnterprise>()
                        .eq(QuotaEnterprise::getRate, detail));
                if (quotaEnterprise ==null) {
                    return AjaxResult.error("额度不在范围");
                }
                return AjaxResult.success("操作成功",BigDecimal.valueOf( quotaEnterprise.getBetween800850()).multiply(new BigDecimal(quotaEnterprise.getBenchmarkLimit())));
            }else if(enterpriseScore > 850 && enterpriseScore<=900){
                QuotaEnterprise quotaEnterprise = quotaEnterpriseMapper.selectOne(new LambdaQueryWrapper<QuotaEnterprise>()
                        .eq(QuotaEnterprise::getRate, detail));
                if (quotaEnterprise ==null) {
                    return AjaxResult.error("额度不在范围");
                }
                return AjaxResult.success("操作成功",BigDecimal.valueOf( quotaEnterprise.getBetween850900()).multiply(new BigDecimal(quotaEnterprise.getBenchmarkLimit())));
            }else if(enterpriseScore > 900 && enterpriseScore<=950){
                QuotaEnterprise quotaEnterprise = quotaEnterpriseMapper.selectOne(new LambdaQueryWrapper<QuotaEnterprise>()
                        .eq(QuotaEnterprise::getRate, detail));
                if (quotaEnterprise ==null) {
                    return AjaxResult.error("额度不在范围");
                }
                return AjaxResult.success("操作成功",BigDecimal.valueOf( quotaEnterprise.getBetween900950()).multiply(new BigDecimal(quotaEnterprise.getBenchmarkLimit())));
            }else if(enterpriseScore > 950 && enterpriseScore<=1000){
                QuotaEnterprise quotaEnterprise = quotaEnterpriseMapper.selectOne(new LambdaQueryWrapper<QuotaEnterprise>()
                        .eq(QuotaEnterprise::getRate, detail));
                if (quotaEnterprise ==null) {
                    return AjaxResult.error("额度不在范围");
                }
                return AjaxResult.success("操作成功",BigDecimal.valueOf( quotaEnterprise.getBetween9501000()).multiply(new BigDecimal(quotaEnterprise.getBenchmarkLimit())));
            } else if(enterpriseScore > 1000){
                QuotaEnterprise quotaEnterprise = quotaEnterpriseMapper.selectOne(new LambdaQueryWrapper<QuotaEnterprise>()
                        .eq(QuotaEnterprise::getRate, detail));
                if (quotaEnterprise ==null) {
                    return AjaxResult.error("额度不在范围");
                }
                return AjaxResult.success("操作成功",BigDecimal.valueOf( quotaEnterprise.getUp1000()).multiply(new BigDecimal(quotaEnterprise.getBenchmarkLimit())));
            }
        }

        //根据额度id 获取额度范围表
        List<QuotaCardRadiusSnapshot> quotaCardRadiusList = quotaCardRadiusMapper.selectList(new LambdaQueryWrapper<QuotaCardRadiusSnapshot>().eq(QuotaCardRadiusSnapshot::getQuotaCardId, quotaCardRecord.getId()));
        for (QuotaCardRadiusSnapshot quotaCardRadius : quotaCardRadiusList) {
            if (quotaCardRadius.getStandardName().equals(detail)) {
                BigDecimal bigDecimal = new BigDecimal(quotaCardRadius.getQuotaRange()).divide(BigDecimal.valueOf(100));
                bigDecimal = bigDecimal.multiply(new BigDecimal(50000)).add(new BigDecimal(50000));
                return AjaxResult.success("操作成功", bigDecimal.doubleValue());
            }
        }
        return AjaxResult.error("额度不在范围");
    }


    private AjaxResult rateTask(ProcessPolicyTask processPolicyTask, ProcessNode node) {
        RateCardRecordSnapshot rateCardRecord = rateCardRecordMapper.selectOne(new LambdaQueryWrapper<RateCardRecordSnapshot>().eq(RateCardRecordSnapshot::getId, node.getRuleCode()));
        if (rateCardRecord == null) {
            return AjaxResult.error("评级不存在");
        }
        if (rateCardRecord.getButtonState() == 0) {
            return AjaxResult.error("评级未开启");
        }
        //从评级卡中获取评分id 再根据节点结果表获取评分
        RateCardRecordSnapshot rateCardRecord1 = rateCardRecordMapper.selectOne(new LambdaQueryWrapper<RateCardRecordSnapshot>().eq(RateCardRecordSnapshot::getId, node.getRuleCode()));
        ProcessNodeResult processNodeResult = processNodeResultMapper.selectOne(new LambdaQueryWrapper<ProcessNodeResult>()
                .eq(ProcessNodeResult::getRuleCode, rateCardRecord1.getScoreCardId())
                .eq(ProcessNodeResult::getProcessId, node.getProcessStrategyId())
                .eq(ProcessNodeResult::getTaskId,processPolicyTask.getId()));
        //评分
        String detail = processNodeResult.getDetail();
        if(StrUtil.isBlank(detail)){
            return  AjaxResult.error("评分未命中");
        }
        BigDecimal score = new BigDecimal(detail);

        //根据评级卡id获取评级范围
        List<RateCardRadiusSnapshot> rateCardRadiusList = rateCardRadiusMapper.selectList(new LambdaQueryWrapper<RateCardRadiusSnapshot>().eq(RateCardRadiusSnapshot::getRateCardId, node.getRuleCode()));
        for (RateCardRadiusSnapshot rateCardRadius : rateCardRadiusList) {
            String[] split = rateCardRadius.getRateRange().split(",");
            if (score.compareTo(new BigDecimal(split[0])) > 0 && score.compareTo(new BigDecimal(split[1])) <= 0) {
                return AjaxResult.success("操作成功", rateCardRadius.getStandardRate());
            }
        }
        return AjaxResult.error("评级不在范围");
    }


    /**
     * @param
     * @return
     */
    public AjaxResult ruleTask(ProcessPolicyTask processPolicyTask, ProcessNode node) throws UnsupportedEncodingException {
        //1.根据rulecode获取规则卡 2.获取规则卡下 所有规则组  3。获取规则组下所有规则以及对象标识集合 4.请求数据接口
        RdeModelAntiFraudSnapshot rdeModelAntiFraud = rdeModelAntiFraudMapper.selectById(node.getRuleCode());
        if (rdeModelAntiFraud == null) {
            return AjaxResult.error("规则不存在");
        }

        if (rdeModelAntiFraud.getStatus().equals("0")) {
            return AjaxResult.error("规则策略未开启");
        }
        List<RdeModelAntiFraudRuleGroupSnapshot> rdeModelAntiFraudRuleGroups = rdeModelAntiFraudRuleGroupMapper.selectList(new LambdaQueryWrapper<RdeModelAntiFraudRuleGroupSnapshot>()
                .eq(RdeModelAntiFraudRuleGroupSnapshot::getModelId, rdeModelAntiFraud.getId())
                .eq(RdeModelAntiFraudRuleGroupSnapshot::getStatus,1)
                .eq(RdeModelAntiFraudRuleGroupSnapshot::getDataStatus,0));
        List<String> resultList = new ArrayList<>();
        rdeModelAntiFraudRuleGroups.forEach(map -> {
            rdeModelAntiFraudRuleRecordMapper.newList2(map.getId()).forEach(map2 -> {
                    resultList.add(map2.getTermRule().replaceAll("&gt;",">").replaceAll("&lt;","<"));
            });
        });
        JSONObject ruleCode = getRuleCode(processPolicyTask, resultList, node);
        JSONArray codeList = new JSONArray();
        List<Map<String, Object>> maps = new ArrayList<>();
        String stronglyRejectFlag = "0";
        if(ruleCode !=null && ruleCode.size() !=0){
            //命中code集合
            JSONObject objectJSONObject = ruleCode.getJSONObject("data");
            if(objectJSONObject !=null &&objectJSONObject.size() != 0 ){
                codeList = objectJSONObject.getJSONArray("codeList");
                if (codeList == null || codeList.size() == 0) {
                    return AjaxResult.success(new JSONObject());
                }
                //根据命中code获取规则详细
                for (int i = 0; i < codeList.size(); i++) {
                    Map<String, Object> decisionLevelDeatil = rdeModelAntiFraudRuleRecordMapper.getDecisionLevelDeatil(codeList.get(i).toString(), node.getRuleCode());
                    if (decisionLevelDeatil.get("stronglyReject")!=null) {
                        if ("1".equals(decisionLevelDeatil.get("stronglyReject").toString())) {
                            stronglyRejectFlag = "1";
                        }
                        //当准入并且 为转人工 access 提示 转人工
                        if ("0".equals(decisionLevelDeatil.get("stronglyReject").toString()) && decisionLevelDeatil.get("transferToPerson") != null && "1".equals(decisionLevelDeatil.get("transferToPerson").toString())) {
                            stronglyRejectFlag = "2";
                        }
                    }
                    maps.add(decisionLevelDeatil);
                }
            }
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("access",stronglyRejectFlag);
        jsonObject.put("decisionList",maps);
        return AjaxResult.success(jsonObject);
    }

    /**
     * @param
     * @return
     */
    public AjaxResult scoreTask(ProcessPolicyTask processPolicyTask, ProcessNode node) throws UnsupportedEncodingException {
        ScoreCardRecordSnapshot scoreCardRecord = scoreCardRecordSnapshotMapper.selectById(node.getRuleCode());
        if (scoreCardRecord == null) {
            return AjaxResult.error("评分卡不存在");
        }
        if (scoreCardRecord.getButtonState() == 0) {
            return AjaxResult.error("评分策略未开启");
        }

        BigDecimal score = new BigDecimal(0);
        //根据评分卡id获取所有规则 （开启、非默认指标）
        List<ScoreIndexRuleSnapshot> scoreIndexRules = scoreIndexRuleMapper.selectList(new LambdaQueryWrapper<ScoreIndexRuleSnapshot>()
                .eq(ScoreIndexRuleSnapshot::getScordCardId, node.getRuleCode())
                .eq(ScoreIndexRuleSnapshot::getButtonState, 1)
                .eq(ScoreIndexRuleSnapshot::getDefaultRule,0)
                .eq(ScoreIndexRuleSnapshot::getDataState,0));
        List<String> strList = new ArrayList<>();
        scoreIndexRules.forEach(map -> {
            strList.add(map.getTermRule().replaceAll("&gt;",">").replaceAll("&lt;","<"));
        });
        //调用接口 获取命中规则code
        JSONObject ruleCode = getRuleCode(processPolicyTask, strList, node);
        JSONArray codeList = new JSONArray();
        if(!JSONUtil.isNull(ruleCode)){
            //命中code集合
            JSONObject objectJSONObject = ruleCode.getJSONObject("data");
            if(!JSONUtil.isNull(objectJSONObject)){
                codeList = objectJSONObject.getJSONArray("codeList");
            }
        }

        List<Map<String, Object>> maps = new ArrayList<>();

        //获取所有规则的上级指标卡id
        List<Integer> primaryCardById = scoreIndexRuleMapper.getPrimaryCardById(node.getRuleCode());
        for (Integer primaryCardId : primaryCardById) {
            //根据指标卡获取规则 并判断规则是否命中
            List<ScoreIndexRuleSnapshot> scoreIndexRuleSnapshots = scoreIndexRuleMapper.selectList(new LambdaQueryWrapper<ScoreIndexRuleSnapshot>()
                    .eq(ScoreIndexRuleSnapshot::getScorePrimaryId, primaryCardId)
                    .eq(ScoreIndexRuleSnapshot::getScordCardId, node.getRuleCode())
                    .eq(ScoreIndexRuleSnapshot::getButtonState, 1)
                    .eq(ScoreIndexRuleSnapshot::getDefaultRule, 0)
                    .orderByAsc(ScoreIndexRuleSnapshot::getLevel));
            //标识 在该指标卡下的规则是否有命中的规则 若不存在命中的规则 取默认规则评分
            boolean flag = false;
            for (ScoreIndexRuleSnapshot scoreIndexRule : scoreIndexRuleSnapshots) {
                if (codeList.contains(scoreIndexRule.getIndexRule())) {
                    flag = true;
                    //判断是否独立生效  否 获取权重并乘评分
                    if (scoreIndexRule.getTakeEffect() == 1) {
                        score = score.add(BigDecimal.valueOf(scoreIndexRule.getScore()));
                    } else {
                        String[] split = scoreIndexRule.getScordPrimaryIds().split(";");
                        Double scoreNew = scoreIndexRule.getScore();
                        BigDecimal bigDecimal = new BigDecimal(scoreNew);
                        for (String s : split) {
                            ScorePrimaryIndexSnapshot scorePrimaryIndex = scorePrimaryIndexMapper.selectOne(new LambdaQueryWrapper<ScorePrimaryIndexSnapshot>()
                                    .eq(ScorePrimaryIndexSnapshot::getId, s)
                                    .eq(ScorePrimaryIndexSnapshot::getDataState,0)
                                    .eq(ScorePrimaryIndexSnapshot::getButtonState,1));
                            if (scorePrimaryIndex != null) {
                                bigDecimal = bigDecimal.multiply(BigDecimal.valueOf(scorePrimaryIndex.getWeight()).divide(BigDecimal.valueOf(100)));
                            }
                        }
                        score = score.add(bigDecimal);
                    }

                    //根据命中code获取规则详细
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("code",scoreIndexRule.getIndexRule());
                    hashMap.put("riskStatement",StrUtil.isNotBlank(scoreIndexRule.getDescription())?scoreIndexRule.getDescription().replaceAll("&gt;",">").replaceAll("&lt;","<"):null);
                    ScorePrimaryIndexSnapshot scorePrimaryIndexSnapshot = scorePrimaryIndexMapper.selectById(scoreIndexRule.getScorePrimaryId());
                    hashMap.put("groupName",scorePrimaryIndexSnapshot!=null?scorePrimaryIndexSnapshot.getPrimaryIndex():"-");
                    maps.add(hashMap);
                    break;
                }
            }
            if(!flag){
                //获取默认指标
                ScoreIndexRuleSnapshot scoreIndexRuleSnapshotsDefault = scoreIndexRuleMapper.selectOne(new LambdaQueryWrapper<ScoreIndexRuleSnapshot>()
                        .eq(ScoreIndexRuleSnapshot::getScorePrimaryId, primaryCardId)
                        .eq(ScoreIndexRuleSnapshot::getScordCardId, node.getRuleCode())
                        .eq(ScoreIndexRuleSnapshot::getButtonState, 1)
                        .eq(ScoreIndexRuleSnapshot::getDefaultRule, 1)
                        .eq(ScoreIndexRuleSnapshot::getDataState,0)
                        .orderByAsc(ScoreIndexRuleSnapshot::getLevel));
                if (scoreIndexRuleSnapshotsDefault !=null) {
                    //判断是否独立生效  否 获取权重并乘评分
                    if (scoreIndexRuleSnapshotsDefault.getTakeEffect() == 1) {
                        score = score.add(BigDecimal.valueOf(scoreIndexRuleSnapshotsDefault.getScore()));
                    }else {
                        String[] split = scoreIndexRuleSnapshotsDefault.getScordPrimaryIds().split(";");
                        Double scoreNew = scoreIndexRuleSnapshotsDefault.getScore();
                        BigDecimal bigDecimal = new BigDecimal(scoreNew);
                        for (String s : split) {
                            ScorePrimaryIndexSnapshot scorePrimaryIndex = scorePrimaryIndexMapper.selectOne(new LambdaQueryWrapper<ScorePrimaryIndexSnapshot>()
                                    .eq(ScorePrimaryIndexSnapshot::getId, s)
                                    .eq(ScorePrimaryIndexSnapshot::getDataState,0)
                                    .eq(ScorePrimaryIndexSnapshot::getButtonState,1));
                            if (scorePrimaryIndex != null) {
                                bigDecimal = bigDecimal.multiply(BigDecimal.valueOf(scorePrimaryIndex.getWeight()).divide(BigDecimal.valueOf(100)));
                            }
                        }
                        score = score.add(bigDecimal);
                    }
                    //根据命中code获取规则详细
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("code",scoreIndexRuleSnapshotsDefault.getIndexRule());
                    hashMap.put("riskStatement",StrUtil.isNotBlank(scoreIndexRuleSnapshotsDefault.getDescription())?scoreIndexRuleSnapshotsDefault.getDescription().replaceAll("&gt;",">").replaceAll("&lt;","<"):null);
                    ScorePrimaryIndexSnapshot scorePrimaryIndexSnapshot = scorePrimaryIndexMapper.selectById(scoreIndexRuleSnapshotsDefault.getScorePrimaryId());
                    hashMap.put("groupName",scorePrimaryIndexSnapshot!=null?scorePrimaryIndexSnapshot.getPrimaryIndex():"-");
                    maps.add(hashMap);
                }
            }
        }
        JSONObject resultJson =  new JSONObject();
        resultJson.put("score",score.setScale(2, RoundingMode.HALF_UP));
        resultJson.put("decisionList",maps);
        return AjaxResult.success(resultJson);
    }


    /**
     * 根据入参调用决策请求接口  获取命中规则
     *
     * @param processPolicyTask 任务信息
     * @param strList           规则集
     * @return
     */
    private JSONObject getRuleCode(ProcessPolicyTask processPolicyTask, List<String> strList, ProcessNode node) throws UnsupportedEncodingException {
        long startTime = System.currentTimeMillis();
        JSONObject dataJson = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("number", processPolicyTask.getTaskNumber());
        jsonObject.put("strList", strList);
        jsonObject.put("JsonObject", processPolicyTask.getRequestData());
        String encode = URLEncoder.encode(JSONObject.toJSONString(jsonObject, JSONWriter.Feature.WriteMapNullValue), "UTF-8");
        dataJson.put("data", encode);
        String data ="";
        try {
            if(node.getModuleId() == 6){
                data = HttpUtil.post(ruleflUrl, JSONObject.toJSONString(dataJson, JSONWriter.Feature.WriteMapNullValue));
            }else {
                data = HttpUtil.post(ruleUrl,JSONObject.toJSONString(dataJson, JSONWriter.Feature.WriteMapNullValue));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("决策命中接口调用失败。请求参数："+ dataJson+"\n 解密参数："+JSONObject.toJSONString(jsonObject, JSONWriter.Feature.WriteMapNullValue)+"\n 返还参数："+data+"\n 异常："+e);
        }

        JSONObject resultObject = new JSONObject();

        SysRunLog sysRunLog = new SysRunLog();
        sysRunLog.setResultCode("失败");
        if (JSONUtil.isJson(data)) {
            resultObject = JSONObject.parseObject(data);
            sysRunLog.setResultCode("成功");
        }else{
            throw new RuntimeException("决策命中接口调用失败，返回数据格式有误。请求参数："+ dataJson+"\n 解密参数："+JSONObject.toJSONString(jsonObject, JSONWriter.Feature.WriteMapNullValue)+"\n 返还参数："+data);
        }
        //请求数据保存至节点请求数据存储表
        NodeRequestData nodeRequestData = new NodeRequestData();
        nodeRequestData.setTaskNumber(processPolicyTask.getOrderNo());
        nodeRequestData.setRuleCode(node.getRuleCode());
        nodeRequestData.setNodeId(node.getNodeId());
        nodeRequestData.setRequestData(jsonObject.toString());
        nodeRequestData.setResultData(data);
        nodeRequestData.setCreateTime(LocalDateTime.now());
        nodeRequestData.setDeptId(processPolicyTask.getDeptId());
        nodeRequestData.setUserId(processPolicyTask.getUserId());
        nodeRequestDataMapper.insert(nodeRequestData);

        long endTime = System.currentTimeMillis();
        //运行日志记录
        ProcessPolicy policy = processPolicyMapper.selectById(processPolicyTask.getProcessStrategyId());
        sysRunLog.setApprovalUserId(processPolicyTask.getApplyUserId());
        sysRunLog.setApprovalUserName(processPolicyTask.getApplyUserName());
        sysRunLog.setDeptId(processPolicyTask.getDeptId());
        sysRunLog.setDeptName(processPolicyTask.getDeptName());
        sysRunLog.setProductName(policy!=null? policy.getProductName():null);
        sysRunLog.setBusinessCode(policy!=null? policy.getBusinessCode():null);
        sysRunLog.setRuleCode(node.getRuleCode());
        sysRunLog.setRuleName(node.getRuleName());
        sysRunLog.setCreateTime(LocalDateTime.now());
        sysRunLog.setResult(data);
        sysRunLog.setApiUrl(ruleUrl);
        sysRunLog.setRequestData(jsonObject.toJSONString());
        sysRunLog.setReturnTime((int) (endTime - startTime));
        sysRunLog.setModuleId(node.getModuleId());
        sysRunLogMapper.insert(sysRunLog);
        return resultObject;
    }


}
