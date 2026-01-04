package com.value.decision.model.decisionmanage.service;

import com.risksmart.system.domain.SysUser;

import java.util.List;

/**
 * @author Vida
 * @date 2024年11月14日 10:45
 * @description
 */
public interface RuoYiService {
    List<Long> getStandardDepts();
    SysUser getUserById(Long id);
}
