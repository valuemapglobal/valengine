/**
 * 开放接口模块 - SPI插件机制
 *
 * <h2>概述</h2>
 * 本模块提供了数据源适配器的SPI（Service Provider Interface）机制，
 * 允许开源用户通过实现标准接口来对接自己的数据源，无需修改核心代码。
 *
 * <h2>核心组件</h2>
 * <ul>
 *   <li>{@link com.value.data.openapi.spi.DataSourceAdapter} - 数据源适配器接口，用户需要实现此接口</li>
 *   <li>{@link com.value.data.openapi.spi.DataSourceContext} - 调用上下文，包含用户信息、订单号等</li>
 *   <li>{@link com.value.data.openapi.spi.DataSourceAdapterManager} - 适配器管理器，自动发现和管理所有适配器</li>
 *   <li>{@link com.value.data.openapi.controller.OpenApiController} - 开放接口控制器，提供统一的API入口</li>
 * </ul>
 *
 * <h2>快速开始</h2>
 * <pre>
 * // 1. 实现DataSourceAdapter接口
 * {@literal @}Component
 * public class MyDataSourceAdapter implements DataSourceAdapter {
 *
 *     {@literal @}Override
 *     public String getName() {
 *         return "my-adapter";
 *     }
 *
 *     {@literal @}Override
 *     public JSONObject invoke(String apiName, JSONObject param, DataSourceContext context) {
 *         // 实现你的数据源调用逻辑
 *         // 例如：调用第三方API、查询数据库等
 *         return result;
 *     }
 * }
 *
 * // 2. 调用接口
 * // POST /openApi/v2/invoke/{apiName}
 * // 或指定适配器：POST /openApi/v2/invoke/by/{adapterName}/{apiName}
 * </pre>
 *
 * <h2>内置适配器</h2>
 * <ul>
 *   <li>{@link com.value.data.openapi.adapter.SampleDataSourceAdapter} - 示例适配器，仅供参考</li>
 *   <li>{@link com.value.data.openapi.adapter.HttpDataSourceAdapter} - HTTP适配器，支持配置化对接RESTful API</li>
 * </ul>
 *
 * <h2>配置说明</h2>
 * <pre>
 * # 启用示例适配器
 * openapi.sample.enabled=true
 *
 * # 启用HTTP适配器
 * openapi:
 *   http:
 *     enabled: true
 *     base-url: https://api.example.com
 *     timeout: 30000
 *     headers:
 *       Authorization: Bearer token
 *     api-mappings:
 *       getInfo: /api/info
 * </pre>
 *
 * @author RiskSmart
 * @since 1.0.0
 */
package com.value.data.openapi;
