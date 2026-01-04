package com.value.decision.model.decisionmanage.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.value.decision.common.utils.FormulaUtil;
import com.value.decision.model.decisionmanage.mapper.DataCallingMapper;
import com.value.decision.model.decisionmanage.mapper.QuotaCardRadiusMapper;
import com.value.decision.model.decisionmanage.mapper.QuotaCardRecordMapper;
import com.value.decision.model.decisionmanage.model.*;
import com.value.decision.model.decisionmanage.model.dto.StandardQuotaFormulaDTO;
import com.value.decision.model.decisionmanage.service.IQuotaCardRadiusSnapshotService;
import com.value.decision.model.decisionmanage.service.IQuotaCardRecordService;
import com.value.decision.model.decisionmanage.service.IQuotaCardRecordSnapshotService;
import com.risksmart.common.core.web.AjaxResult;
import com.value.decision.common.security.LoginUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * <p>
 * 额度卡主表 服务实现类
 * </p>
 *
 * @author hc
 * @since 2023-08-12
 */
@Slf4j
@Service
public class QuotaCardRecordServiceImpl extends ServiceImpl<QuotaCardRecordMapper, QuotaCardRecord> implements IQuotaCardRecordService {

    @Resource
    private QuotaCardRecordMapper quotaCardRecordMapper;

    @Resource
    private QuotaCardRadiusMapper quotaCardRadiusMapper;

    @Resource
    private DataCallingMapper dataCallingMapper;

    @Override
    public AjaxResult newList(QuotaCardRecord quotaCardRecord,LoginUser loginUser) {
        Long deptId;
        if(quotaCardRecord.getDeptId() != null){
            deptId = Long.valueOf(quotaCardRecord.getDeptId());
        }else{
            deptId = loginUser.getSysUser().getDeptId();
        }
        LambdaQueryWrapper<QuotaCardRecord> like = new LambdaQueryWrapper<QuotaCardRecord>().like(QuotaCardRecord::getQuotaCard, quotaCardRecord.getQuotaCard());
        like.eq(QuotaCardRecord::getDeptId,deptId);
        if (StrUtil.isNotBlank(quotaCardRecord.getProjectCode())) {
            like.eq(QuotaCardRecord::getProjectCode, quotaCardRecord.getProjectCode());
        }
        like.eq(QuotaCardRecord::getBusinessCode,quotaCardRecord.getBusinessCode());
        like.eq(QuotaCardRecord::getRuleCode,quotaCardRecord.getRuleCode());
        like.orderByDesc(QuotaCardRecord::getCreateTime);
        return AjaxResult.success(quotaCardRecordMapper.selectList(like));
    }

    @Override
    public AjaxResult updateStatus(QuotaCardRecord quotaCardRecord) {
        if (quotaCardRecord.getId() == null || quotaCardRecord.getButtonState() == null) {
            return AjaxResult.error("参数缺失");
        }
        //查询评级范围表 不存在则提示无法更新状态
        if (quotaCardRadiusMapper.selectCount(new LambdaQueryWrapper<QuotaCardRadius>().eq(QuotaCardRadius::getQuotaCardId,quotaCardRecord.getId())) == 0) {
            return AjaxResult.error("当前风险额度规则未提交,请先完善风险额度规则");
        }
        QuotaCardRecord quotaCardRecord1 = new QuotaCardRecord();
        quotaCardRecord1.setButtonState(quotaCardRecord.getButtonState());
        quotaCardRecordMapper.update(quotaCardRecord1, new LambdaQueryWrapper<QuotaCardRecord>().eq(QuotaCardRecord::getId, quotaCardRecord.getId()));
        return AjaxResult.success();
    }

    @Resource
    private IQuotaCardRecordSnapshotService quotaCardRecordSnapshotService;

