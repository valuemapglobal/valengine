package com.value.decision.model.decisionmanage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.value.decision.model.decisionmanage.model.Business;
import com.value.decision.common.security.LoginUser;

import java.util.List;

/**
 * 业务场景表服务类
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
public interface BusinessService extends IService<Business> {
    List<Business> queryList(Business business, LoginUser loginUser);

    List<Business> standardList();
}
