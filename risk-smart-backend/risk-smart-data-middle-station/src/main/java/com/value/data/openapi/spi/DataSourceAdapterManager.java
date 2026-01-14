package com.value.data.openapi.spi;

import com.alibaba.fastjson2.JSONObject;
import com.risksmart.common.core.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 数据源适配器管理器
 * 管理所有注册的数据源适配器，并提供统一的调用入口
 *
 * @author RiskSmart
 */
@Slf4j
@Component
public class DataSourceAdapterManager {

    private final Map<String, DataSourceAdapter> adapterMap = new ConcurrentHashMap<>();
    private List<DataSourceAdapter> sortedAdapters;

    @Autowired(required = false)
    public void setAdapters(List<DataSourceAdapter> adapters) {
        if (adapters != null && !adapters.isEmpty()) {
            adapters.forEach(adapter -> {
                adapterMap.put(adapter.getName(), adapter);
                log.info("注册数据源适配器: {} - {}", adapter.getName(), adapter.getDescription());
            });
            // 按优先级排序
            sortedAdapters = adapters.stream()
                    .sorted(Comparator.comparingInt(DataSourceAdapter::getOrder))
                    .collect(Collectors.toList());
        }
    }

    /**
     * 根据名称获取适配器
     *
     * @param name 适配器名称
     * @return 适配器实例
     */
    public DataSourceAdapter getAdapter(String name) {
        return adapterMap.get(name);
    }

    /**
     * 获取所有适配器
     *
     * @return 适配器列表
     */
    public List<DataSourceAdapter> getAllAdapters() {
        return sortedAdapters;
    }

    /**
     * 查找支持指定接口的适配器
     *
     * @param apiName 接口名称
     * @return 适配器（按优先级返回第一个支持的）
     */
    public Optional<DataSourceAdapter> findAdapter(String apiName) {
        if (sortedAdapters == null) {
            return Optional.empty();
        }
        return sortedAdapters.stream()
                .filter(adapter -> adapter.supports(apiName))
                .findFirst();
    }

    /**
     * 调用数据源接口
     *
     * @param adapterName 适配器名称（可选，为空则自动查找）
     * @param apiName     接口名称
     * @param param       请求参数
     * @param context     上下文
     * @return 响应数据
     */
    public JSONObject invoke(String adapterName, String apiName, JSONObject param, DataSourceContext context) {
        DataSourceAdapter adapter;

        if (adapterName != null && !adapterName.isEmpty()) {
            adapter = getAdapter(adapterName);
            if (adapter == null) {
                throw new ServiceException("未找到数据源适配器: " + adapterName);
            }
        } else {
            adapter = findAdapter(apiName)
                    .orElseThrow(() -> new ServiceException("未找到支持接口[" + apiName + "]的数据源适配器"));
        }

        try {
            log.info("调用数据源适配器: {}, 接口: {}, 订单: {}", adapter.getName(), apiName, context.getOrderId());
            JSONObject result = adapter.invoke(apiName, param, context);
            log.info("数据源适配器调用成功: {}, 接口: {}", adapter.getName(), apiName);
            return result;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("数据源适配器调用异常: {}, 接口: {}", adapter.getName(), apiName, e);
            throw new ServiceException("数据源调用失败: " + e.getMessage());
        }
    }

    /**
     * 检查是否有可用的适配器
     */
    public boolean hasAdapters() {
        return sortedAdapters != null && !sortedAdapters.isEmpty();
    }
}
