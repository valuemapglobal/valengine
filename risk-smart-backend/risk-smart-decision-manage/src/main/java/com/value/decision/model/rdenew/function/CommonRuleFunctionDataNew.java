
package com.value.decision.model.rdenew.function;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.value.decision.common.domain.R;
import com.value.decision.common.dto.EncryptDTO;
import com.value.decision.common.utils.EncryptBodyUtil;
import com.value.decision.common.utils.StringUtils;
import com.risksmart.common.core.web.AjaxResult;
import com.value.decision.model.decisionmanage.constants.RuleConstants;
import com.value.decision.model.decisionmanage.mapper.*;
import com.value.decision.model.decisionmanage.model.DataCalling;
import com.value.decision.model.decisionmanage.model.ModelRegularData;
import com.value.decision.model.decisionmanage.model.ModelTaskRecord;
import com.value.decision.model.decisionmanage.model.ModelTestTask;
import com.value.decision.model.decisionmanage.model.QuotaCardRecordSnapshot;
import com.value.decision.model.decisionmanage.model.RateCardRecordSnapshot;
import com.value.decision.model.decisionmanage.model.RuleRecordReuseSnapshot;
import com.value.decision.model.decisionmanage.model.ScoreCardReuseSnapshot;
import com.value.decision.model.decisionmanage.model.ScoreIndexRule;
import com.value.decision.model.decisionmanage.model.ScoreIndexRuleSnapshot;
import com.value.decision.model.decisionmanage.model.SysRunLog;
import com.value.decision.model.decisionmanage.model.dto.model.ModelRuleDTO;
import com.value.decision.model.decisionmanage.model.dto.model.ModelTestTaskDataVO;
import com.value.decision.model.decisionmanage.model.dto.model.ModelTestTaskVO;
import com.value.decision.model.decisionmanage.model.dto.model.PolicyRequestDTO;
import com.value.decision.model.decisionmanage.service.IModelTaskRecordService;
import com.value.decision.model.decisionmanage.service.IModelTestTaskService;
import com.value.decision.model.decisionmanage.util.InterfaceFieldCache;
import com.value.decision.model.decisionmanage.util.ParameterMappingUtil;
import com.value.decision.process.service.feign.InterfaceUserPermissionsFeign;
import com.value.decision.process.model.ProcessNode;
import com.value.decision.process.model.ProcessPolicy;
import com.value.decision.process.vo.InterfaceUser;
import com.value.decision.model.rdenew.domain.RdeModelAntiFraudRuleGroup;
import com.value.decision.model.rdenew.mapper.RdeModelAntiFraudRuleGroupMapper;
import com.value.decision.model.rdenew.mapper.RdeModelAntiFraudRuleRecordMapper;
import com.value.decision.snapshot.domain.RdeModelAntiFraudRuleGroupSnapshot;
import com.value.decision.snapshot.mapper.RdeModelAntiFraudRuleGroupSnapshotMapper;
import com.value.decision.snapshot.mapper.RdeModelAntiFraudRuleRecordSnapshotMapper;
import com.value.decision.version.domain.RdeModelAntiFraudRuleGroupVersion;
import com.value.decision.version.domain.RdeModelAntiFraudRuleRecordVersion;
import com.value.decision.version.domain.RdeModelDecisionCodeLevelVersion;
import com.value.decision.version.domain.ScoreIndexRuleVersion;
import com.value.decision.version.mapper.RdeModelAntiFraudRuleGroupVersionMapper;
import com.value.decision.version.mapper.RdeModelAntiFraudRuleRecordVersionMapper;
import com.value.decision.version.mapper.RdeModelDecisionCodeLevelVersionMapper;
import com.value.decision.version.mapper.ScoreIndexRuleVersionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import com.value.decision.model.decisionmanage.model.dto.model.FieldsPortDTO;
import com.value.decision.model.decisionmanage.model.dto.model.FieldsDTO;
import jakarta.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description：任务方法
 * @author： Dianne
 * @create： 2024/10/25 10:12
 */
@Component
public class CommonRuleFunctionDataNew {
    private final Logger logger = LoggerFactory.getLogger(CommonRuleFunctionDataNew.class);

    @Value("${service.interfaceManage.fieldsUrl}")
    private String fieldsUrl;

    @Value("${service.engine.ruleUrl}")
    private String ruleUrl;

    @Value("${service.engine.ruleflUrl}")
    private String ruleflUrl;

    @Value("${service.interfaceManage.interfaceRequestUrl}")
    private String interfaceRequestUrl;

    @Value("${service.interfaceManage.queryAppkeyByUserIdUrl}")
    private String queryAppkeyByUserIdUrl;

    @Value("${service.interfaceManage.interfaceDetailUrl}")

    private String interfaceDetailUrl;

    @Resource
    private SysRunLogMapper sysRunLogMapper;

    @Autowired
    private IModelTestTaskService iModelTestTaskService;

    @Autowired
    private IModelTaskRecordService iModelTaskRecordService;

    @Autowired
    private ScoreIndexRuleMapper scoreIndexRuleMapper;

    @Autowired
    private ScoreIndexRuleSnapshotMapper scoreIndexRuleSnapshotMapper;

    @Autowired
    private ScoreIndexRuleVersionMapper scoreIndexRuleVersionMapper;

    @Autowired
    private RdeModelAntiFraudRuleGroupMapper rdeModelAntiFraudRuleGroupMapper;

    @Autowired
    private RdeModelAntiFraudRuleRecordMapper rdeModelAntiFraudRuleRecordMapper;

    @Autowired
    private RdeModelAntiFraudRuleGroupSnapshotMapper rdeModelAntiFraudRuleGroupSnapshotMapper;

    @Autowired
    private RdeModelAntiFraudRuleGroupVersionMapper rdeModelAntiFraudRuleGroupVersionMapper;

    @Autowired
    private RdeModelAntiFraudRuleRecordSnapshotMapper rdeModelAntiFraudRuleRecordSnapshotMapper;

    @Autowired
    private RdeModelAntiFraudRuleRecordVersionMapper rdeModelAntiFraudRuleRecordVersionMapper;

    @Autowired
    private RdeModelDecisionCodeLevelVersionMapper rdeModelDecisionCodeLevelVersionMapper;

    @Autowired
    private ScoreCardReuseSnapshotMapper scoreCardReuseSnapshotMapper;

    @Autowired
    private RuleRecordReuseSnapshotMapper ruleRecordReuseSnapshotMapper;

    @Autowired
    private DataCallingMapper dataCallingMapper;

    @Autowired
    private ModelRegularDataMapper modelRegularDataMapper;

    @Autowired
    private QuotaCardRecordSnapshotMapper quotaCardRecordSnapshotMapper;

    @Autowired
    private RateCardRecordSnapshotMapper rateCardRecordSnapshotMapper;

    @Autowired
    private InterfaceUserPermissionsFeign interfaceUserPermissionsFeign;

    @Autowired
    private InterfaceFieldCache interfaceFieldCache;

