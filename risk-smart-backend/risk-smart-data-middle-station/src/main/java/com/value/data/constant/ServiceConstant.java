package com.value.data.constant;

import com.value.data.common.service.IdGeneratorService;
import com.value.data.common.service.RedisService;
import com.value.data.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * 统一注入管理
 */
@Component
@RequiredArgsConstructor
public class ServiceConstant {

    public final Environment env;

    public final IdGeneratorService idGeneratorService;

    public final RedisService redisService;

    /**
     * service 注入模板
     */
    /**
     * 元数据主题服务类
     */
    public final RdeRiskVariableThemeService rdeRiskVariableThemeService;
    /**
     * 元数据分组服务
     */
    public final RdeRiskVariableGroupService rdeRiskVariableGroupService;
    /**
     * 元数据属性服务
     */
    public final RdeRiskVariableRecordService rdeRiskVariableRecordService;

    /**
     * 接口日志表 服务类
     */
    public final InterfaceLogService interfaceLogService;

    /**
     * 接口管理表 服务类
     */
    public final InterfaceManageService interfaceManageService;

    /**
     * 接口供应商管理表 服务类
     */
    public final InterfaceSourceManageService interfaceSourceManageService;

    /**
     * 接口参数管理表 服务类
     */
    public final InterfaceFieldIdManageService interfaceFieldIdManageService;

    /**
     * 接口权限管理表 服务类
     */
    public final InterfacePermissionsManageService interfacePermissionsManageService;

    public final InterfaceUserService interfaceUserService;

    /**
     * 部门关联appkey 服务类
     */
    public final InterfaceDeptAppService interfaceDeptAppService;
}
