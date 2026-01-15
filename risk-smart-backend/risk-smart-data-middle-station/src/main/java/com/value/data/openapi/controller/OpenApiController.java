package com.value.data.openapi.controller;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson2.JSONObject;
import com.risksmart.common.core.utils.StringUtils;
import com.value.data.common.model.LoginUser;
import com.value.data.common.utils.AjaxResult;
import com.value.data.common.utils.SecurityUtils;
import com.value.data.openapi.spi.DataSourceAdapter;
import com.value.data.openapi.spi.DataSourceAdapterManager;
import com.value.data.openapi.spi.DataSourceContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 开放接口控制器（SPI机制）
 * 通过数据源适配器SPI机制，支持用户自定义数据源对接
 *
 * @author RiskSmart
 */
@RestController
@RequestMapping("/openApi/v2")
@Slf4j
@AllArgsConstructor
public class OpenApiController {

    private final DataSourceAdapterManager adapterManager;

    /**
     * 获取所有可用的数据源适配器列表
     */
    @GetMapping("/adapters")
    public AjaxResult listAdapters() {
        List<DataSourceAdapter> adapters = adapterManager.getAllAdapters();
        if (adapters == null || adapters.isEmpty()) {
            return AjaxResult.success("暂无可用的数据源适配器", List.of());
        }
        List<Map<String, Object>> result = adapters.stream().map(adapter -> {
            Map<String, Object> map = new HashMap<>();
            map.put("name", adapter.getName());
            map.put("description", adapter.getDescription());
            map.put("order", adapter.getOrder());
            return map;
        }).collect(Collectors.toList());
        return AjaxResult.success(result);
    }

    /**
     * 调用数据源接口（自动选择适配器）
     *
     * @param apiName    接口名称/代号
     * @param returnName 返回值参数名（可选，用于从data中提取指定字段）
     * @param param      请求参数
     * @param request    HTTP请求
     * @return 接口响应
     */
    @PostMapping("/invoke/{apiName}")
    public AjaxResult invoke(@PathVariable String apiName,
                             @RequestParam(required = false) String returnName,
                             @RequestBody(required = false) JSONObject param,
                             HttpServletRequest request) {
        return doInvoke(null, apiName, returnName, param, request);
    }

    /**
     * 调用指定适配器的数据源接口
     *
     * @param adapterName 适配器名称
     * @param apiName     接口名称/代号
     * @param returnName  返回值参数名（可选）
     * @param param       请求参数
     * @param request     HTTP请求
     * @return 接口响应
     */
    @PostMapping("/invoke/by/{adapterName}/{apiName}")
    public AjaxResult invokeWithAdapter(@PathVariable String adapterName,
                                        @PathVariable String apiName,
                                        @RequestParam(required = false) String returnName,
                                        @RequestBody(required = false) JSONObject param,
                                        HttpServletRequest request) {
        return doInvoke(adapterName, apiName, returnName, param, request);
    }

    /**
     * 执行调用
     */
    private AjaxResult doInvoke(String adapterName, String apiName, String returnName,
                                JSONObject param, HttpServletRequest request) {
        if (StringUtils.isBlank(apiName)) {
            return AjaxResult.error("接口名称不能为空");
        }

        if (!adapterManager.hasAdapters()) {
            return AjaxResult.error("暂无可用的数据源适配器，请先实现DataSourceAdapter接口");
        }

        if (param == null) {
            param = new JSONObject();
        }

        // 构建上下文
        DataSourceContext context = buildContext(param, request);

        // 调用适配器
        JSONObject result = adapterManager.invoke(adapterName, apiName, param, context);

        // 处理返回值
        if (StringUtils.isNotBlank(returnName) && result != null) {
            Object data = result.get("data");
            if (data instanceof JSONObject) {
                return AjaxResult.success(((JSONObject) data).get(returnName));
            }
            return AjaxResult.success(data);
        }

        return AjaxResult.success(result != null ? result.get("data") : null);
    }

    /**
     * 构建调用上下文
     */
    private DataSourceContext buildContext(JSONObject param, HttpServletRequest request) {
        DataSourceContext.DataSourceContextBuilder builder = DataSourceContext.builder();

        // 获取订单号
        String orderId = param.getString("orderId");
        if (StringUtils.isBlank(orderId)) {
            orderId = param.getString("orderNo");
        }
        if (StringUtils.isBlank(orderId)) {
            orderId = IdUtil.simpleUUID();
        }
        builder.orderId(orderId);

        // 获取用户信息
        try {
            LoginUser loginUser = SecurityUtils.getLoginUser(request);
            if (loginUser != null) {
                builder.userId(loginUser.getUserid());
                builder.userName(loginUser.getUsername());
                if (loginUser.getSysUser() != null) {
                    builder.deptId(loginUser.getSysUser().getDeptId());
                }
            }
        } catch (Exception e) {
            log.debug("获取登录用户信息失败: {}", e.getMessage());
        }

        // 请求IP
        builder.requestIp(getClientIp(request));

        return builder.build();
    }

    /**
     * 获取客户端IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