    /**
     * 通过manage获取接口出参字段和字段对应类型以及接口类型
     * @param manageNoList
     * @return
     */
    public JSONObject getEnteredGinsengByManageNo(List<String> manageNoList){
        //跑决策引擎的数据拼接
        JSONObject ruleData = new JSONObject();
        ruleData.put("companyModules",new JSONObject());
        for (String manageNo:manageNoList) {
            String result = HttpUtil.get(fieldsUrl+manageNo+"/fields");
            if (JSONUtil.isJson(result)) {
                JSONObject data = JSONObject.parseObject(result);
                JSONObject jsonObject = data.getJSONObject("data");
                if (jsonObject != null) {
                    com.value.decision.model.decisionmanage.model.dto.model.FieldsPortDTO fieldsPortDTO = new FieldsPortDTO();
                    fieldsPortDTO.setInterfaceName(jsonObject.getString("interfaceName"));
                    if (!CollectionUtils.isEmpty(jsonObject.getJSONObject("fields").getJSONArray("output"))){
                        List<FieldsDTO> fieldsDTOList = JSON.parseArray(jsonObject.getJSONObject("fields").getString("output"), FieldsDTO.class);
                        fieldsPortDTO.setAttributeList(fieldsDTOList);
                    }
                    if (jsonObject.getIntValue("responseType") == 0){
                        JSONObject dataInitialization = getDataInitialization(fieldsPortDTO);
                        ruleData.put(jsonObject.getString("interfaceName"),dataInitialization);
                    }else if (jsonObject.getIntValue("responseType") == 5){
                        JSONArray dataInitializationArray = getDataInitializationArray(fieldsPortDTO);
                        ruleData.getJSONObject("companyModules").put(jsonObject.getString("interfaceName"),dataInitializationArray);
                    }
                    logger.info("测试模型-接口编号"+jsonObject.getString("interfaceName")+"接口名称"+jsonObject.getString("interfaceNameZh")+"  初始化数据完成");
                }
            }
        }

        return ruleData;
    }

    /**
     * 遍历字段以及字段类型初始化值 - 对象
     */
    private JSONObject getDataInitialization(FieldsPortDTO fieldsPortDTO){
        JSONObject attributeObject = new JSONObject();
        if (!CollectionUtils.isEmpty(fieldsPortDTO.getAttributeList())){
            List<FieldsDTO> attributeList = fieldsPortDTO.getAttributeList();
            for (int i = 0; i < attributeList.size(); i++) {
                if (attributeList.get(i).getType() == 1){
                    attributeObject.put(attributeList.get(i).getName()," ");
                }else if (attributeList.get(i).getType() == 6){
                    attributeObject.put(attributeList.get(i).getName(),false);
                }else if (attributeList.get(i).getType() == 0){
                    attributeObject.put(attributeList.get(i).getName(),0);
                }else if (attributeList.get(i).getType() == 7){
                    attributeObject.put(attributeList.get(i).getName(),0.0);
                }
            }
        }
        return attributeObject;
    }

    /**
     * 遍历字段以及字段类型初始化值 - 数组
     */
    private JSONArray getDataInitializationArray(FieldsPortDTO fieldsPortDTO){
        JSONArray attributeArray = new JSONArray();
        if (!CollectionUtils.isEmpty(fieldsPortDTO.getAttributeList())){
            List<FieldsDTO> attributeList = fieldsPortDTO.getAttributeList();
            JSONObject attributeObject = new JSONObject();
            for (int i = 0; i < attributeList.size(); i++) {
                if (attributeList.get(i).getType() == 1){
                    attributeObject.put(attributeList.get(i).getName()," ");
                }else if (attributeList.get(i).getType() == 6){
                    attributeObject.put(attributeList.get(i).getName(),false);
                }else if (attributeList.get(i).getType() == 0){
                    attributeObject.put(attributeList.get(i).getName(),0);
                }else if (attributeList.get(i).getType() == 7){
                    attributeObject.put(attributeList.get(i).getName(),0.0);
                }
            }
            attributeArray.add(attributeObject);
        }
        return attributeArray;
    }



    /**
     * 根据入参调用决策请求接口  模型测试
     *
     * @param modelTestTaskDataVO 任务信息
     * @param strList           规则集
     * @param modelTestTaskVO
     * @return
     */
    public JSONObject getRuleCode(ModelTestTaskDataVO modelTestTaskDataVO, List<String> strList, ModelTestTaskVO modelTestTaskVO) throws UnsupportedEncodingException {
        long startTime = System.currentTimeMillis();
        JSONObject dataJson = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("number", modelTestTaskDataVO.getTaskNo());
        jsonObject.put("strList", strList);
        jsonObject.put("JsonObject", modelTestTaskDataVO.getRuleData());
        String encode = URLEncoder.encode(JSONObject.toJSONString(jsonObject, JSONWriter.Feature.WriteMapNullValue), "UTF-8");
        dataJson.put("data", encode);
        //执行决策引擎
        String data = null;
        try{
            data = HttpUtil.post(ruleUrl,JSONObject.toJSONString(dataJson, JSONWriter.Feature.WriteMapNullValue));
        }catch (Exception e){
            e.printStackTrace();
        }
        logger.info("测试任务编号: "+modelTestTaskDataVO.getTaskNo()+" 执行决策引擎返回的结果: "+data);

        JSONObject resultObject = new JSONObject();

        SysRunLog sysRunLog = new SysRunLog();
        sysRunLog.setResultCode("失败");
        if (JSONUtil.isJson(data)) {
            resultObject = JSONObject.parseObject(data);
            sysRunLog.setResultCode("成功");
        }

        //更新模型任务表
        ModelTestTask modelTestTask = new ModelTestTask();
        ModelTestTask modelTestTask1 = new ModelTestTask();
        modelTestTask.setTestDrl(strList.toString());
        modelTestTask.setTestStatus("成功".equals(sysRunLog.getResultCode())?2:3);
        LambdaQueryWrapper<ModelTestTask> wrapper = Wrappers.lambdaQuery(modelTestTask1);
        wrapper.eq(ModelTestTask::getTaskNo,modelTestTaskDataVO.getTaskNo())
                .eq(ModelTestTask::getDataStatus,0);
        iModelTestTaskService.update(modelTestTask,wrapper);

        long endTime = System.currentTimeMillis();
        //运行日志记录
        sysRunLog.setApprovalUserId(modelTestTaskDataVO.getUserId());
        sysRunLog.setApprovalUserName(modelTestTaskDataVO.getApplyUserName().toString());
        sysRunLog.setDeptId(modelTestTaskDataVO.getDeptId());
        sysRunLog.setDeptName("");
        sysRunLog.setProductName(modelTestTaskVO.getProductName()!=null? modelTestTaskVO.getProductName():null);
        sysRunLog.setBusinessCode(modelTestTaskVO.getBusinessName()!=null? modelTestTaskVO.getBusinessName():null);
        sysRunLog.setRuleCode(modelTestTaskVO.getModelId().toString());
        sysRunLog.setRuleName(modelTestTaskVO.getModelName());
        sysRunLog.setCreateTime(LocalDateTime.now());
        sysRunLog.setResult(data);
        sysRunLog.setApiUrl(ruleUrl);
        sysRunLog.setRequestData(dataJson.toString());
        sysRunLog.setReturnTime((int) (endTime - startTime));
        sysRunLog.setModuleId(modelTestTaskVO.getRuleCode());
        sysRunLogMapper.insert(sysRunLog);
        return resultObject;
    }




