package com.value.decision.model.decisionmanage.service;

import com.risksmart.system.domain.SysUser;

import java.util.List;

/**
 * 若依服务接口
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
public interface RuoYiService {
    List<Long> getStandardDepts();
    SysUser getUserById(Long id);
}
