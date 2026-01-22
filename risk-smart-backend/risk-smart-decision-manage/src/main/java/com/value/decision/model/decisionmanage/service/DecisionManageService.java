package com.value.decision.model.decisionmanage.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.risksmart.common.core.exception.ServiceException;
import com.value.decision.model.decisionmanage.model.dto.ImportRuleWithJsonDTO;
import com.value.decision.common.security.LoginUser;

import java.io.IOException;
import java.nio.file.Path;

/**
 * 决策管理服务接口
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
public interface DecisionManageService {
    /**
     * 根据策略的id，将策略、规则组、规则数据以json格式返回
     * 结果会包含关联关系
     * @param strategyId 规则id
     * @return 策略、规则组、规则json数据
     * @throws JsonProcessingException 序列化json时出现异常
     */
    String getRuleJson(Integer strategyId) throws JsonProcessingException;

    /**
     * 根据策略的id，将策略、规则组、规则数据导出到excel文件
     * @param strategyId 规则id
     * @param excel excel文件。文件不存在时自动创建。
     * @return 成功导入的规则数
     * @throws IOException 文件创建时出现异常
     * @throws ServiceException 导出到excel的过程中出现异常
     */

    int exportRuleToExcel(Integer strategyId, Path excel);

    /**
     * 导入策略
     * @param params 导入策略所需的信息
     * @return 成功导入的规则数，如果为负数则表示失败
     *          -1 文件读取失败
     *          -2 不是json
     *          -3 json序列化失败
     *          -4 无策略需要导入
     */
    int importRuleByJson(ImportRuleWithJsonDTO params, LoginUser loginUser);
}