    @Resource
    private IQuotaCardRadiusSnapshotService quotaCardRadiusSnapshotService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult release(QuotaCardRecord quotaCardRecord, LoginUser loginUser) {
        if (quotaCardRecord.getProjectCode() == null || quotaCardRecord.getBusinessCode() == null || quotaCardRecord.getRuleCode() == null) {
            return AjaxResult.error("参数缺失");
        }
        Integer deptId = loginUser.getSysUser().getDeptId().intValue();
        LambdaQueryWrapper<QuotaCardRecord> eq = new LambdaQueryWrapper<QuotaCardRecord>()
                .eq(QuotaCardRecord::getDeptId, deptId)
                .eq(QuotaCardRecord::getProjectCode, quotaCardRecord.getProjectCode())
                .eq(QuotaCardRecord::getBusinessCode, quotaCardRecord.getBusinessCode())
                .eq(QuotaCardRecord::getRuleCode, quotaCardRecord.getRuleCode());
        LambdaQueryWrapper<QuotaCardRecordSnapshot> eqS = new LambdaQueryWrapper<QuotaCardRecordSnapshot>()
                .eq(QuotaCardRecordSnapshot::getDeptId, deptId)
                .eq(QuotaCardRecordSnapshot::getProjectCode, quotaCardRecord.getProjectCode())
                .eq(QuotaCardRecordSnapshot::getBusinessCode, quotaCardRecord.getBusinessCode())
                .eq(QuotaCardRecordSnapshot::getRuleCode, quotaCardRecord.getRuleCode());
        quotaCardRecordSnapshotService.remove(eqS);
        List<QuotaCardRecord> rateCardRecords = quotaCardRecordMapper.selectList(eq);
        List<QuotaCardRecordSnapshot> quotaCardRecordSnapshots = new ArrayList<>();
        quotaCardRecordSnapshots = JSON.parseArray(JSON.toJSONString(rateCardRecords), QuotaCardRecordSnapshot.class);
        if (quotaCardRecordSnapshots != null) {
            quotaCardRecordSnapshotService.saveBatch(quotaCardRecordSnapshots);
        }

        LambdaQueryWrapper<QuotaCardRadius> eq1 = new LambdaQueryWrapper<QuotaCardRadius>()
                .eq(QuotaCardRadius::getDeptId, deptId)
                .eq(QuotaCardRadius::getProjectCode, quotaCardRecord.getProjectCode())
                .eq(QuotaCardRadius::getBusinessCode, quotaCardRecord.getBusinessCode())
                .eq(QuotaCardRadius::getRuleCode, quotaCardRecord.getRuleCode());
        LambdaQueryWrapper<QuotaCardRadiusSnapshot> eqS1 = new LambdaQueryWrapper<QuotaCardRadiusSnapshot>()
                .eq(QuotaCardRadiusSnapshot::getDeptId, deptId)
                .eq(QuotaCardRadiusSnapshot::getProjectCode, quotaCardRecord.getProjectCode())
                .eq(QuotaCardRadiusSnapshot::getBusinessCode, quotaCardRecord.getBusinessCode())
                .eq(QuotaCardRadiusSnapshot::getRuleCode, quotaCardRecord.getRuleCode());
        quotaCardRadiusSnapshotService.remove(eqS1);
        List<QuotaCardRadius> quotaCardRadii = quotaCardRadiusMapper.selectList(eq1);
        List<QuotaCardRadiusSnapshot> quotaCardRadiusSnapshots = new ArrayList<>();
        quotaCardRadiusSnapshots = JSON.parseArray(JSON.toJSONString(quotaCardRadii), QuotaCardRadiusSnapshot.class);
        if (quotaCardRadiusSnapshots != null) {
            quotaCardRadiusSnapshotService.saveBatch(quotaCardRadiusSnapshots);
        }
        return AjaxResult.success();
    }

    // ==================== 标准额度公式配置实现 ====================

