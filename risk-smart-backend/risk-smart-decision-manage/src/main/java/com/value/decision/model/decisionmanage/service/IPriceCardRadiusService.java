package com.value.decision.model.decisionmanage.service;

import com.value.decision.model.decisionmanage.model.PriceCardRadius;
import com.risksmart.common.core.web.AjaxResult;
import com.value.decision.common.security.LoginUser;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 定价详情表 服务类
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
public interface IPriceCardRadiusService extends IService<PriceCardRadius> {

    AjaxResult submit(PriceCardRadius priceCardRadius, LoginUser loginUser);

    AjaxResult get(PriceCardRadius priceCardRadius);

}
