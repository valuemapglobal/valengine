package com.value.decision.model.decisionmanage.controller;


import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.value.decision.common.dto.EncryptDTO;
import com.value.decision.common.utils.EncryptBodyUtil;
import com.value.decision.common.configure.ModelTaskRecordProperties;
import com.risksmart.common.core.constant.QuotaCalculationConstants;
import com.risksmart.common.core.constant.SecurityConstants;
import com.value.decision.common.utils.AjaxResultUtil;
import com.value.decision.common.utils.CommonUtil;
import com.value.decision.common.utils.SnowFlakeCloud;
import com.risksmart.common.core.web.AjaxResult;
import com.value.decision.model.decisionmanage.model.ModelProcessData;
import com.value.decision.model.decisionmanage.model.ScoreCardRecordSnapshot;
import com.value.decision.model.decisionmanage.model.dto.TaskFailureDetailDTO;
import com.value.decision.model.decisionmanage.model.dto.model.ModelRuleResultDTO;
import com.value.decision.model.decisionmanage.model.dto.model.ModelTaskRecordVO;
import com.value.decision.model.decisionmanage.model.dto.model.TaskRecordDTO;
import com.value.decision.model.decisionmanage.service.*;
import com.value.decision.process.service.feign.FeignDataMiddleStationService;
import com.value.decision.process.vo.InterfaceUser;
import com.value.decision.model.rdenew.function.CommonRuleFunctionDataNew;
import com.value.decision.snapshot.domain.RdeModelAntiFraudSnapshot;
import com.value.decision.snapshot.mapper.RdeModelAntiFraudSnapshotMapper;
import com.risksmart.common.core.domain.R;
import com.value.decision.common.utils.security.SecurityUtils;
import com.risksmart.system.domain.SysUser;
import com.value.decision.common.security.LoginUser;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * 模型任务记录表 前端控制器
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@Log4j2
@RestController
@RequestMapping("/model-task-record")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@AllArgsConstructor
public class ModelTaskRecordController {

    private IModelTaskRecordService iModelTaskRecordService;
    private CommonRuleFunctionDataNew commonRuleFunctionDataNew;
    private IModelProcessDataService modelProcessDataService;
    private RdeModelAntiFraudSnapshotMapper rdeModelAntiFraudSnapshotMapper;
    private IScoreCardRecordSnapshotService scoreCardRecordSnapshotService;
    private RuoYiService ruoYiService;
    private ModelTaskRecordProperties properties;
    private FeignDataMiddleStationService feignDataMiddleStationService;
    private IFailureLogService failureLogService;

    /**
     * 流程任务启动 - 支持单个和批量执行
     * @param modelTaskRecordVO
     * @return
     */
    @PostMapping("/processTaskInitiation")
    public AjaxResult processTaskInitiation(@RequestBody ModelTaskRecordVO modelTaskRecordVO){

        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null){
            return AjaxResult.error(SecurityConstants.ERR_SECURITY_MSG);
        }
        //用户信息
        Map<String,Object> userIdentity = new HashMap<>();
        userIdentity.put("userId",loginUser.getSysUser().getUserId());
        userIdentity.put("authorizationToken",loginUser.getSysUser().getDeptId());
        modelTaskRecordVO.setUserIdentity(userIdentity);
        modelTaskRecordVO.setUserIdentification(String.valueOf(loginUser.getSysUser().getUserId()));
        String taskNo = "MTask_" + SnowFlakeCloud.nextId();
        modelTaskRecordVO.setTaskNo(taskNo);
        modelTaskRecordVO.setDeptId(loginUser.getSysUser().getDeptId().intValue());
        modelTaskRecordVO.setUserId(loginUser.getSysUser().getUserId().intValue());
        modelTaskRecordVO.setApplicationUser(loginUser.getUsername());