    /**
     * 保存标准额度计算公式配置（优化版）
     *
     * ⚠️ 重要：此方法不保存快照表！快照表只在 release() 方法中保存
     *
     * 实现思路：
     * 1. 校验额度卡是否存在
     * 2. 校验数据权限（部门隔离）
     * 3. 公式校验（非空校验 + 字段列表非空校验）
     * 4. 生成可执行公式（将完整路径替换为字段代码）
     * 5. 构建字段变量映射（用于执行时查找字段值）
     * 6. 更新主表数据
     * 7. 不保存快照表（复用现有的 release() 方法）
     *
     * @param formulaDTO 公式配置数据
     * @param loginUser 当前登录用户
     * @return 保存结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult saveStandardQuotaFormula(StandardQuotaFormulaDTO formulaDTO, LoginUser loginUser) {
        // 1. 校验额度卡是否存在
        QuotaCardRecord quotaCard = this.getById(formulaDTO.getQuotaCardId());
        if (quotaCard == null) {
            return AjaxResult.error("额度卡不存在");
        }

        // 2. 校验数据权限（部门隔离）
        Integer loginUserDeptId = loginUser.getSysUser().getDeptId().intValue();
        if (!quotaCard.getDeptId().equals(loginUserDeptId)) {
            log.warn("用户{}尝试操作其他部门数据, quotaCardDeptId={}, loginUserDeptId={}",
                    loginUser.getUserid(), quotaCard.getDeptId(), loginUserDeptId);
            return AjaxResult.error("无权操作其他部门数据");
        }

        // 3. 公式校验
        String validationError = checkFormula(formulaDTO);
        if (validationError != null) {
            return AjaxResult.success(validationError);
        }

        // 4. 生成可执行公式（将完整路径替换为字段代码）
        String executableFormula = generateExecutableFormula(formulaDTO.getRawFormula(), formulaDTO.getFormulaFields());
        log.info("公式转换 - 原始公式: {}", formulaDTO.getRawFormula());
        log.info("公式转换 - 可执行公式: {}", executableFormula);

        // 5. 构建字段变量映射（用于执行时查找字段值）
        Map<String, Map<String, Object>> variablesMap = buildVariablesMap(formulaDTO.getFormulaFields());
        String variablesJson = JSON.toJSONString(variablesMap);

        // 6. 保存前端传入的完整树形结构数据（用于前端回显）
        String selectedFieldsJson = null;
        if (formulaDTO.getSelectedFields() != null && !formulaDTO.getSelectedFields().isEmpty()) {
            selectedFieldsJson = JSON.toJSONString(formulaDTO.getSelectedFields());
        }

        // 7. 更新主表数据
        quotaCard.setStandardQuotaFormula(executableFormula);              // 可执行公式（用于POI执行）
        quotaCard.setStandardQuotaFormulaZh(formulaDTO.getRawFormula());   // 原始公式（用于前端展示）
        quotaCard.setFormulaVariables(variablesJson);                      // 字段映射
        quotaCard.setSelectedFields(selectedFieldsJson);                   // 前端完整树形结构数据
        quotaCard.setUpdateTime(LocalDateTime.now());

        this.updateById(quotaCard);

        log.info("保存标准额度公式成功, quotaCardId={}, userId={}, executableFormula={}",
                quotaCard.getId(), loginUser.getUserid(), executableFormula);

        return AjaxResult.success("配置已保存成功");
    }

    /**
     * 生成可执行公式
     * 将原始公式中的完整路径替换为字段代码
     *
     * 示例：
     * 输入: "MAX(司法诉讼特征变量/新司法案件明细接口/案号年份T据今相差 特殊值999*100, 1000)"
     * 输出: "MAX(caseNumberYearBefore*100, 1000)"
     *
     * @param rawFormula 原始公式（包含完整路径）
     * @param fields 字段列表
     * @return 可执行公式（字段代码）
     */
    private String generateExecutableFormula(String rawFormula, List<StandardQuotaFormulaDTO.FormulaField> fields) {
        String executableFormula = rawFormula;

        // 按路径长度降序排序，避免短路径替换掉长路径的一部分
        // 例如: "账户信息/账户基本/开户时长(月)" 应该在 "账户信息/账户基本" 之前替换
        List<StandardQuotaFormulaDTO.FormulaField> sortedFields = fields.stream()
                .sorted((f1, f2) -> f2.getFullPath().length() - f1.getFullPath().length())
                .collect(java.util.stream.Collectors.toList());

        for (StandardQuotaFormulaDTO.FormulaField field : sortedFields) {
            executableFormula = executableFormula.replace(field.getFullPath(), field.getFieldCode());
        }

        return executableFormula;
    }

    /**
     * 构建字段变量映射
     * 用于执行时根据字段代码查找字段信息
     *
     * @param fields 字段列表
     * @return 字段变量映射 Map
     */
    private Map<String, Map<String, Object>> buildVariablesMap(List<StandardQuotaFormulaDTO.FormulaField> fields) {
        Map<String, Map<String, Object>> variablesMap = new HashMap<>();

        for (StandardQuotaFormulaDTO.FormulaField field : fields) {
            Map<String, Object> fieldInfo = new HashMap<>();
            fieldInfo.put("fieldCode", field.getFieldCode());
            fieldInfo.put("fieldName", field.getFieldName());
            fieldInfo.put("manageNo", field.getManageNo());
            fieldInfo.put("dataType", field.getDataType());
            fieldInfo.put("fullPath", field.getFullPath());

            // 从 allPaths 解析 sourceNo 和 interfaceNo（用于调用中台接口）
            String sourceNo = extractSourceNoFromAllPaths(field);
            String interfaceNo = extractInterfaceNoFromAllPaths(field);
            if (StrUtil.isNotBlank(sourceNo)) {
                fieldInfo.put("sourceNo", sourceNo);
            }
            if (StrUtil.isNotBlank(interfaceNo)) {
                fieldInfo.put("interfaceNo", interfaceNo);
            }

            variablesMap.put(field.getFieldCode(), fieldInfo);
        }

        return variablesMap;
    }

