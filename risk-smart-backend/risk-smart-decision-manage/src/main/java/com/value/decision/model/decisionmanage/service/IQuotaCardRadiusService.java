package com.value.decision.model.decisionmanage.service;

import com.value.decision.model.decisionmanage.model.QuotaCardRadius;
import com.risksmart.common.core.web.AjaxResult;
import com.value.decision.common.security.LoginUser;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 额度范围表 服务类
 * </p>
 *
 * @author hc
 * @since 2023-08-12
 */
public interface IQuotaCardRadiusService extends IService<QuotaCardRadius> {

    AjaxResult submit(QuotaCardRadius quotaCardRadius, LoginUser loginUser);

    AjaxResult get(QuotaCardRadius quotaCardRadius, LoginUser loginUser);

}