        // 单个任务执行
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        Thread thread = new Thread(() -> {
            try {
                iModelTaskRecordService.processTaskInitiation(modelTaskRecordVO);
            } catch (Exception e) {
                e.printStackTrace();
                //异常推送飞书消息
                CommonUtil.sendBotMessage("流程通用启动接口异常，请联系管理员！");
                return;
            }
        });
        executorService.execute(thread);
        executorService.shutdown();

        Map<String,Object> map = new HashMap<>();
        map.put("taskNo",taskNo);
        return AjaxResult.success(map);
    }

    @PostMapping("/encryptHex")
    public AjaxResult encryptHex(@RequestBody String body){
        String aesKey = properties.getAesKey();
        byte[] key = HexUtil.decodeHex(aesKey);
        SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, key);
        return AjaxResult.success(aes.encryptHex(body,CharsetUtil.CHARSET_UTF_8));
    }

    /**
     * 额度
     * @param params 入参
     *  cname-企业名称
     *  userIdentification-用户标识
     *  orderNo-请求唯一流水号
     * @return creditFacility-授信额度；description-额度计算依据（年报）
     */
    @PostMapping("/quota")
    public AjaxResult quota(@RequestBody JSONObject params) throws Exception {
        //校验参数
        String cname = params.getString("cname");
        String userIdentification = params.getString("userIdentification");
        String orderNo = params.getString("orderNo");
        if (StrUtil.isBlank(cname) || StrUtil.isBlank(userIdentification) || StrUtil.isBlank(orderNo)){
            return AjaxResult.error("cname、userIdentification、orderNo不能为空");
        }
        //解密得到userId
        String aesKey = properties.getAesKey();
        byte[] key = HexUtil.decodeHex(aesKey);
        SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, key);
        String userIdStr = aes.decryptStr(userIdentification, CharsetUtil.CHARSET_UTF_8);
        log.info("解密得到用户ID：{}",userIdStr);
        Long userId = Long.valueOf(userIdStr);
        //根据userId拿取数据中台密钥
        R<InterfaceUser> interfaceUserR = feignDataMiddleStationService.queryAppKeyByUserId(userId.intValue());
        InterfaceUser interfaceUser = interfaceUserR.getData();
        if (interfaceUser==null){
            return AjaxResult.error(String.format("未找到用户%s的数据中台密钥,code:%s,msg:%s",
                    userIdStr,interfaceUserR.getCode(),interfaceUserR.getMsg()
            ));
        }
        //获取数据中台小微画像企业年报数据
        EncryptDTO encryptBody = EncryptBodyUtil.createEncryptBody(
                interfaceUser.getAppKey(),
                interfaceUser.getSecret(),
                properties.getQuota().getManageNo(),
                properties.getQuota().getSourceNo(),
                properties.getQuota().getInterfaceNo(),
                orderNo,
                params
        );
        JSONObject result = feignDataMiddleStationService.api(encryptBody);
        log.debug("【/quota接口】数据中台返回结果: {}", result.toJSONString());

        //根据公式计算授信额度
        HashMap<String, Object> map = new HashMap<>();
        map.put("creditFacility",BigDecimal.ZERO);

        // 获取data字段 (数据中台直接返回最新财务数据)
        Object dataObj = result.get("data");
        if (dataObj == null) {
            log.warn("【/quota接口】财务数据为空：{}", result.getString("msg"));
            return AjaxResult.success("财务数据为空："+result.getString("msg"),map);
        }

        // 将data转换为JSONObject (兼容FastJSON返回的LinkedHashMap)
        JSONObject financialData;
        if (dataObj instanceof Map) {
            log.debug("【/quota接口】检测到Map类型({}),转换为JSONObject", dataObj.getClass().getName());
            financialData = new JSONObject((Map<String, Object>) dataObj);
        } else if (dataObj instanceof JSONObject) {
            financialData = (JSONObject) dataObj;
        } else {
            log.warn("【/quota接口】data类型不支持: {}", dataObj.getClass().getName());
            return AjaxResult.success("data类型不支持: " + dataObj.getClass().getName(),map);
        }

        // 检查是否包含必要的财务字段
        if (!financialData.containsKey("cFinanceTotalOwnersEquity") && !financialData.containsKey("cfinanceTotalOwnersEquity")) {
            log.warn("【/quota接口】财务数据缺少必要字段cFinanceTotalOwnersEquity");
            return AjaxResult.success("财务数据缺少必要字段cFinanceTotalOwnersEquity",map);
        }

        log.debug("【/quota接口】获取到最新财务数据，准备计算授信额度");

        //计算
        BigDecimal creditFacility = calculateCreditFacility(financialData);
        map.put("creditFacility",creditFacility);
        map.put("description","最新财务数据");

        log.info("【/quota接口】计算成功，企业：{}，授信额度：{}", cname, creditFacility);
        return AjaxResult.success(map);
    }
    /**
     * 从JSONObject中获取BigDecimal字段(兼容大小写)
     * 数据中台返回的是驼峰命名(cFinance*),先尝试驼峰,如果为null则尝试小写
     */
    private BigDecimal getFinancialField(JSONObject data, String lowercaseKey, String camelCaseKey) {
        BigDecimal value = data.getBigDecimal(camelCaseKey);
        return value != null ? value : data.getBigDecimal(lowercaseKey);
    }

    /**
     * 计算授信额度，公式如下：
     *  IF(
     *     cFinanceTotalOwnersEquity <= 0,
     *     0,
     *     MIN(
     *         cFinanceMainBusinessRevenue * 0.5,
     *         MAX(0, cFinanceNetProfit) * 7,
     *         cFinanceTotalOwnersEquity * 0.5
     *     ) * MAX(
     *         0,
     *         1 - 2 * MAX(0, cFinanceAssetLiabilityRatio - 0.5)
     *     )
     * )
     * @param f 财务年报数据
     * @return 授信额度，保留2位小数
     */
    private BigDecimal calculateCreditFacility(JSONObject f){
        // 获取财务数据 (兼容大小写不同的字段名)
        BigDecimal cFinanceTotalOwnersEquity = getFinancialField(f,
                "cfinanceTotalOwnersEquity", "cFinanceTotalOwnersEquity");
        BigDecimal cFinanceMainBusinessRevenue = getFinancialField(f,
                "cfinanceMainBusinessRevenue", "cFinanceMainBusinessRevenue");
        BigDecimal cFinanceNetProfit = getFinancialField(f,
                "cfinanceNetProfit", "cFinanceNetProfit");
        BigDecimal cFinanceAssetLiabilityRatio = getFinancialField(f,
                "cfinanceAssetLiabilityRatio", "cFinanceAssetLiabilityRatio");

        // 处理 null 值情况
        if (cFinanceTotalOwnersEquity == null || cFinanceMainBusinessRevenue == null ||
                cFinanceNetProfit == null || cFinanceAssetLiabilityRatio == null) {
            log.warn("【/quota接口】财务数据字段存在null值，返回0");
            return BigDecimal.ZERO;
        }

        // 资产负债率单位转换: 如果大于1,说明是百分比数值(如66.54),需要除以100转为小数(如0.6654)
        if (cFinanceAssetLiabilityRatio.compareTo(BigDecimal.ONE) > 0) {
            cFinanceAssetLiabilityRatio = cFinanceAssetLiabilityRatio.divide(QuotaCalculationConstants.PERCENT_DIVISOR, QuotaCalculationConstants.SCALE_PRECISION, RoundingMode.HALF_UP);
            log.debug("【/quota接口】资产负债率单位转换: {} → {}", cFinanceAssetLiabilityRatio.multiply(QuotaCalculationConstants.PERCENT_DIVISOR), cFinanceAssetLiabilityRatio);
        }

        // 1. 如果所有者权益 <= 0，返回 0
        if (cFinanceTotalOwnersEquity.compareTo(BigDecimal.ZERO) <= 0) {
            log.info("【/quota接口】所有者权益为负，返回0");
            return BigDecimal.ZERO;
        }

        log.debug("【/quota接口】财务数据 - 所有者权益:{}, 营业收入:{}, 净利润:{}, 资产负债率:{}",
                cFinanceTotalOwnersEquity, cFinanceMainBusinessRevenue, cFinanceNetProfit, cFinanceAssetLiabilityRatio);

        // 2. 计算 MIN 部分
        BigDecimal revenuePart = cFinanceMainBusinessRevenue.multiply(QuotaCalculationConstants.REVENUE_FACTOR);
        BigDecimal netProfitPart = cFinanceNetProfit.max(BigDecimal.ZERO).multiply(QuotaCalculationConstants.PROFIT_FACTOR);
        BigDecimal equityPart = cFinanceTotalOwnersEquity.multiply(QuotaCalculationConstants.EQUITY_FACTOR);

        log.debug("【/quota接口】MIN(营业收入*0.5={}, 净利润*7={}, 所有者权益*0.5={}) = {}",
                revenuePart, netProfitPart, equityPart, revenuePart.min(netProfitPart).min(equityPart));

        // 取三者中的最小值
        BigDecimal minPart = revenuePart.min(netProfitPart).min(equityPart);

        // 3. 计算调节因子 MAX(0, 1 - 2 * MAX(0, 资产负债率 - 0.5))
        BigDecimal ratioExcess = cFinanceAssetLiabilityRatio.subtract(QuotaCalculationConstants.RATIO_THRESHOLD).max(BigDecimal.ZERO);
        BigDecimal adjustmentFactor = BigDecimal.ONE
                .subtract(QuotaCalculationConstants.RATIO_ADJUSTMENT.multiply(ratioExcess))
                .max(BigDecimal.ZERO);

        log.debug("【/quota接口】调节因子 = MAX(0, 1 - 2*{}) = {}", ratioExcess, adjustmentFactor);

        // 4. 最终结果 = minPart * adjustmentFactor
        BigDecimal result = minPart.multiply(adjustmentFactor);
        log.debug("【/quota接口】最终结果 = {} * {} = {}", minPart, adjustmentFactor, result);

        // 5. 保留两位小数并四舍五入
        return result.setScale(QuotaCalculationConstants.RESULT_SCALE, RoundingMode.HALF_UP);
    }


    /**
     * 流程任务启动 - 入参传递用户信息
     * @param modelTaskRecordVO
     * @return
     */
    @PostMapping("/processInitiation")
    public AjaxResult processInitiation(@RequestBody ModelTaskRecordVO modelTaskRecordVO){

        if (StrUtil.isBlank(modelTaskRecordVO.getUserIdentification())){
            return AjaxResult.error(SecurityConstants.USER_INFORMATION_SPACE);
        }

        //获取加密后的用户id标识
        String userIdentification = modelTaskRecordVO.getUserIdentification();
        //解密得到userId
        String aesKey = properties.getAesKey();
        byte[] key = HexUtil.decodeHex(aesKey);
        SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, key);
        String userIdStr = aes.decryptStr(userIdentification, CharsetUtil.CHARSET_UTF_8);
        log.info("解密得到用户ID：{}",userIdStr);
        Long userId = Long.valueOf(userIdStr);
        //通过userId获取用户信息
        SysUser userInfo = ruoYiService.getUserById(userId);
        if (userInfo==null){
            return AjaxResult.error(String.format("未找到用户%s的信息",userIdStr));
        }

        //用户信息
        Map<String,Object> userIdentity = new HashMap<>();
        userIdentity.put("userId",userInfo.getUserId());
        userIdentity.put("authorizationToken",userInfo.getDeptId());
        modelTaskRecordVO.setUserIdentity(userIdentity);
        String taskNo = "MTask_" + SnowFlakeCloud.nextId();
        modelTaskRecordVO.setTaskNo(taskNo);
        modelTaskRecordVO.setDeptId(userInfo.getDeptId().intValue());
        modelTaskRecordVO.setUserId(userInfo.getUserId().intValue());
        modelTaskRecordVO.setApplicationUser(userInfo.getUserName());

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        Thread thread = new Thread(() -> {
            try {
                iModelTaskRecordService.processTaskInitiation(modelTaskRecordVO);
            } catch (Exception e) {
                e.printStackTrace();
                //异常推送飞书消息
                CommonUtil.sendBotMessage("流程通用启动接口异常，请联系管理员！");
                return;
            }
        });
        executorService.execute(thread);
        executorService.shutdown();

        Map<String,Object> map = new HashMap<>();
        map.put("taskNo",taskNo);
        return AjaxResult.success(map);
    }
    /**
     * 流程任务启动（同步）
     * @param modelTaskRecordVO
     * @return
     */
    @PostMapping("/processTaskInitiationSync")
    public AjaxResult processTaskInitiationSync(@RequestBody ModelTaskRecordVO modelTaskRecordVO){
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null){
            return AjaxResult.error(SecurityConstants.ERR_SECURITY_MSG);
        }
        //用户信息
        Map<String,Object> userIdentity = new HashMap<>();
        userIdentity.put("userId",loginUser.getSysUser().getUserId());
        userIdentity.put("authorizationToken",loginUser.getSysUser().getDeptId());
        modelTaskRecordVO.setUserIdentity(userIdentity);
        String taskNo = "MTask_" + SnowFlakeCloud.nextId();
        modelTaskRecordVO.setTaskNo(taskNo);
        modelTaskRecordVO.setDeptId(loginUser.getSysUser().getDeptId().intValue());
        modelTaskRecordVO.setUserId(loginUser.getSysUser().getUserId().intValue());
        modelTaskRecordVO.setApplicationUser(loginUser.getUsername());
        modelTaskRecordVO.setUserIdentification(String.valueOf(loginUser.getSysUser().getUserId()));

        try {
            Object result = iModelTaskRecordService.processTaskInitiationSync(modelTaskRecordVO);
            return AjaxResult.success(result);
        } catch (Exception e) {
            log.error("同步流程任务启动失败", e);
            return AjaxResult.error("任务执行失败：" + e.getMessage());
        }
    }


    /**
     * 流程任务列表
     * @param modelTaskRecordVO
     * @return
     */
    @PostMapping("/taskRecordList")
    public AjaxResult taskRecordList(@RequestBody ModelTaskRecordVO modelTaskRecordVO){
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null){
            return AjaxResult.error(SecurityConstants.ERR_SECURITY_MSG);
        }
        modelTaskRecordVO.setDeptId(loginUser.getSysUser().getDeptId().intValue());

        try{
            List<TaskRecordDTO> taskRecordDTOList = iModelTaskRecordService.taskRecordList(modelTaskRecordVO);
            taskRecordDTOList.forEach(task ->{
                task.setTermRule(null);
            });
            AjaxResult paging = commonRuleFunctionDataNew.paging(taskRecordDTOList, modelTaskRecordVO.getPageNum(), modelTaskRecordVO.getPageSize());
            return AjaxResult.success(paging.get("data"));
        }catch (Exception e){
            return AjaxResult.error("格式有误");
        }
    }
    @PostMapping("/taskRecordListBussiness")
    public AjaxResult taskRecordList2(@RequestBody ModelTaskRecordVO modelTaskRecordVO){
        if (StrUtil.isBlank(modelTaskRecordVO.getUserIdentification())){
            return AjaxResult.error(SecurityConstants.USER_INFORMATION_SPACE);
        }

        //获取加密后的用户id标识
        String userIdentification = modelTaskRecordVO.getUserIdentification();
        //解密得到userId
        String aesKey = properties.getAesKey();
        byte[] key = HexUtil.decodeHex(aesKey);
        SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, key);
        String userIdStr = aes.decryptStr(userIdentification, CharsetUtil.CHARSET_UTF_8);
        log.info("解密得到用户ID：{}",userIdStr);
        Long userId = Long.valueOf(userIdStr);
        //通过userId获取用户信息
        SysUser userInfo = ruoYiService.getUserById(userId);
        if (userInfo==null){
            return AjaxResult.error(String.format("未找到用户%s的信息",userIdStr));
        }

        modelTaskRecordVO.setDeptId(userInfo.getDeptId().intValue());

        try{
            List<TaskRecordDTO> taskRecordDTOList = iModelTaskRecordService.taskRecordList(modelTaskRecordVO);
            taskRecordDTOList.forEach(task ->{
                task.setTermRule(null);
            });
            AjaxResult paging = commonRuleFunctionDataNew.paging(taskRecordDTOList, modelTaskRecordVO.getPageNum(), modelTaskRecordVO.getPageSize());
            return AjaxResult.success(paging.get("data"));
        }catch (Exception e){
            return AjaxResult.error("格式有误");
        }
    }


    @PostMapping("/dataResponseForm")
    public AjaxResult dataResponseForm(@RequestBody ModelTaskRecordVO modelTaskRecordVO){
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null){
            return AjaxResult.error(SecurityConstants.ERR_SECURITY_MSG);
        }
        modelTaskRecordVO.setDeptId(loginUser.getSysUser().getDeptId().intValue());
        ModelRuleResultDTO modelRuleResultDTO = iModelTaskRecordService.dataResponseForm(modelTaskRecordVO);
        return AjaxResult.success(modelRuleResultDTO);
    }
    @PostMapping("/dataResponseFormBussiness")
    public AjaxResult dataResponseForm2(@RequestBody ModelTaskRecordVO modelTaskRecordVO){
        if (StrUtil.isBlank(modelTaskRecordVO.getUserIdentification())){
            return AjaxResult.error(SecurityConstants.USER_INFORMATION_SPACE);
        }

        //获取加密后的用户id标识
        String userIdentification = modelTaskRecordVO.getUserIdentification();
        //解密得到userId
        String aesKey = properties.getAesKey();
        byte[] key = HexUtil.decodeHex(aesKey);
        SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, key);
        String userIdStr = aes.decryptStr(userIdentification, CharsetUtil.CHARSET_UTF_8);
        log.info("解密得到用户ID：{}",userIdStr);
        Long userId = Long.valueOf(userIdStr);
        //通过userId获取用户信息
        SysUser userInfo = ruoYiService.getUserById(userId);
        if (userInfo==null){
            return AjaxResult.error(String.format("未找到用户%s的信息",userIdStr));
        }


        modelTaskRecordVO.setDeptId(userInfo.getDeptId().intValue());
        ModelRuleResultDTO modelRuleResultDTO = iModelTaskRecordService.dataResponseForm(modelTaskRecordVO);
        return AjaxResult.success(modelRuleResultDTO);
    }

    /**
     * 获取任务下所有节点结果
     * @param taskNo 任务编号
     * @return 所有节点结果
     */
    @GetMapping("/nodesResult/{taskNo}")
    public AjaxResult nodesResult(@PathVariable("taskNo") String taskNo){
        //登录校验
        final LoginUser user = SecurityUtils.getLoginUser();
        //查询所有节点结果
        final List<ModelProcessData> modelProcessDataList = modelProcessDataService.lambdaQuery()
                .eq(ModelProcessData::getDeptId, user.getSysUser().getDeptId())
                .eq(ModelProcessData::getTaskNo, taskNo)
                .orderByAsc(ModelProcessData::getCreateTime)
                .list();
        //按照rule_code(策略标识导航)分组
        final Map<Integer, List<ModelProcessData>> ruleCodeMap = modelProcessDataList.stream()
                .collect(Collectors.groupingBy(ModelProcessData::getRuleCode));
        //数据库查询模型名称
        final Map<String, String> modelNameMap = getModelNameMap(ruleCodeMap);
        //节点结果解析
        final List<Map<String, Object>> result = modelProcessDataList.stream().map(node -> {
            final Map<String, Object> map = new HashMap<>();
            map.put("result", AjaxResultUtil.parse(node.getResponseValue()).get(AjaxResult.DATA_TAG));
            map.put("modelId", node.getModelId());
            map.put("ruleCode", node.getRuleCode());
            map.put("modelName", modelNameMap.get(node.getRuleCode() + ":" + node.getModelId()));
            return map;
        }).collect(Collectors.toList());
        return AjaxResult.success(result);
    }

    /**
     * 模型名称
     * @param ruleCodeMap 所有节点结果按照rule_code分组
     * @return key-rule_code:model_id
     *         value-模型名称
     */
    private Map<String,String> getModelNameMap(Map<Integer, List<ModelProcessData>> ruleCodeMap){
        final Map<String,String> modelNameMap = new HashMap<>();
        ruleCodeMap.forEach((key,value) -> {
            if (key == 1){//评分
                scoreCardRecordSnapshotService.lambdaQuery()
                        .in(
                                ScoreCardRecordSnapshot::getId,
                                value.stream().map(ModelProcessData::getModelId).collect(Collectors.toList())
                        )
                        .list()
                        .forEach(score -> modelNameMap.put(key+":"+score.getId(),score.getScoreCard()));
            }else if (key == 5 || key == 6){//规则、分类
                rdeModelAntiFraudSnapshotMapper.selectList(
                        Wrappers.lambdaQuery(RdeModelAntiFraudSnapshot.class)
                                .in(
                                        RdeModelAntiFraudSnapshot::getId,
                                        value.stream().map(ModelProcessData::getModelId).collect(Collectors.toList())
                                )
                ).forEach(rde -> modelNameMap.put(key+":"+rde.getId(),rde.getName()));;
            }
        });
        return modelNameMap;
    }

    /**
     * 获取报告形式的响应数据
     */
    @PostMapping("/report-response")
    public AjaxResult getReportResponseForm(@RequestBody ModelTaskRecordVO modelTaskRecordVO) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null){
            return AjaxResult.error(SecurityConstants.ERR_SECURITY_MSG);
        }
        modelTaskRecordVO.setDeptId(loginUser.getSysUser().getDeptId().intValue());
        return AjaxResult.success(iModelTaskRecordService.getReportResponseForm(modelTaskRecordVO));
    }

    /**
     * 获取任务失败原因
     * @param taskNo 任务编号
     * @return 仅返回用户友好的失败消息
     */
    @GetMapping("/failure/{taskNo}")
    public AjaxResult getFailureDetail(@PathVariable("taskNo") String taskNo) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            return AjaxResult.error(SecurityConstants.ERR_SECURITY_MSG);
        }

        if (StrUtil.isBlank(taskNo)) {
            return AjaxResult.error("任务编号不能为空");
        }

        try {
            // 失败详情(带权限校验)
            Integer deptId = loginUser.getSysUser().getDeptId().intValue();
            TaskFailureDetailDTO failureDetail = failureLogService.getFailureDetail(taskNo, deptId);

            if (failureDetail == null) {
                return AjaxResult.error("未找到该任务的失败记录或无权限查看");
            }

            Map<String, String> result = new HashMap<>();
            result.put("failureMessage", failureDetail.getUserMessage());

            return AjaxResult.success(result);

        } catch (Exception e) {
            log.error("查询任务失败详情异常: taskNo={}", taskNo, e);
            return AjaxResult.error("查询失败详情失败: " + e.getMessage());
        }
    }
}