    /**
     * 从 allPaths 中提取 sourceNo
     * allPaths 是三层路径数组：[数据源层, 接口层, 字段层]
     * 接口层包含完整信息：interfaceSourceNo + interfaceNo + interfaceManageNo
     */
    private String extractSourceNoFromAllPaths(StandardQuotaFormulaDTO.FormulaField field) {
        try {
            List<Object> allPaths = field.getAllPaths();
            if (allPaths == null || allPaths.isEmpty()) {
                return null;
            }

            // 遍历 allPaths，找到接口层（同时包含 interfaceSourceNo, interfaceNo, interfaceManageNo）
            for (Object pathObj : allPaths) {
                if (pathObj instanceof Map) {
                    Map<String, Object> pathMap = (Map<String, Object>) pathObj;
                    String interfaceSourceNo = (String) pathMap.get("interfaceSourceNo");
                    String interfaceNo = (String) pathMap.get("interfaceNo");
                    String interfaceManageNo = (String) pathMap.get("interfaceManageNo");

                    // 接口层：同时有这三个字段
                    if (StrUtil.isNotBlank(interfaceSourceNo) &&
                        StrUtil.isNotBlank(interfaceNo) &&
                        StrUtil.isNotBlank(interfaceManageNo)) {
                        return interfaceSourceNo;
                    }
                }
            }
        } catch (Exception e) {
            log.warn("解析 allPaths 提取 sourceNo 失败, fieldCode={}", field.getFieldCode(), e);
        }
        return null;
    }

    /**
     * 从 allPaths 中提取 interfaceNo
     */
    private String extractInterfaceNoFromAllPaths(StandardQuotaFormulaDTO.FormulaField field) {
        try {
            List<Object> allPaths = field.getAllPaths();
            if (allPaths == null || allPaths.isEmpty()) {
                return null;
            }

            // 遍历 allPaths，找到接口层
            for (Object pathObj : allPaths) {
                if (pathObj instanceof Map) {
                    Map<String, Object> pathMap = (Map<String, Object>) pathObj;
                    String interfaceSourceNo = (String) pathMap.get("interfaceSourceNo");
                    String interfaceNo = (String) pathMap.get("interfaceNo");
                    String interfaceManageNo = (String) pathMap.get("interfaceManageNo");

                    // 接口层：同时有这三个字段
                    if (StrUtil.isNotBlank(interfaceSourceNo) &&
                        StrUtil.isNotBlank(interfaceNo) &&
                        StrUtil.isNotBlank(interfaceManageNo)) {
                        return interfaceNo;
                    }
                }
            }
        } catch (Exception e) {
            log.warn("解析 allPaths 提取 interfaceNo 失败, fieldCode={}", field.getFieldCode(), e);
        }
        return null;
    }

