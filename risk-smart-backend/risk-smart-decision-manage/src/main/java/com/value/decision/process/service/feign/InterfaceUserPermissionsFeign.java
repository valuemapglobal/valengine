package com.value.decision.process.service.feign;

import com.risksmart.common.core.domain.R;
import com.value.decision.process.vo.InterfaceUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(contextId = "interfaceUserPermissionsFeign", value = "risk-smart-data-middle-station", path = "/interfaceUserPermissions")
public interface InterfaceUserPermissionsFeign {

    @GetMapping("/queryAppkeyByUserId")
    R<InterfaceUser> queryAppKeyByUserId(@RequestParam("userId") Integer userId);
}
