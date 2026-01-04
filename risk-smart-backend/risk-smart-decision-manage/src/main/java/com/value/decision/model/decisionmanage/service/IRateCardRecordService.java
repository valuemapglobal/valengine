package com.value.decision.model.decisionmanage.service;

import com.value.decision.model.decisionmanage.model.RateCardRecord;
import com.risksmart.common.core.web.AjaxResult;
import com.value.decision.common.security.LoginUser;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 评级卡主表 服务类
 * </p>
 *
 * @author hc
 * @since 2023-08-10
 */
public interface IRateCardRecordService extends IService<RateCardRecord> {

    AjaxResult updateStatus(RateCardRecord rateCardRecord);

    AjaxResult newList(RateCardRecord rateCardRecord, LoginUser loginUser);

    AjaxResult release(RateCardRecord rateCardRecord, LoginUser loginUser);

}