    /**
     * 获取标准额度计算公式配置（优化版）
     *
     * 返回格式：
     * {
     *   "quotaCardId": 1,
     *   "quotaCard": "测试额度卡",
     *   "rawFormula": "MAX(账户信息/账户基本/开户时长(月)*100, 1000)",
     *   "executableFormula": "MAX(account_age*100, 1000)",
     *   "formulaFields": [...]
     * }
     *
     * @param quotaCardId 额度卡ID
     * @param loginUser 当前登录用户
     * @return 公式配置数据
     */
    @Override
    public AjaxResult getStandardQuotaFormula(Integer quotaCardId, LoginUser loginUser) {
        QuotaCardRecord quotaCard = this.getById(quotaCardId);

        if (quotaCard == null) {
            return AjaxResult.error("额度卡不存在");
        }

        // 校验数据权限
        Integer loginUserDeptId = loginUser.getSysUser().getDeptId().intValue();
        if (!quotaCard.getDeptId().equals(loginUserDeptId)) {
            return AjaxResult.error("无权查看其他部门数据");
        }

        // 构造返回数据
        Map<String, Object> result = new HashMap<>();
        result.put("quotaCardId", quotaCard.getId());
        result.put("quotaCard", quotaCard.getQuotaCard());
        result.put("rawFormula", quotaCard.getStandardQuotaFormulaZh());          // 原始公式（前端展示）
        result.put("executableFormula", quotaCard.getStandardQuotaFormula());    // 可执行公式（调试用）

        // 解析字段变量映射，转换为字段列表
        List<Map<String, Object>> formulaFields = new ArrayList<>();
        if (StrUtil.isNotBlank(quotaCard.getFormulaVariables())) {
            Map<String, Object> variablesMap = JSON.parseObject(quotaCard.getFormulaVariables(), Map.class);

            for (Map.Entry<String, Object> entry : variablesMap.entrySet()) {
                Map<String, Object> fieldInfo = (Map<String, Object>) entry.getValue();
                formulaFields.add(fieldInfo);
            }
        }
        result.put("formulaFields", formulaFields);

        // 解析已选字段的完整树形结构数据（前端回显用）
        List<Object> selectedFields = new ArrayList<>();
        if (StrUtil.isNotBlank(quotaCard.getSelectedFields())) {
            selectedFields = JSON.parseArray(quotaCard.getSelectedFields(), Object.class);
        }
        result.put("selectedFields", selectedFields);

        return AjaxResult.success(result);
    }

    /**
     * 公式校验（增强版）
     *
     * 校验步骤：
     * 1. 校验原始公式不能为空
     * 2. 校验字段列表不能为空
     * 3. 校验每个字段的必要属性
     * 4. 校验公式中使用的字段是否都已配置
     * 5. 校验可执行公式语法是否正确（使用 POI）
     *
     * @param formulaDTO 公式配置数据
     * @return null=校验通过，非null=错误信息（给前端展示）
     */
    private String checkFormula(StandardQuotaFormulaDTO formulaDTO) {
        // 1. 校验原始公式不能为空
        if (StrUtil.isBlank(formulaDTO.getRawFormula())) {
            log.error("原始公式不能为空");
            return "原始公式不能为空";
        }

        // 2. 校验字段列表不能为空
        if (formulaDTO.getFormulaFields() == null || formulaDTO.getFormulaFields().isEmpty()) {
            log.error("公式字段列表不能为空");
            return "公式字段列表不能为空，请至少配置一个字段";
        }

        // 3. 校验每个字段的必要属性 + fieldCode 唯一性
        Set<String> fieldCodeSet = new HashSet<>();
        Set<String> fullPathSet = new HashSet<>();

        for (StandardQuotaFormulaDTO.FormulaField field : formulaDTO.getFormulaFields()) {
            if (StrUtil.isBlank(field.getFieldCode())) {
                String errorMsg = String.format("字段代码不能为空（字段名称：%s）", field.getFieldName());
                log.error(errorMsg);
                return errorMsg;
            }
            if (StrUtil.isBlank(field.getFullPath())) {
                String errorMsg = String.format("字段完整路径不能为空（字段代码：%s）", field.getFieldCode());
                log.error(errorMsg);
                return errorMsg;
            }
            if (StrUtil.isBlank(field.getManageNo())) {
                String errorMsg = String.format("接口编号不能为空（字段代码：%s）", field.getFieldCode());
                log.error(errorMsg);
                return errorMsg;
            }

            // 校验 fieldCode 唯一性（核心校验）
            if (!fieldCodeSet.add(field.getFieldCode())) {
                String errorMsg = String.format("存在重复的字段代码：%s（字段名称：%s）。提示：如果需要使用来自不同接口的同名字段，请为字段代码添加唯一后缀，例如：caseNumberYearBefore_1、caseNumberYearBefore_2",
                        field.getFieldCode(), field.getFieldName());
                log.error(errorMsg);
                return errorMsg;
            }

            // 校验 fullPath 唯一性（避免重复选择同一字段）
            if (!fullPathSet.add(field.getFullPath())) {
                String errorMsg = String.format("存在重复的字段路径：%s，请勿重复选择同一个字段", field.getFullPath());
                log.error(errorMsg);
                return errorMsg;
            }

            // 校验字段路径必须是三层（数据源/接口/字段）
            String fullPath = field.getFullPath();
            long pathLevel = fullPath.chars().filter(ch -> ch == '/').count() + 1;
            if (pathLevel != 3) {
                String errorMsg = String.format(
                        "字段路径层级不正确：'%s'（当前层级：%d）。公式计算需要完整的三层字段路径，格式：数据源/接口/字段名。请重新选择到具体字段层",
                        fullPath, pathLevel
                );
                log.error(errorMsg);
                return errorMsg;
            }
        }

        // 4. 校验公式中使用的字段是否都已配置（警告，不阻断）
        String rawFormula = formulaDTO.getRawFormula();
        for (StandardQuotaFormulaDTO.FormulaField field : formulaDTO.getFormulaFields()) {
            if (!rawFormula.contains(field.getFullPath())) {
                log.warn("公式中未使用字段: {}", field.getFullPath());
            }
        }

        // 5. 校验可执行公式语法（转换后用测试数据验证）
        try {
            String executableFormula = generateExecutableFormula(formulaDTO.getRawFormula(), formulaDTO.getFormulaFields());

            // 5.1 检查非法运算符组合（在替换字段代码前检查）
            String operatorError = checkInvalidOperators(executableFormula);
            if (operatorError != null) {
                log.error("公式包含非法运算符组合: {}, executableFormula={}", operatorError, executableFormula);
                return operatorError;
            }

            // 5.2 替换字段代码为测试数值（所有字段用 1 代替，用于语法校验）
            // 原因：POI 需要真实数值才能判断语法是否正确
            // 例如：MAX(accountBalance, minBalance) → MAX(1, 1)
            String testFormula = executableFormula;
            for (StandardQuotaFormulaDTO.FormulaField field : formulaDTO.getFormulaFields()) {
                testFormula = testFormula.replace(field.getFieldCode(), "1");
            }

            // 5.3 使用 POI 校验公式语法，返回详细错误信息
            String validationError = FormulaUtil.validateFormulaWithDetail(testFormula);
            if (validationError != null) {
                log.error("公式语法错误: {}, executableFormula={}, testFormula={}", validationError, executableFormula, testFormula);
                return validationError;
            }

            log.info("公式语法校验通过, executableFormula={}, testFormula={}", executableFormula, testFormula);

        } catch (Exception e) {
            String errorMsg = String.format("公式转换或校验失败：%s。原始公式：%s", e.getMessage(), formulaDTO.getRawFormula());
            log.error(errorMsg, e);
            return errorMsg;
        }

        // 所有校验通过
        return null;
    }

