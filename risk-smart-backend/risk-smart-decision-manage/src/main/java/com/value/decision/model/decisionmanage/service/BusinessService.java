package com.value.decision.model.decisionmanage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.value.decision.model.decisionmanage.model.Business;
import com.value.decision.common.security.LoginUser;

import java.util.List;

/**
 * <p>
 * 业务场景表 服务类
 * </p>
 *
 * @author Vida
 * @since 2024-11-07
 */
public interface BusinessService extends IService<Business> {
    List<Business> queryList(Business business, LoginUser loginUser);

    List<Business> standardList();
}
