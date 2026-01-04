package com.value.decision.model.decisionmanage.service;

import com.value.decision.model.decisionmanage.model.QuotaCardRecord;
import com.value.decision.model.decisionmanage.model.dto.StandardQuotaFormulaDTO;
import com.risksmart.common.core.web.AjaxResult;
import com.value.decision.common.security.LoginUser;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 额度卡主表 服务类
 * </p>
 *
 * @author hc
 * @since 2023-08-12
 */
public interface IQuotaCardRecordService extends IService<QuotaCardRecord> {

    AjaxResult newList(QuotaCardRecord quotaCardRecord,LoginUser loginUser);

    AjaxResult updateStatus(QuotaCardRecord quotaCardRecord);

    AjaxResult release(QuotaCardRecord quotaCardRecord, LoginUser loginUser);

    /**
     * 保存标准额度计算公式配置
     *
     * @param formulaDTO 公式配置数据
     * @param loginUser 当前登录用户
     * @return 保存结果
     */
    AjaxResult saveStandardQuotaFormula(StandardQuotaFormulaDTO formulaDTO, LoginUser loginUser);

    /**
     * 获取标准额度计算公式配置
     *
     * @param quotaCardId 额度卡ID
     * @param loginUser 当前登录用户
     * @return 公式配置数据
     */
    AjaxResult getStandardQuotaFormula(Integer quotaCardId, LoginUser loginUser);

    /**
     * 从 data_calling 表中提取公式需要的字段值
     *
     * @param quotaCardRecord 额度卡配置
     * @param taskNo 任务编号
     * @return 字段值映射 Map<fieldCode, fieldValue>
     */
    java.util.Map<String, Object> extractFieldValuesFromDataCalling(
            com.value.decision.model.decisionmanage.model.QuotaCardRecordSnapshot quotaCardRecord,
            String taskNo
    );

    /**
     * 使用配置的公式计算标准额度
     *
     * @param quotaCardRecord 额度卡配置
     * @param fieldValues 字段实际值
     * @return 计算后的标准额度
     */
    java.math.BigDecimal calculateStandardQuotaByFormula(
            com.value.decision.model.decisionmanage.model.QuotaCardRecordSnapshot quotaCardRecord,
            java.util.Map<String, Object> fieldValues
    );

}