    /**
     * 检查公式中是否包含非法的运算符组合
     *
     * 常见错误：
     * - /  - (除号后紧跟减号，应该用括号：/ (-xxx) 或者去掉空格)
     * - *  + (乘号后紧跟加号)
     * - /  + (除号后紧跟加号)
     * - +- , -+, *除, 除* 等连续运算符
     *
     * @param formula 公式
     * @return null=合法，非null=错误信息
     */
    private String checkInvalidOperators(String formula) {
        // 去除字符串内容（避免误判字符串中的运算符）
        String cleanFormula = formula.replaceAll("\"[^\"]*\"", "\"\"");

        // 将中文括号替换为英文括号（统一处理）
        cleanFormula = cleanFormula.replace("（", "(").replace("）", ")");

        // 检查常见的非法运算符组合
        if (cleanFormula.matches(".*[/]\\s*[-]\\s+.*")) {
            return "公式包含非法运算符组合 '/  -'。如果要除以负数，请使用括号，例如：/ (-2000) 或 / (0-变量)";
        }
        if (cleanFormula.matches(".*[*]\\s*[+]\\s+.*")) {
            return "公式包含非法运算符组合 '*  +'。请检查运算符使用是否正确";
        }
        if (cleanFormula.matches(".*[/]\\s*[+]\\s+.*")) {
            return "公式包含非法运算符组合 '/  +'。请检查运算符使用是否正确";
        }

        // 检查连续运算符（不含空格）
        if (cleanFormula.matches(".*[+\\-*/]{2,}.*")) {
            // 排除负号的情况（例如：5 + -3 或 (  -3) 或 （-3））
            if (!cleanFormula.matches(".*[+\\-*/]\\s*[-]\\s*\\d.*") &&
                    !cleanFormula.matches(".*[(（]\\s*[-].*")) {
                return "公式包含连续的运算符，请检查公式是否正确。如果要使用负数，请用括号包裹，例如：(0-变量) 或 (-100)";
            }
        }

        // 检查运算符在不合适的位置（支持中文括号）
        if (cleanFormula.matches(".*[+\\-*/]\\s*[,)）].*")) {
            return "运算符后面不能直接跟逗号或右括号，请检查公式";
        }
        if (cleanFormula.matches(".*[(（,]\\s*[*/].*")) {
            return "左括号或逗号后面不能直接跟乘号或除号，请检查公式";
        }

        // 检查逗号后直接跟右括号（函数参数缺失）
        if (cleanFormula.matches(".*,\\s*[)）].*")) {
            return "函数参数不完整，逗号后面不能直接跟右括号（例如：MIN(字段,) 应该改为 MIN(字段, 10)）";
        }

        // 检查左括号后直接跟逗号（函数参数缺失）
        if (cleanFormula.matches(".*[(（]\\s*,.*")) {
            return "函数参数不完整，左括号后面不能直接跟逗号（例如：MAX(,字段) 应该改为 MAX(10, 字段)）";
        }

        return null;
    }

