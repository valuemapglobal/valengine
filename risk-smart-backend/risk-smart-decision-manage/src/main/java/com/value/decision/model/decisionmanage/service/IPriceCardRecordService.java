package com.value.decision.model.decisionmanage.service;

import com.value.decision.model.decisionmanage.model.PriceCardRecord;
import com.risksmart.common.core.web.AjaxResult;
import com.value.decision.common.security.LoginUser;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 定价卡主表 服务类
 * </p>
 *
 * @author hc
 * @since 2023-08-12
 */
public interface IPriceCardRecordService extends IService<PriceCardRecord> {

    AjaxResult newList(PriceCardRecord priceCardRecord,LoginUser loginUser);

    AjaxResult updateStatus(PriceCardRecord priceCardRecord);

    AjaxResult release(PriceCardRecord priceCardRecord, LoginUser loginUser);

}
