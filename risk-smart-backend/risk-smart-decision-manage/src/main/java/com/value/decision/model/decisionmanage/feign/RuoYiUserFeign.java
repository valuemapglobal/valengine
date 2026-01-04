package com.value.decision.model.decisionmanage.feign;

import com.value.decision.common.constant.SecurityConstants;
import com.value.decision.common.domain.R;
import com.risksmart.system.domain.SysUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(contextId = "ruoYiUserFeign", value = "risk-smart-system", fallbackFactory = RuoYiUserFeignFallbackFactory.class)
public interface RuoYiUserFeign {
    @PostMapping("/user/selectOne")
    R<SysUser> selectOne(@RequestBody SysUser user, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);
}