    // ==================== 标准额度公式计算实现 ====================

    /**
     * 从 data_calling 表中提取公式需要的字段值
     *
     * 实现逻辑：
     * 1. 查询 data_calling 表，条件: orderNo = taskNo
     * 2. 解析 formulaVariables 获取需要的字段信息（fieldCode, fieldManageNo）
     * 3. 遍历 data_calling 记录，匹配 fieldManageNo = manageNo
     * 4. 从 responseBody.data 中提取 fieldCode 对应的值
     * 5. 返回 Map<fieldCode, fieldValue>
     *
     * @param quotaCardRecord 额度卡配置快照
     * @param taskNo 任务编号
     * @return 字段值映射 Map<fieldCode, fieldValue>
     */
    @Override
    public Map<String, Object> extractFieldValuesFromDataCalling(
            QuotaCardRecordSnapshot quotaCardRecord,
            String taskNo
    ) {
        Map<String, Object> fieldValues = new HashMap<>();

        try {
            // 1. 解析 formulaVariables 获取字段信息
            String formulaVariablesJson = quotaCardRecord.getFormulaVariables();
            if (StrUtil.isBlank(formulaVariablesJson)) {
                log.warn("额度卡公式变量为空, quotaCardId={}, taskNo={}", quotaCardRecord.getId(), taskNo);
                return fieldValues;
            }

            Map<String, Map<String, Object>> variablesMap = JSON.parseObject(
                    formulaVariablesJson,
                    new TypeReference<Map<String, Map<String, Object>>>() {}
            );

            // 2. 查询 data_calling 表
            LambdaQueryWrapper<DataCalling> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(DataCalling::getOrderNo, taskNo);
            queryWrapper.eq(DataCalling::getCallStatus, "查得");  // 只查询成功的调用
            List<DataCalling> dataCallingList = dataCallingMapper.selectList(queryWrapper);

            if (dataCallingList == null || dataCallingList.isEmpty()) {
                log.warn("未找到中台调用数据, taskNo={}", taskNo);
                return fieldValues;
            }

            log.info("查询到 {} 条中台调用数据, taskNo={}", dataCallingList.size(), taskNo);

            // 3. 遍历字段变量，匹配 data_calling 数据
            for (Map.Entry<String, Map<String, Object>> entry : variablesMap.entrySet()) {
                String fieldCode = entry.getKey();
                Map<String, Object> fieldInfo = entry.getValue();
                String manageNo = (String) fieldInfo.get("manageNo");

                if (StrUtil.isBlank(manageNo)) {
                    log.warn("接口编号为空, fieldCode={}", fieldCode);
                    continue;
                }

                // 4. 在 data_calling 记录中查找匹配的 manageNo
                for (DataCalling dataCalling : dataCallingList) {
                    if (manageNo.equals(dataCalling.getManageNo())) {
                        // 5. 解析 responseBody，提取字段值
                        String responseBody = dataCalling.getResponseBody();
                        if (StrUtil.isNotBlank(responseBody)) {
                            try {
                                Map<String, Object> responseMap = JSON.parseObject(responseBody, Map.class);
                                Object dataObject = responseMap.get("data");

                                if (dataObject != null) {
                                    // 处理 data 为数组的情况（取第一条记录）
                                    if (dataObject instanceof List) {
                                        List<Map<String, Object>> dataList = (List<Map<String, Object>>) dataObject;
                                        if (!dataList.isEmpty()) {
                                            Map<String, Object> firstRecord = dataList.get(0);
                                            if (firstRecord.containsKey(fieldCode)) {
                                                Object fieldValue = firstRecord.get(fieldCode);
                                                fieldValues.put(fieldCode, fieldValue);
                                            }
                                        } else {
                                            log.warn("responseBody.data 数组为空: fieldCode={}, manageNo={}",
                                                    fieldCode, manageNo);
                                        }
                                    }
                                    // 处理 data 为对象的情况
                                    else if (dataObject instanceof Map) {
                                        Map<String, Object> dataMap = (Map<String, Object>) dataObject;
                                        if (dataMap.containsKey(fieldCode)) {
                                            Object fieldValue = dataMap.get(fieldCode);
                                            fieldValues.put(fieldCode, fieldValue);
                                        }
                                    }
                                } else {
                                    log.warn("responseBody.data 为 null: fieldCode={}, manageNo={}",
                                            fieldCode, manageNo);
                                }
                            } catch (Exception e) {
                                log.error("解析 responseBody 失败: manageNo={}, error={}",
                                        manageNo, e.getMessage(), e);
                            }
                        }
                        break;  // 找到匹配的 manageNo，跳出循环
                    }
                }
            }
        } catch (Exception e) {
            log.error("从 data_calling 表提取字段值失败, quotaCardId={}, taskNo={}, error={}",
                    quotaCardRecord.getId(), taskNo, e.getMessage(), e);
        }

        return fieldValues;
    }