    /**
     * 获取决策接口请求参数 -manage_no  --模型测试即为原表数据
     *
     * @param modelTestTask
     */
    public List<Map<String, Object>> getRequestData(ModelTestTask modelTestTask) {
        switch (modelTestTask.getRuleCode()) {
            case 1:
                List<Map<String, Object>> mapList = new ArrayList<>();
                List<ScoreIndexRule> scoreIndexRule= scoreIndexRuleMapper.selectList(new LambdaQueryWrapper<ScoreIndexRule>()
                        .eq(ScoreIndexRule::getScordCardId, modelTestTask.getModelId())
                        .eq(ScoreIndexRule::getButtonState, 1)
                        .eq(ScoreIndexRule::getDataState,0));
                scoreIndexRule.forEach(map -> {
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
                return collect;
            case 5:
            case 6:  //TODO 规则复用后的关联表查询复用规则
                List<Map<String, Object>> mapList2 = new ArrayList<>();
                List<RdeModelAntiFraudRuleGroup> rdeModelAntiFraudRuleGroups = rdeModelAntiFraudRuleGroupMapper.selectList(new LambdaQueryWrapper<RdeModelAntiFraudRuleGroup>()
                        .eq(RdeModelAntiFraudRuleGroup::getModelId, modelTestTask.getModelId())
                        .eq(RdeModelAntiFraudRuleGroup::getStatus, 1)
                        .eq(RdeModelAntiFraudRuleGroup::getDataStatus,0));
                rdeModelAntiFraudRuleGroups.forEach(map -> {
                    rdeModelAntiFraudRuleRecordMapper.newList2(map.getId()).forEach(map2 -> {
                        if (map2.getStatus().equals("1")) {
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
                });

                List<Map<String, Object>> collect2 = mapList2.stream().distinct().collect(Collectors.toList());
                return collect2;
        }
        return null;
    }


    /**
     * 获取决策drl文件请求参数  --模型测试即为原表数据
     *
     * @param modelTestTask
     */
    public List<String> getDrlData(ModelTestTask modelTestTask) {
        switch (modelTestTask.getRuleCode()) {
            case 1:
                //根据评分卡id获取所有规则 （开启、非默认指标）
                List<ScoreIndexRule> scoreIndexRules = scoreIndexRuleMapper.selectList(new LambdaQueryWrapper<ScoreIndexRule>()
                        .eq(ScoreIndexRule::getScordCardId, modelTestTask.getModelId())
                        .eq(ScoreIndexRule::getButtonState, 1)
                        .eq(ScoreIndexRule::getDefaultRule,0)
                        .eq(ScoreIndexRule::getDataState,0));
                List<String> strList = new ArrayList<>();
                scoreIndexRules.forEach(map -> {
                    strList.add(map.getTermRule().replaceAll("&gt;",">").replaceAll("&lt;","<"));
                });
                return strList;
            case 5:
            case 6:  //TODO 规则复用后的关联表查询复用规则
                List<RdeModelAntiFraudRuleGroup> rdeModelAntiFraudRuleGroups = rdeModelAntiFraudRuleGroupMapper.selectList(new LambdaQueryWrapper<RdeModelAntiFraudRuleGroup>()
                        .eq(RdeModelAntiFraudRuleGroup::getModelId, modelTestTask.getModelId())
                        .eq(RdeModelAntiFraudRuleGroup::getStatus,1)
                        .eq(RdeModelAntiFraudRuleGroup::getDataStatus,0));
                List<String> resultList = new ArrayList<>();
                rdeModelAntiFraudRuleGroups.forEach(map -> {
                    rdeModelAntiFraudRuleRecordMapper.newList2(map.getId()).forEach(map2 -> {
                        resultList.add(map2.getTermRule().replaceAll("&gt;",">").replaceAll("&lt;","<"));
                    });
                });
                return resultList;
        }
        return new ArrayList<>();
    }

    /**
     * 对List进行分页操作
     *
     * @author Chasen
     * @create 2022/5/18
     */
    public AjaxResult paging(List list, Integer pageNum, Integer pageSize) {
        pageNum = pageNum == null ? 1 : pageNum;
        pageSize = pageSize == null ? 10 : pageSize;
        Page page = new Page(pageNum, pageSize);
        int total = list.size();
        page.setTotal(total);
        //计算当前需要显示的数据下标起始值
        int startIndex = (pageNum - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, total);
        if (startIndex <= total) {
            //从链表中截取需要显示的子链表，并加入到Page
            page.addAll(list.subList(startIndex, endIndex));
        }
        PageInfo pageInfo = new PageInfo<>(page);
        return AjaxResult.success(pageInfo);
    }


    /**
     * 通过manage获取接口入参字段和字段对应类型以及名称
     * @param manageNoList
     * @return
     */
    public List<PolicyRequestDTO> getRequestDataByManageNo(List<String> manageNoList){
        List<PolicyRequestDTO> policyRequestDTOList = new ArrayList<>();
        for (String manageNo:manageNoList) {
            String result = HttpUtil.get(fieldsUrl+manageNo+"/fields");
            if (JSONUtil.isJson(result)) {
                JSONObject data = JSONObject.parseObject(result);
                JSONObject jsonObject = data.getJSONObject("data");
                if (jsonObject != null) {
                    FieldsPortDTO fieldsPortDTO = new FieldsPortDTO();
                    fieldsPortDTO.setInterfaceName(jsonObject.getString("interfaceName"));
                    if (!CollectionUtils.isEmpty(jsonObject.getJSONObject("fields").getJSONArray("input"))){
                        // 修复：使用addAll而不是直接赋值，避免覆盖之前的数据
                        List<PolicyRequestDTO> tempList = JSON.parseArray(jsonObject.getJSONObject("fields").getString("input"), PolicyRequestDTO.class);
                        tempList.stream().forEach(x -> x.setTypeName(getDataFieldName(x.getType())));
                        policyRequestDTOList.addAll(tempList);
                        logger.info("流程模型接口-接口编号"+jsonObject.getString("interfaceName")+"接口名称"+jsonObject.getString("interfaceNameZh")+"  查询入参数据完成，字段数量: {}", tempList.size());
                    } else {
                        logger.info("流程模型接口-接口编号"+jsonObject.getString("interfaceName")+"接口名称"+jsonObject.getString("interfaceNameZh")+"  无入参字段");
                    }
                }
            }
        }
        logger.info("【getRequestDataByManageNo】所有接口入参字段总数: {}", policyRequestDTOList.size());

        return policyRequestDTOList;
    }



    /**
     * 获取决策drl文件请求参数  --流程使用即为快照表数据
     *
     * @param processNodeList
     */
    public List<String> getManageSnapshotData(List<ProcessNode> processNodeList) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (ProcessNode processNode:processNodeList) {
            //规则模型||分类模型  //TODO 规则复用后的关联表查询复用规则
            if (processNode.getModuleId() == 5 || processNode.getModuleId() == 6){
                List<RdeModelAntiFraudRuleGroupSnapshot> rdeModelAntiFraudRuleGroupList = rdeModelAntiFraudRuleGroupSnapshotMapper.selectList(new LambdaQueryWrapper<RdeModelAntiFraudRuleGroupSnapshot>()
                        .eq(RdeModelAntiFraudRuleGroupSnapshot::getModelId, processNode.getRuleCode())
                        .eq(RdeModelAntiFraudRuleGroupSnapshot::getStatus, 1)
                        .eq(RdeModelAntiFraudRuleGroupSnapshot::getDataStatus,0));
                rdeModelAntiFraudRuleGroupList.forEach(map -> {
                    rdeModelAntiFraudRuleRecordSnapshotMapper.newList2(map.getId()).forEach(map2 -> {
                        if (map2.getStatus().equals("1")) {
                            ObjectMapper objectMapper = new ObjectMapper();
                            try {
                                if (StrUtil.isNotBlank(map2.getDataModule())) {
                                    List<Map<String, Object>> list = objectMapper.readValue(map2.getDataModule(), new TypeReference<List<Map<String, Object>>>() {
                                    });
                                    mapList.addAll(list);
                                }
                            } catch (JsonProcessingException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                });

            }//评分模型
            else if (processNode.getModuleId() == 1){
                // 1. 处理评分模型的接口参数
                List<ScoreIndexRuleSnapshot> scoreIndexRule= scoreIndexRuleSnapshotMapper.selectList(new LambdaQueryWrapper<ScoreIndexRuleSnapshot>()
                        .eq(ScoreIndexRuleSnapshot::getScordCardId, processNode.getRuleCode())
                        .eq(ScoreIndexRuleSnapshot::getButtonState, 1)
                        .eq(ScoreIndexRuleSnapshot::getDataState,0));
                scoreIndexRule.forEach(map -> {
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

                // 2. 【新增】通过 评分->评级->额度 的关联关系，提取额度模型的接口参数
                try {
                    Integer scoreCardId = Integer.valueOf(processNode.getRuleCode());
                    logger.info("【getManageSnapshotData】评分模型ID={}, 开始查找关联的额度模型", scoreCardId);

                    // 2.1 查找关联的评级模型
                    RateCardRecordSnapshot rateCard = rateCardRecordSnapshotMapper.selectOne(new LambdaQueryWrapper<RateCardRecordSnapshot>()
                            .eq(RateCardRecordSnapshot::getScoreCardId, scoreCardId)
                            .eq(RateCardRecordSnapshot::getButtonState, 1)
                            .eq(RateCardRecordSnapshot::getDataState, 0));

                    if (rateCard != null) {
                        logger.info("【getManageSnapshotData】找到关联的评级模型ID={}", rateCard.getId());

                        // 2.2 查找关联的额度模型
                        QuotaCardRecordSnapshot quotaCard = quotaCardRecordSnapshotMapper.selectOne(new LambdaQueryWrapper<QuotaCardRecordSnapshot>()
                                .eq(QuotaCardRecordSnapshot::getRateCardId, rateCard.getId())
                                .eq(QuotaCardRecordSnapshot::getButtonState, 1)
                                .eq(QuotaCardRecordSnapshot::getDataState, 0));

                        if (quotaCard != null && StrUtil.isNotBlank(quotaCard.getFormulaVariables())) {
                            logger.info("【getManageSnapshotData】找到关联的额度模型ID={}, 开始解析formulaVariables", quotaCard.getId());

                            // 2.3 解析额度模型的 formulaVariables，提取接口参数
                            Map<String, Map<String, Object>> variablesMap = JSON.parseObject(
                                    quotaCard.getFormulaVariables(),
                                    new com.alibaba.fastjson2.TypeReference<Map<String, Map<String, Object>>>() {}
                            );

                            if (variablesMap != null && !variablesMap.isEmpty()) {
                                int addedCount = 0;
                                // 遍历每个变量字段，提取接口信息
                                for (Map.Entry<String, Map<String, Object>> entry : variablesMap.entrySet()) {
                                    Map<String, Object> fieldInfo = entry.getValue();
                                    String manageNo = (String) fieldInfo.get("manageNo");
                                    String sourceNo = (String) fieldInfo.get("sourceNo");
                                    String interfaceNo = (String) fieldInfo.get("interfaceNo");

                                    if (StrUtil.isNotBlank(manageNo) && StrUtil.isNotBlank(sourceNo) && StrUtil.isNotBlank(interfaceNo)) {
                                        // 构建与评分模型相同格式的 map
                                        Map<String, Object> interfaceMap = new HashMap<>();
                                        interfaceMap.put("manageNo", manageNo);
                                        interfaceMap.put("sourceNo", sourceNo);
                                        interfaceMap.put("interfaceNo", interfaceNo);
                                        mapList.add(interfaceMap);
                                        addedCount++;
                                    }
                                }
                                logger.info("【getManageSnapshotData】额度模型接口参数已添加, 数量={}", addedCount);
                            } else {
                                logger.info("【getManageSnapshotData】额度模型formulaVariables为空");
                            }
                        } else {
                            logger.info("【getManageSnapshotData】未找到关联的额度模型或formulaVariables为空");
                        }
                    } else {
                        logger.info("【getManageSnapshotData】未找到关联的评级模型");
                    }
                } catch (Exception e) {
                    logger.error("【getManageSnapshotData】获取评分模型关联的额度模型接口失败, scoreCardId={}", processNode.getRuleCode(), e);
                }
            }
        }
        List<String> manageList = new ArrayList<>();
        logger.info("【getManageSnapshotData】解析后的mapList数量: {}", mapList.size());
        if (!CollectionUtils.isEmpty(mapList)){
            List<Map<String, Object>> collect = mapList.stream().distinct().collect(Collectors.toList());
            logger.info("【getManageSnapshotData】去重后的mapList数量: {}", collect.size());
            collect.stream().forEach(x ->{
                Object manageNoObj = x.get("manageNo");
                if (manageNoObj != null) {
                    manageList.add(manageNoObj.toString());
                } else {
                    logger.warn("【getManageSnapshotData】发现manageNo为null的记录: {}", x);
                }
            });
        }
        logger.info("【getManageSnapshotData】最终返回的manageList: {}", manageList);

        return manageList;
    }

    /**
     * 遍历及字段类型输出名称
     */
    private String getDataFieldName(Integer type){

        if (type == 1){
            return "字符型";
        }else if (type == 6){
            return "布尔型";
        }else if (type == 0){
            return "数值型";
        }else if (type == 7){
            return "小数型";
        }
        return "-";
    }





    /**
     * 获取决策接口请求参数
     * @param processNode
     * @param processEntry
     * @param taskNo
     */
    public ModelRuleDTO getRequestParameter(ProcessNode processNode, String taskNo, Map<String,Object> processEntry, ProcessPolicy processPolicy,Map<String,Object> userIdentity) {

        ModelRuleDTO modelRuleDTO = new ModelRuleDTO();

        List<Map<String, Object>> mapList = new ArrayList<>();
        List<String> drlData = new ArrayList<>();
        switch (processNode.getModuleId()) {
            case 1:
                scoreAssociationProcess(processNode,processPolicy,mapList,drlData);
                break;
            case 5:
            case 6:
                List<Map<String, Object>> maps = new ArrayList<>();
                ruleAssociationProcess(processNode,processPolicy,mapList,drlData,maps);
                //将所运行的任务drl数据存储
                ModelRegularData modelRegularData = new ModelRegularData();
                modelRegularData.setNodeId(processNode.getNodeId());
                modelRegularData.setRuleData(JSON.toJSONString(maps));
                modelRegularData.setTaskNumber(taskNo);
                modelRegularData.setRuleCode(processNode.getModuleId());
                modelRegularDataMapper.insert(modelRegularData);

//                List<RdeModelAntiFraudRuleGroupSnapshot> rdeModelAntiFraudRuleGroups = rdeModelAntiFraudRuleGroupSnapshotMapper.selectList(new LambdaQueryWrapper<RdeModelAntiFraudRuleGroupSnapshot>()
//                        .eq(RdeModelAntiFraudRuleGroupSnapshot::getModelId, processNode.getRuleCode())
//                        .eq(RdeModelAntiFraudRuleGroupSnapshot::getStatus, 1)
//                        .eq(RdeModelAntiFraudRuleGroupSnapshot::getDataStatus,0));
//                rdeModelAntiFraudRuleGroups.forEach(map -> {
//                    rdeModelAntiFraudRuleRecordSnapshotMapper.newList2(map.getId()).forEach(map2 -> {
//                        if (map2.getStatus().equals("1")) {
//                            ObjectMapper objectMapper = new ObjectMapper();
//                            try {
//                                if (StrUtil.isNotBlank(map2.getDataModule())) {
//                                    List<Map<String, Object>> list = objectMapper.readValue(map2.getDataModule(), new TypeReference<List<Map<String, Object>>>() {
//                                    });
//                                    mapList.addAll(list);
//                                }
//                                if (StrUtil.isNotBlank(map2.getTermRule())){
//                                    drlData.add(map2.getTermRule());
//                                }
//                            } catch (JsonProcessingException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
//                });
        }
        List<String> resultList = new ArrayList<>();
        drlData.stream().forEach(x ->resultList.add(x.replaceAll("&gt;",">").replaceAll("&lt;","<")));
        List<Map<String, Object>> collect = mapList.stream().distinct().collect(Collectors.toList());
        modelRuleDTO.setRequestData(getRequestDataByInterface(collect, taskNo,processEntry,userIdentity));
        modelRuleDTO.setDrlList(resultList);
        return modelRuleDTO;
    }


    //通过数据中台获取决策请求数据
    private JSONObject getRequestDataByInterface(List<Map<String, Object>> collect,String taskNo,Map<String,Object> processEntry,Map<String,Object> userIdentity){
        //请求参数
        JSONObject resultMap2 = new JSONObject();
        JSONObject hashMapRule = new JSONObject();
        collect.forEach(map -> {
            String manageNo = map.get("manageNo").toString();

            // 首次尝试调用接口
            JSONObject jsonObject2 = callDataMiddleStationInterface(map, manageNo, taskNo, processEntry, userIdentity, false);

            // 如果调用失败，刷新缓存后重试一次
            if (jsonObject2 == null) {
                logger.warn("接口[{}]首次调用失败，可能是字段配置变更，尝试刷新缓存后重试", manageNo);
                interfaceFieldCache.refreshCache(manageNo);
                jsonObject2 = callDataMiddleStationInterface(map, manageNo, taskNo, processEntry, userIdentity, true);

                if (jsonObject2 == null) {
                    throw new RuntimeException("接口[" + manageNo + "]调用失败，已刷新缓存重试仍失败");
                }
            }

            if (map.get("objectFlag").equals("0")) {
                resultMap2.put(map.get("objectName").toString(), jsonObject2.get("data"));
            } else if (map.get("objectFlag").equals("5")) {
                hashMapRule.put(map.get("objectName").toString(),jsonObject2.get("data"));
                resultMap2.put("companyModules", hashMapRule);
            }
        });
        resultMap2.put("companyModules", hashMapRule);
        return resultMap2;
    }

    /**
     * 调用数据中台接口（带缓存和字段映射）
     */
    private JSONObject callDataMiddleStationInterface(
            Map<String, Object> map,
            String manageNo,
            String taskNo,
            Map<String, Object> processEntry,
            Map<String, Object> userIdentity,
            boolean isRetry) {

        try {
            // 1. 保持原始行为：透明传递所有字段，orderNo特殊处理使用taskNo
            HashMap<String, Object> hashMap = new HashMap<>();
            processEntry.forEach((key, value) -> {
                if ("orderNo".equals(key)){
                    hashMap.put(key,taskNo);
                }else {
                    hashMap.put(key,value);
                }
            });

            // 2. 精确字段映射增强：根据接口需要的字段（从缓存获取），添加可能需要的字段名变体
            try {
                List<String> requiredFields = interfaceFieldCache.getInputFields(manageNo);

                for (String requiredField : requiredFields) {
                    // 特殊处理：如果接口需要orderNo或orderId，强制使用taskNo
                    if ("orderNo".equals(requiredField) || "orderId".equals(requiredField)) {
                        hashMap.put(requiredField, taskNo);
                        continue;
                    }

                    // 如果hashMap中已经有这个字段了，跳过（避免覆盖原有字段）
                    if (hashMap.containsKey(requiredField)) {
                        continue;
                    }

                    // 尝试通过映射规则找到这个字段的值（从processEntry的所有字段中查找变体）
                    Object value = ParameterMappingUtil.findValueFromVariants(requiredField, processEntry);
                    if (value != null) {
                        hashMap.put(requiredField, value);
                        logger.debug("为接口[{}]添加映射字段: {} = {}", manageNo, requiredField, value);
                    }
                }
            } catch (Exception e) {
                // 字段映射增强失败不影响主流程，记录日志后继续（保证透明传递的基础功能）
                logger.warn("接口[{}]字段映射增强失败，将使用透明传递模式", manageNo, e);
            }

            map.put("paramData", hashMap);
            String result = null;
            EncryptDTO encryptBody = null;
            try {
                //动态获取appkey和sercet
                String appkey = null;
                String secret = null;
                R<InterfaceUser> interfaceUser = interfaceUserPermissionsFeign.queryAppKeyByUserId(Integer.parseInt(userIdentity.get("userId").toString()));
                logger.info("查询appkey：interfaceUser{}",JSON.toJSONString(interfaceUser));
                if (interfaceUser.getCode() == 200){
                    InterfaceUser interfaceUserData = interfaceUser.getData();
                    appkey = interfaceUserData.getAppKey();
                    secret = interfaceUserData.getSecret();
                }

                encryptBody = EncryptBodyUtil.createEncryptBody(appkey, secret, map.get("manageNo").toString()
                        , map.get("sourceNo").toString(), map.get("interfaceNo").toString(), taskNo, hashMap);
                logger.info("数据接口请求{}: hashMap={}", isRetry ? "(重试)" : "", map);
                result = HttpUtil.post(interfaceRequestUrl, JSON.toJSONString(encryptBody));
                logger.info("数据接口结果{}: result={}", isRetry ? "(重试)" : "", result);
                String status = "未查得";
                JSONObject jsonObject = JSONObject.parseObject(result);
                if (jsonObject != null && jsonObject.get("data") != null ) {
                    String code = jsonObject.getString("code");
                    if ("200".equals(code) ||  "00".equals(code)) {
                        status = "查得";
                    }
                }
                saveDataCelling(taskNo,map.get("manageNo").toString(),map.get("sourceNo").toString(),status,result);
            } catch (Exception e) {
                logger.error("数据中台接口调用失败，manageNo: {}, 请求参数: {}, 返还参数: {}",
                        manageNo, JSON.toJSONString(encryptBody), result, e);
                // 返回null表示失败，让调用方决定是否重试
                return null;
            }

            JSONObject jsonObject2;
            if (JSONUtil.isJson(result) && JSONObject.parseObject(result).getString("code").equals("200")) {
                jsonObject2 = JSONObject.parseObject(result);
                return jsonObject2;
            } else {
                logger.warn("数据中台返回非200状态码，manageNo: {}, 返还参数: {}", manageNo, result);
                // 如果是重试且仍失败，抛出异常并附带详细信息
                if (isRetry) {
                    throw new RuntimeException("数据中台接口调用失败，返还参数：" + result);
                }
                // 首次失败返回null，让调用方决定是否重试
                return null;
            }

        } catch (RuntimeException e) {
            // RuntimeException 直接向上抛出，保留错误信息（包括数据中台返回的详细信息）
            throw e;
        } catch (Exception e) {
            // 其他异常（如网络异常、JSON解析异常等）记录日志后返回null，让调用方决定是否重试
            logger.error("调用数据中台接口发生异常，manageNo: {}", manageNo, e);
            return null;
        }
    }


    public void saveDataCelling(String taskNo,String manageNo, String sourceNo, String callStatus,String responseBody){
        //存储数据至 数据调用表
        DataCalling dataCalling = new DataCalling();
        dataCalling.setCreateTime(LocalDateTime.now());
        dataCalling.setOrderNo(taskNo);
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
        dataCallingMapper.insert(dataCalling);
    }


    /**
     * 根据入参调用决策请求接口  模型测试
     *
     * @param modelTestTaskDataVO 任务信息
     * @return
     */
    public JSONObject getDecisionCode(ModelTestTaskDataVO modelTestTaskDataVO) throws UnsupportedEncodingException {
        long startTime = System.currentTimeMillis();
        JSONObject dataJson = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("number", modelTestTaskDataVO.getTaskNo());
        jsonObject.put("strList", StringUtils.isNotEmpty(modelTestTaskDataVO.getStrList())?modelTestTaskDataVO.getStrList():new ArrayList<>());
        jsonObject.put("JsonObject", modelTestTaskDataVO.getRuleData());
        String encode = URLEncoder.encode(JSONObject.toJSONString(jsonObject, JSONWriter.Feature.WriteMapNullValue), "UTF-8");
        dataJson.put("data", encode);
        //执行决策引擎
        String data = null;
        try{
            if(modelTestTaskDataVO.getRuleCode() == 6){
                data = HttpUtil.post(ruleflUrl, JSONObject.toJSONString(dataJson, JSONWriter.Feature.WriteMapNullValue));
            }else {
                data = HttpUtil.post(ruleUrl,JSONObject.toJSONString(dataJson, JSONWriter.Feature.WriteMapNullValue));
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        JSONObject resultObject = new JSONObject();

        SysRunLog sysRunLog = new SysRunLog();
        sysRunLog.setResultCode("失败");
        if (JSONUtil.isJson(data)) {
            resultObject = JSONObject.parseObject(data);
            sysRunLog.setResultCode("成功");
        }

        //更新模型任务表
        ModelTaskRecord modelTaskRecord = new ModelTaskRecord();
        ModelTaskRecord ModelTaskRecord1 = new ModelTaskRecord();
        modelTaskRecord.setTermRule(StringUtils.isNotEmpty(modelTestTaskDataVO.getStrList())?modelTestTaskDataVO.getStrList().toString():"[]");
//        modelTaskRecord.setTaskStatus("成功".equals(sysRunLog.getResultCode())?3:4);
        LambdaQueryWrapper<ModelTaskRecord> wrapper = Wrappers.lambdaQuery(ModelTaskRecord1);
        wrapper.eq(ModelTaskRecord::getTaskNo,modelTestTaskDataVO.getTaskNo())
                .eq(ModelTaskRecord::getDataStatus,0);
        iModelTaskRecordService.update(modelTaskRecord,wrapper);

        long endTime = System.currentTimeMillis();
        //运行日志记录
//        sysRunLog.setApprovalUserId(modelTestTaskDataVO.getUserId());
//        sysRunLog.setApprovalUserName(modelTestTaskDataVO.getApplyUserName().toString());
//        sysRunLog.setDeptId(modelTestTaskDataVO.getDeptId());
//        sysRunLog.setDeptName("");
//        sysRunLog.setProductName(modelTestTaskVO.getProductName()!=null? modelTestTaskVO.getProductName():null);
//        sysRunLog.setBusinessCode(modelTestTaskVO.getBusinessName()!=null? modelTestTaskVO.getBusinessName():null);
//        sysRunLog.setRuleCode(modelTestTaskVO.getModelId().toString());
//        sysRunLog.setRuleName(modelTestTaskVO.getModelName());
//        sysRunLog.setCreateTime(LocalDateTime.now());
//        sysRunLog.setResult(data);
//        sysRunLog.setApiUrl(ruleUrl);
//        sysRunLog.setRequestData(dataJson.toString());
//        sysRunLog.setReturnTime((int) (endTime - startTime));
//        sysRunLog.setModuleId(modelTestTaskVO.getRuleCode());
//        sysRunLogMapper.insert(sysRunLog);
        return resultObject;
    }


    /**
     * 评分关联数据处理
     * @param processNode
     * @param processPolicy
     */
    public void scoreAssociationProcess(ProcessNode processNode,ProcessPolicy processPolicy,List<Map<String, Object>> mapList,List<String> drlData){
        //先查一下此modelId是否为标准评分卡,如果为标准评分卡即需要根据版本号去对应版本表中获取
        ScoreCardReuseSnapshot scoreCardReuseSnapshot = scoreCardReuseSnapshotMapper.selectOne(new LambdaQueryWrapper<ScoreCardReuseSnapshot>()
                .eq(ScoreCardReuseSnapshot::getParentCardId,processNode.getRuleCode())
                .eq(ScoreCardReuseSnapshot::getDataStatus,0)
                .eq(ScoreCardReuseSnapshot::getBuildProjectCode,processPolicy.getProductId())
                .eq(ScoreCardReuseSnapshot::getBuildProjectCode,processPolicy.getBusinessCode())
                .eq(ScoreCardReuseSnapshot::getButtonState, RuleConstants.DATA_ENABLE));
        if (scoreCardReuseSnapshot == null){
            List<ScoreIndexRuleSnapshot> scoreIndexRuleSnapshots = scoreIndexRuleSnapshotMapper.selectList(new LambdaQueryWrapper<ScoreIndexRuleSnapshot>()
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
                    if (StrUtil.isNotBlank(map.getTermRule())){
                        drlData.add(map.getTermRule().replaceAll(">", ">").replaceAll("<", "<"));
                    }
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            });
        }else {
            List<ScoreIndexRuleVersion> scoreIndexRuleSnapshots = scoreIndexRuleVersionMapper.selectList(new LambdaQueryWrapper<ScoreIndexRuleVersion>()
                    .eq(ScoreIndexRuleVersion::getScordCardId, processNode.getRuleCode())
                    .eq(ScoreIndexRuleVersion::getVersionControl,scoreCardReuseSnapshot.getVersionControl())
                    .eq(ScoreIndexRuleVersion::getButtonState, 1)
                    .eq(ScoreIndexRuleVersion::getDataState,0));
            scoreIndexRuleSnapshots.forEach(map -> {
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    if (StrUtil.isNotBlank(map.getDataModule())) {
                        List<Map<String, Object>> list = objectMapper.readValue(map.getDataModule(), new TypeReference<List<Map<String, Object>>>() {
                        });
                        mapList.addAll(list);
                    }
                    if (StrUtil.isNotBlank(map.getTermRule())){
                        drlData.add(map.getTermRule().replaceAll(">", ">").replaceAll("<", "<"));
                    }
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            });
        }
    }


    /**
     * 规则关联数据处理
     * @param processNode
     * @param processPolicy
     */
    public void ruleAssociationProcess(ProcessNode processNode,ProcessPolicy processPolicy,List<Map<String, Object>> mapList,List<String> drlData,List<Map<String, Object>> maps){
        //1,编写是否为标准模型
        LambdaQueryWrapper<RuleRecordReuseSnapshot> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(RuleRecordReuseSnapshot::getParentCardId,processNode.getRuleCode())
                .eq(RuleRecordReuseSnapshot::getDataStatus,0)
                .eq(RuleRecordReuseSnapshot::getMoudleId,RuleConstants.POLICY_GROUP_LIST)
                .eq(RuleRecordReuseSnapshot::getDeptId,processNode.getDeptId())
                .eq(RuleRecordReuseSnapshot::getBuildProjectCode,processPolicy.getProductId())
                .eq(RuleRecordReuseSnapshot::getBuildBusinessCode,processPolicy.getBusinessCode());
        RuleRecordReuseSnapshot ruleRecordReuseSnapshot = ruleRecordReuseSnapshotMapper.selectOne(wrapper);
        //2,是请求关联表找到版本号然后请求rdeModelAntiFraudVerson数据
        if (ruleRecordReuseSnapshot != null){
            LambdaQueryWrapper<RdeModelAntiFraudRuleGroupVersion> wrapperGroup = Wrappers.lambdaQuery();
            wrapperGroup.eq(RdeModelAntiFraudRuleGroupVersion::getModelId,processNode.getRuleCode())
                    .eq(RdeModelAntiFraudRuleGroupVersion::getStatus,RuleConstants.DATA_ENABLE)
                    .eq(RdeModelAntiFraudRuleGroupVersion::getDataStatus,0)
                    .eq(RdeModelAntiFraudRuleGroupVersion::getVersionControl,ruleRecordReuseSnapshot.getVersionControl());
            List<RdeModelAntiFraudRuleGroupVersion> rdeModelAntiFraudRuleGroupVersionList = rdeModelAntiFraudRuleGroupVersionMapper.selectList(wrapperGroup);
            rdeModelAntiFraudRuleGroupVersionList.stream().forEach(x ->{
                LambdaQueryWrapper<RdeModelAntiFraudRuleRecordVersion> wrapperRecord = Wrappers.lambdaQuery();
                wrapperRecord.eq(RdeModelAntiFraudRuleRecordVersion::getGroupId,x.getId())
                        .eq(RdeModelAntiFraudRuleRecordVersion::getStatus,RuleConstants.DATA_ENABLE)
                        .eq(RdeModelAntiFraudRuleRecordVersion::getDataStatus,0)
                        .eq(RdeModelAntiFraudRuleRecordVersion::getVersionControl,ruleRecordReuseSnapshot.getVersionControl());
                List<RdeModelAntiFraudRuleRecordVersion> rdeModelAntiFraudRuleRecordVersionList = rdeModelAntiFraudRuleRecordVersionMapper.selectList(wrapperRecord);
                rdeModelAntiFraudRuleRecordVersionList.stream().forEach(rule ->{
                    Map<String,Object> map = new HashMap<>();
                    LambdaQueryWrapper<RdeModelDecisionCodeLevelVersion> wrapperLevel = Wrappers.lambdaQuery();
                    wrapperLevel.eq(RdeModelDecisionCodeLevelVersion::getId,rule.getCodeId())
                            .eq(RdeModelDecisionCodeLevelVersion::getDataStatus,0)
                            .eq(RdeModelDecisionCodeLevelVersion::getVersionControl,ruleRecordReuseSnapshot.getVersionControl());
                    RdeModelDecisionCodeLevelVersion rdeModelDecisionCodeLevelVersion = rdeModelDecisionCodeLevelVersionMapper.selectOne(wrapperLevel);
                    map.put("groupName",x.getName());
                    map.put("code",rdeModelDecisionCodeLevelVersion.getCode());
                    map.put("riskLevel",rdeModelDecisionCodeLevelVersion.getLevel());
                    map.put("riskStatement",rdeModelDecisionCodeLevelVersion.getContent());
                    map.put("stronglyReject",rdeModelDecisionCodeLevelVersion.getStronglyReject());
                    maps.add(map);
                    ObjectMapper objectMapper = new ObjectMapper();
                    try {
                        List<Map<String, Object>> list = objectMapper.readValue(rdeModelDecisionCodeLevelVersion.getDataModule(), new TypeReference<List<Map<String, Object>>>() {
                        });
                        mapList.addAll(list);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    drlData.add(rdeModelDecisionCodeLevelVersion.getTermRule().replaceAll(">", ">").replaceAll("<", "<"));
                });
            });
        }else {

            //模型底下自建数据
            List<RdeModelAntiFraudRuleGroupSnapshot> rdeModelAntiFraudRuleGroups = rdeModelAntiFraudRuleGroupSnapshotMapper.selectList(new LambdaQueryWrapper<RdeModelAntiFraudRuleGroupSnapshot>()
                    .eq(RdeModelAntiFraudRuleGroupSnapshot::getModelId, processNode.getRuleCode())
                    .eq(RdeModelAntiFraudRuleGroupSnapshot::getStatus, 1)
                    .eq(RdeModelAntiFraudRuleGroupSnapshot::getDataStatus,0));
            rdeModelAntiFraudRuleGroups.forEach(map -> {
                rdeModelAntiFraudRuleRecordSnapshotMapper.newList2(map.getId()).forEach(map2 -> {
                    if (map2.getStatus().equals("1")) {
                        ObjectMapper objectMapper = new ObjectMapper();
                        try {
                            if (StrUtil.isNotBlank(map2.getDataModule())) {
                                List<Map<String, Object>> list = objectMapper.readValue(map2.getDataModule(), new TypeReference<List<Map<String, Object>>>() {
                                });
                                mapList.addAll(list);
                            }
                            if (StrUtil.isNotBlank(map2.getTermRule())){
                                drlData.add(map2.getTermRule().replaceAll(">", ">").replaceAll("<", "<"));
                            }
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                    }
                });
            });

            //模型底下引用的规则组和规则数据
            LambdaQueryWrapper<RuleRecordReuseSnapshot> ruleRecordReuseGroup = Wrappers.lambdaQuery();
            ruleRecordReuseGroup.eq(RuleRecordReuseSnapshot::getDeptId,processNode.getDeptId())
                    .eq(RuleRecordReuseSnapshot::getBuildBusinessCode,processPolicy.getBusinessCode())
                    .eq(RuleRecordReuseSnapshot::getBuildProjectCode,processPolicy.getProductId())
                    .eq(RuleRecordReuseSnapshot::getBuildRuleCode,processNode.getModuleId())
                    .eq(RuleRecordReuseSnapshot::getMoudleId,RuleConstants.RULE_GROUP_LIST)
                    .eq(RuleRecordReuseSnapshot::getButtonState,RuleConstants.DATA_ENABLE)
                    .eq(RuleRecordReuseSnapshot::getRuleId,processNode.getRuleCode());
            List<RuleRecordReuseSnapshot> ruleRecordReuseGroupList = ruleRecordReuseSnapshotMapper.selectList(ruleRecordReuseGroup);
            ruleRecordReuseGroupList.stream().forEach(reuseGroup ->{
                LambdaQueryWrapper<RdeModelAntiFraudRuleGroupVersion> wrapperGroupVersion = Wrappers.lambdaQuery();
                wrapperGroupVersion.eq(RdeModelAntiFraudRuleGroupVersion::getId,reuseGroup.getParentCardId())
                        .eq(RdeModelAntiFraudRuleGroupVersion::getVersionControl,reuseGroup.getVersionControl())
                        .eq(RdeModelAntiFraudRuleGroupVersion::getStatus,RuleConstants.DATA_ENABLE)
                        .eq(RdeModelAntiFraudRuleGroupVersion::getDataStatus,0);
                List<RdeModelAntiFraudRuleGroupVersion> rdeModelAntiFraudRuleGroupVersionList = rdeModelAntiFraudRuleGroupVersionMapper.selectList(wrapperGroupVersion);
                rdeModelAntiFraudRuleGroupVersionList.stream().forEach(ruleGroup ->{
                    LambdaQueryWrapper<RdeModelAntiFraudRuleRecordVersion> wrapperRecordVersion = Wrappers.lambdaQuery();
                    wrapperRecordVersion.eq(RdeModelAntiFraudRuleRecordVersion::getGroupId,ruleGroup.getId())
                            .eq(RdeModelAntiFraudRuleRecordVersion::getVersionControl,ruleGroup.getVersionControl())
                            .eq(RdeModelAntiFraudRuleRecordVersion::getStatus,RuleConstants.DATA_ENABLE)
                            .eq(RdeModelAntiFraudRuleRecordVersion::getDataStatus,0);
                    List<RdeModelAntiFraudRuleRecordVersion> rdeModelAntiFraudRuleRecordVersionList = rdeModelAntiFraudRuleRecordVersionMapper.selectList(wrapperRecordVersion);
                    //查询标准规则id下的level数据找到manage_no和drl
                    rdeModelAntiFraudRuleRecordVersionList.stream().forEach(rule ->{
                        Map<String,Object> map = new HashMap<>();
                        LambdaQueryWrapper<RdeModelDecisionCodeLevelVersion> wrapperLevel = Wrappers.lambdaQuery();
                        wrapperLevel.eq(RdeModelDecisionCodeLevelVersion::getId,rule.getCodeId())
                                .eq(RdeModelDecisionCodeLevelVersion::getDataStatus,0)
                                .eq(RdeModelDecisionCodeLevelVersion::getVersionControl,rule.getVersionControl());
                        RdeModelDecisionCodeLevelVersion rdeModelDecisionCodeLevelVersion = rdeModelDecisionCodeLevelVersionMapper.selectOne(wrapperLevel);
                        map.put("groupName",ruleGroup.getName());
                        map.put("code",rdeModelDecisionCodeLevelVersion.getCode());
                        map.put("riskLevel",rdeModelDecisionCodeLevelVersion.getLevel());
                        map.put("riskStatement",rdeModelDecisionCodeLevelVersion.getContent());
                        map.put("stronglyReject",rdeModelDecisionCodeLevelVersion.getStronglyReject());
                        maps.add(map);
                        ObjectMapper objectMapper = new ObjectMapper();
                        try {
                            List<Map<String, Object>> list = objectMapper.readValue(rdeModelDecisionCodeLevelVersion.getDataModule(), new TypeReference<List<Map<String, Object>>>() {
                            });
                            mapList.addAll(list);
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                        drlData.add(rdeModelDecisionCodeLevelVersion.getTermRule().replaceAll(">", ">").replaceAll("<", "<"));
                    });
                });
            });

            //模型底下引用的规则数据
            LambdaQueryWrapper<RuleRecordReuseSnapshot> ruleRecordReuseRecord = Wrappers.lambdaQuery();
            ruleRecordReuseRecord.eq(RuleRecordReuseSnapshot::getDeptId,processNode.getDeptId())
                    .eq(RuleRecordReuseSnapshot::getBuildBusinessCode,processPolicy.getBusinessCode())
                    .eq(RuleRecordReuseSnapshot::getBuildProjectCode,processPolicy.getProductId())
                    .eq(RuleRecordReuseSnapshot::getBuildRuleCode,processNode.getModuleId())
                    .eq(RuleRecordReuseSnapshot::getMoudleId,RuleConstants.RULE_LIST)
                    .eq(RuleRecordReuseSnapshot::getButtonState,RuleConstants.DATA_ENABLE)
                    .eq(RuleRecordReuseSnapshot::getRuleId,processNode.getRuleCode());
            List<RuleRecordReuseSnapshot> ruleRecordReuseRecordList = ruleRecordReuseSnapshotMapper.selectList(ruleRecordReuseGroup);
            ruleRecordReuseRecordList.stream().forEach(reuseRecord ->{
                LambdaQueryWrapper<RdeModelAntiFraudRuleRecordVersion> wrapperRecordVersion = Wrappers.lambdaQuery();
                wrapperRecordVersion.eq(RdeModelAntiFraudRuleRecordVersion::getId,reuseRecord.getParentCardId())
                        .eq(RdeModelAntiFraudRuleRecordVersion::getVersionControl,reuseRecord.getVersionControl())
                        .eq(RdeModelAntiFraudRuleRecordVersion::getStatus,RuleConstants.DATA_ENABLE)
                        .eq(RdeModelAntiFraudRuleRecordVersion::getDataStatus,0);
                List<RdeModelAntiFraudRuleRecordVersion> rdeModelAntiFraudRuleRecordVersionList = rdeModelAntiFraudRuleRecordVersionMapper.selectList(wrapperRecordVersion);
                //查询标准规则id下的level数据找到manage_no和drl
                rdeModelAntiFraudRuleRecordVersionList.stream().forEach(rule ->{
                    //通过groupId在快照表中找到规则组名称
                    RdeModelAntiFraudRuleGroupSnapshot rdeModelAntiFraudRuleGroupSnapshot = rdeModelAntiFraudRuleGroupSnapshotMapper.selectById(rule.getGroupId());
                    Map<String,Object> map = new HashMap<>();
                    LambdaQueryWrapper<RdeModelDecisionCodeLevelVersion> wrapperLevel = Wrappers.lambdaQuery();
                    wrapperLevel.eq(RdeModelDecisionCodeLevelVersion::getId,rule.getCodeId())
                            .eq(RdeModelDecisionCodeLevelVersion::getDataStatus,0)
                            .eq(RdeModelDecisionCodeLevelVersion::getVersionControl,rule.getVersionControl());
                    RdeModelDecisionCodeLevelVersion rdeModelDecisionCodeLevelVersion = rdeModelDecisionCodeLevelVersionMapper.selectOne(wrapperLevel);
                    map.put("groupName",rdeModelAntiFraudRuleGroupSnapshot.getName());
                    map.put("code",rdeModelDecisionCodeLevelVersion.getCode());
                    map.put("riskLevel",rdeModelDecisionCodeLevelVersion.getLevel());
                    map.put("riskStatement",rdeModelDecisionCodeLevelVersion.getContent());
                    map.put("stronglyReject",rdeModelDecisionCodeLevelVersion.getStronglyReject());
                    maps.add(map);
                    ObjectMapper objectMapper = new ObjectMapper();
                    try {
                        List<Map<String, Object>> list = objectMapper.readValue(rdeModelDecisionCodeLevelVersion.getDataModule(), new TypeReference<List<Map<String, Object>>>() {
                        });
                        mapList.addAll(list);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    drlData.add(rdeModelDecisionCodeLevelVersion.getTermRule().replaceAll(">", ">").replaceAll("<", "<"));
                });
            });

        }
    }


}
