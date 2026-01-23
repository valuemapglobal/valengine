package com.value.decision.model.decisionmanage.service;

import com.value.decision.model.decisionmanage.model.RateCardRadius;
import com.risksmart.common.core.web.AjaxResult;
import com.value.decision.common.security.LoginUser;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 评级范围表 服务类
 * </p>
 *
 * @author hc
 * @since 2023-08-10
 */
public interface IRateCardRadiusService extends IService<RateCardRadius> {

    AjaxResult submit(RateCardRadius rateCardRadius, LoginUser loginUser);

    AjaxResult get(RateCardRadius rateCardRadius, LoginUser loginUser);

}