    /**
     * 使用配置的公式计算标准额度
     *
     * 实现逻辑：
     * 1. 获取 standardQuotaFormula（可执行公式，如 "caseNumberYearBefore*100+financial*500"）
     * 2. 替换公式中的字段代码为实际值
     * 3. 使用 FormulaUtil.evaluateFormula() 计算公式
     * 4. 返回计算结果（BigDecimal）
     *
     * @param quotaCardRecord 额度卡配置快照
     * @param fieldValues 字段实际值 Map<fieldCode, fieldValue>
     * @return 计算后的标准额度
     */
    @Override
    public BigDecimal calculateStandardQuotaByFormula(
            QuotaCardRecordSnapshot quotaCardRecord,
            Map<String, Object> fieldValues
    ) {
        try {
            // 1. 获取可执行公式
            String formula = quotaCardRecord.getStandardQuotaFormula();
            if (StrUtil.isBlank(formula)) {
                log.error("标准额度公式为空, quotaCardId={}", quotaCardRecord.getId());
                return BigDecimal.ZERO;
            }

            log.info("开始计算标准额度, quotaCardId={}, formula={}, fieldValues={}",
                    quotaCardRecord.getId(), formula, fieldValues);

            // 2. 替换公式中的字段代码为实际值
            String executableFormula = formula;
            for (Map.Entry<String, Object> entry : fieldValues.entrySet()) {
                String fieldCode = entry.getKey();
                Object fieldValue = entry.getValue();

                // 将字段值转换为字符串（数值型直接转，字符型需加引号）
                String valueStr;
                if (fieldValue == null) {
                    // null 值转为 0
                    valueStr = "0";
                    log.debug("字段 {} 的值为 null，转换为 0", fieldCode);
                } else if (fieldValue instanceof Number) {
                    valueStr = fieldValue.toString();
                } else if (fieldValue instanceof Boolean) {
                    // 布尔型转为 1/0
                    valueStr = ((Boolean) fieldValue) ? "1" : "0";
                } else {
                    // 字符型加引号
                    valueStr = "\"" + fieldValue + "\"";
                }

                // 替换字段代码为实际值
                executableFormula = executableFormula.replace(fieldCode, valueStr);
            }

            // 3. 使用 FormulaUtil 计算公式
            BigDecimal result = BigDecimal.valueOf(FormulaUtil.evaluateFormula(executableFormula));

            log.info("标准额度计算完成, quotaCardId={}, result={}", quotaCardRecord.getId(), result);

            return result;

        } catch (Exception e) {
            log.error("计算标准额度失败, quotaCardId={}, error={}",
                    quotaCardRecord.getId(), e.getMessage(), e);
            return BigDecimal.ZERO;
        }
    }
}
