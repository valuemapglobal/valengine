package com.value.decision.model.decisionmanage.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 任务执行失败场景枚举
 *
 * @author Austin
 * @since 2025-01-16
 */
@Getter
@AllArgsConstructor
public enum FailureSceneEnum {

    // ========== A. 外部服务异常 ==========
    EXT_DATA_TIMEOUT(
        "A1",
        "外部服务异常",
        "数据中台接口超时",
        "调用外部数据服务超时,请稍后重试",
        true
    ),
    EXT_DATA_ERROR(
        "A2",
        "外部服务异常",
        "数据中台返回错误",
        "外部数据服务返回错误,请检查提交的信息",
        false
    ),
    EXT_DATA_EMPTY(
        "A3",
        "外部服务异常",
        "数据中台返回数据为空",
        "无法找到对应的企业信息,请检查企业统一社会信用代码",
        false
    ),
    EXT_DATA_JSON_ERROR(
        "A4",
        "外部服务异常",
        "数据中台返回JSON格式异常",
        "外部数据服务返回数据格式异常,请联系技术支持",
        true
    ),
    EXT_FEIGN_ERROR(
        "A5",
        "外部服务异常",
        "Feign服务调用失败",
        "外部服务暂时不可用,请稍后重试",
        true
    ),
    ENGINE_UNAVAILABLE(
        "A6",
        "外部服务异常",
        "决策引擎服务不可用",
        "决策引擎服务暂时不可用,请稍后重试",
        true
    ),
    ENGINE_RESPONSE_INVALID(
        "A7",
        "外部服务异常",
        "决策引擎返回格式异常",
        "决策引擎返回数据格式异常,请联系技术支持",
        true
    ),

    // ========== B. 流程配置问题 ==========
    PROCESS_NO_NODES(
        "B1",
        "流程配置问题",
        "流程策略未配置节点",
        "流程策略未配置执行节点,请联系管理员",
        false
    ),
    PROCESS_NOT_FOUND(
        "B2",
        "流程配置问题",
        "流程策略不存在",
        "流程策略不存在或已停用,请联系管理员",
        false
    ),
    MODEL_NOT_FOUND(
        "B3",
        "流程配置问题",
        "模型配置缺失",
        "流程节点关联的模型不存在,请联系管理员",
        false
    ),
    INVALID_MODULE_ID(
        "B4",
        "流程配置问题",
        "流程节点模型类型无效",
        "流程节点配置的模型类型无效,请联系技术支持",
        false
    ),

    // ========== C. 模型配置问题 ==========
    MODEL_DRL_EMPTY(
        "C1",
        "模型配置问题",
        "模型规则配置为空",
        "模型规则配置为空,请联系管理员完善配置",
        false
    ),
    MODEL_DATASOURCE_EMPTY(
        "C2",
        "模型配置问题",
        "模型数据源配置为空",
        "模型数据源配置为空,请联系管理员完善配置",
        false
    ),
    MODEL_JSON_ERROR(
        "C3",
        "模型配置问题",
        "模型配置格式错误",
        "模型配置数据格式错误,请联系技术支持",
        false
    ),

    // ========== E. 提交信息有误 ==========
    INPUT_DUPLICATE(
        "E1",
        "提交信息有误",
        "订单号重复提交",
        "订单号重复,请检查是否已提交过",
        false
    ),
    INPUT_MISSING(
        "E2",
        "提交信息有误",
        "必填参数缺失",
        "提交的数据缺少必填字段,请检查输入",
        false
    ),
    INPUT_INVALID(
        "E3",
        "提交信息有误",
        "参数格式错误",
        "提交的数据格式不正确,请检查",
        false
    ),

    // ========== F. 系统内部异常 ==========
    SYS_DB_ERROR(
        "F1",
        "系统内部异常",
        "数据库连接异常",
        "系统数据库服务暂时不可用,请稍后重试",
        true
    ),
    SYS_REDIS_ERROR(
        "F2",
        "系统内部异常",
        "Redis连接异常",
        "系统缓存服务暂时不可用,请稍后重试",
        true
    ),
    SYS_NPE(
        "F3",
        "系统内部异常",
        "空指针异常",
        "系统内部发生错误,已通知技术团队处理",
        false
    ),
    SYS_RESOURCE_EXHAUSTED(
        "F4",
        "系统内部异常",
        "系统资源耗尽",
        "系统负载过高,请稍后重试",
        true
    ),
    SYS_UNKNOWN(
        "F9",
        "系统内部异常",
        "未知异常",
        "系统处理异常,请联系技术支持",
        false
    );

    /**
     * 场景编码 (用于统计)
     */
    private final String sceneCode;

    /**
     * 失败大类 (用户可见)
     */
    private final String failureCategory;

    /**
     * 失败场景 (开发可见)
     */
    private final String failureScene;

    /**
     * 用户友好提示消息
     */
    private final String userMessage;

    /**
     * 是否可重试
     */
    private final boolean retryable;

    /**
     * 根据场景编码获取枚举
     *
     * @param sceneCode 场景编码
     * @return 失败场景枚举
     */
    public static FailureSceneEnum getByCode(String sceneCode) {
        for (FailureSceneEnum e : values()) {
            if (e.getSceneCode().equals(sceneCode)) {
                return e;
            }
        }
        return SYS_UNKNOWN;
    }

    /**
     * 根据异常自动推断失败场景
     *
     * @param e 异常对象
     * @return 失败场景枚举
     */
    public static FailureSceneEnum inferFromException(Exception e) {
        String exceptionName = e.getClass().getName();
        String message = e.getMessage() != null ? e.getMessage() : "";

        // 收集异常链中所有的异常类型和消息(用于更准确的判断)
        StringBuilder allExceptionNames = new StringBuilder(exceptionName);
        StringBuilder allMessages = new StringBuilder(message);
        Throwable cause = e.getCause();
        while (cause != null) {
            allExceptionNames.append("|").append(cause.getClass().getName());
            if (cause.getMessage() != null) {
                allMessages.append(" ").append(cause.getMessage());
            }
            cause = cause.getCause();
        }
        String fullExceptionChain = allExceptionNames.toString();
        String fullMessage = allMessages.toString();

        // ========== 优先级1: 解析数据中台返回信息 ==========
        // 如果异常消息中包含数据中台的返回参数,尝试提取msg字段进行精确分类
        if (message.contains("数据中台") && message.contains("返还参数")) {
            String dataPlatformMsg = extractDataPlatformMessage(message);
            if (dataPlatformMsg != null && !dataPlatformMsg.isEmpty()) {
                // 根据数据中台返回的msg进行分类
                if (dataPlatformMsg.contains("无企业基本信息") || dataPlatformMsg.contains("未查得") ||
                    dataPlatformMsg.contains("不存在") || dataPlatformMsg.contains("数据为空") ||
                    dataPlatformMsg.contains("未找到") || dataPlatformMsg.contains("查无")) {
                    return EXT_DATA_EMPTY;
                }

                if (dataPlatformMsg.contains("超时") || dataPlatformMsg.contains("timeout")) {
                    return EXT_DATA_TIMEOUT;
                }

                // 其他数据中台错误归为数据中台返回错误
                return EXT_DATA_ERROR;
            }
        }

        // ========== 优先级2: 按异常类型判断(检查整个异常链) ==========

        // 1. Feign异常 (最优先判断,因为可能被包装在RuntimeException中)
        if (fullExceptionChain.contains("FeignException") ||
            fullExceptionChain.contains("feign.")) {
            // 进一步判断是否是服务不可用
            if (fullMessage.contains("Load balancer") ||
                fullMessage.contains("No instances available") ||
                fullMessage.contains("does not contain an instance") ||
                fullMessage.contains("503") ||
                fullMessage.contains("ServiceUnavailable")) {
                return EXT_FEIGN_ERROR;  // A5 - 可重试
            }
            // 其他Feign异常也归为可重试
            return EXT_FEIGN_ERROR;
        }

        // 2. 连接异常 (Connection refused, Connection reset, etc.)
        if (e instanceof java.net.ConnectException ||
            fullExceptionChain.contains("ConnectException") ||
            fullMessage.contains("Connection refused") ||
            fullMessage.contains("Connection reset") ||
            fullMessage.contains("connect timed out")) {
            // 连接被拒绝通常是服务未启动或网络问题,可重试
            if (fullMessage.contains("决策引擎") || fullMessage.contains("engine") || fullMessage.contains("rule")) {
                return ENGINE_UNAVAILABLE;  // A6 - 决策引擎不可用
            }
            return EXT_FEIGN_ERROR;  // A5 - 外部服务不可用,可重试
        }

        // 3. 超时异常
        if (e instanceof java.net.SocketTimeoutException ||
            fullExceptionChain.contains("SocketTimeoutException") ||
            fullMessage.contains("Read timed out")) {
            return EXT_DATA_TIMEOUT;
        }

        // 4. JSON解析异常
        if (e instanceof com.alibaba.fastjson.JSONException ||
            e instanceof com.fasterxml.jackson.core.JsonProcessingException) {
            if (message.contains("data") || message.contains("response") || message.contains("数据中台")) {
                return EXT_DATA_JSON_ERROR;
            }
            return MODEL_JSON_ERROR;
        }

        // 5. 数据库异常
        if (e instanceof org.springframework.dao.DataAccessException ||
            exceptionName.contains("SQLException")) {
            return SYS_DB_ERROR;
        }

        // 6. Redis异常
        if (exceptionName.contains("RedisConnectionFailureException")) {
            return SYS_REDIS_ERROR;
        }

        // 7. 重复键异常
        if (e instanceof org.springframework.dao.DuplicateKeyException) {
            return INPUT_DUPLICATE;
        }

        // 8. 空指针异常
        if (e instanceof NullPointerException) {
            return SYS_NPE;
        }

        // 9. 线程池拒绝异常
        if (e instanceof java.util.concurrent.RejectedExecutionException) {
            return SYS_RESOURCE_EXHAUSTED;
        }

        // 注意: OutOfMemoryError是Error不是Exception,无法在此处理

        // ========== 优先级3: 通用消息关键词判断(适用于所有异常类型) ==========

        // 数据为空相关
        if (fullMessage.contains("无企业基本信息") || fullMessage.contains("未查得") ||
            fullMessage.contains("不存在") || fullMessage.contains("数据为空")) {
            return EXT_DATA_EMPTY;
        }

        // 特殊判断：重试后仍失败的场景（大概率是服务不可用，可重试）
        if (fullMessage.contains("已刷新缓存重试仍失败") ||
            fullMessage.contains("刷新缓存后重试") ||
            fullMessage.contains("重试仍失败")) {
            // 这种情况通常是外部服务不可用导致的，应该可重试
            return EXT_FEIGN_ERROR;  // A5 - 可重试
        }

        // 数据中台相关(排除Feign服务不可用的情况)
        if (fullMessage.contains("数据中台") && (fullMessage.contains("接口调用失败") ||
            fullMessage.contains("返回数据格式有误"))) {
            // 排除Feign服务不可用的情况
            if (!fullMessage.contains("Load balancer") &&
                !fullMessage.contains("No instances available") &&
                !fullMessage.contains("ServiceUnavailable") &&
                !fullExceptionChain.contains("FeignException")) {
                return EXT_DATA_ERROR;
            }
        }

        // 接口调用失败（包括 DATA_MIDDLE_STATION 等英文标识）
        if ((fullMessage.contains("DATA_MIDDLE_STATION") || fullMessage.contains("数据中台")) &&
            fullMessage.contains("调用失败")) {
            // 排除Feign服务不可用的情况
            if (!fullMessage.contains("Load balancer") &&
                !fullMessage.contains("No instances available") &&
                !fullMessage.contains("ServiceUnavailable") &&
                !fullExceptionChain.contains("FeignException")) {
                return EXT_DATA_ERROR;
            }
        }

        // 接口相关错误（通用匹配）- 最后兜底,但排除Feign异常
        if (fullMessage.contains("接口") && (fullMessage.contains("调用失败") || fullMessage.contains("失败"))) {
            // 排除Feign服务不可用的情况
            if (!fullMessage.contains("Load balancer") &&
                !fullMessage.contains("No instances available") &&
                !fullMessage.contains("ServiceUnavailable") &&
                !fullExceptionChain.contains("FeignException")) {
                return EXT_DATA_ERROR;
            }
        }

        // 重复提交
        if (message.contains("重复") || message.contains("已存在")) {
            return INPUT_DUPLICATE;
        }

        // 流程配置相关
        if (message.contains("流程策略") && message.contains("节点")) {
            return PROCESS_NO_NODES;
        }
        if (message.contains("流程策略不存在") || message.contains("已停用")) {
            return PROCESS_NOT_FOUND;
        }

        // 输入参数相关
        if (message.contains("必填")) {
            return INPUT_MISSING;
        }
        if (message.contains("格式") && (message.contains("错误") || message.contains("不正确")) ||
            message.contains("invalid") || message.contains("参数错误")) {
            return INPUT_INVALID;
        }

        // 决策引擎相关
        if (message.contains("决策引擎") && (message.contains("不可用") || message.contains("超时"))) {
            return ENGINE_UNAVAILABLE;
        }

        // 兜底
        return SYS_UNKNOWN;
    }

    /**
     * 从异常消息中提取数据中台返回的msg字段
     *
     * @param exceptionMessage 异常消息
     * @return 数据中台返回的msg内容,提取失败返回null
     */
    public static String extractDataPlatformMessage(String exceptionMessage) {
        try {
            // 查找 "返还参数：" 之后的JSON部分
            int startIndex = exceptionMessage.indexOf("返还参数：");
            if (startIndex == -1) {
                startIndex = exceptionMessage.indexOf("返还参数:");
            }
            if (startIndex == -1) {
                return null;
            }

            // 从"返还参数："之后开始提取
            String jsonPart = exceptionMessage.substring(startIndex + 5).trim();

            // 找到第一个 { 和对应的 }
            int jsonStart = jsonPart.indexOf("{");
            if (jsonStart == -1) {
                return null;
            }

            // 简单的括号匹配查找JSON结束位置
            int bracketCount = 0;
            int jsonEnd = -1;
            for (int i = jsonStart; i < jsonPart.length(); i++) {
                char c = jsonPart.charAt(i);
                if (c == '{') {
                    bracketCount++;
                } else if (c == '}') {
                    bracketCount--;
                    if (bracketCount == 0) {
                        jsonEnd = i + 1;
                        break;
                    }
                }
            }

            if (jsonEnd == -1) {
                return null;
            }

            String jsonStr = jsonPart.substring(jsonStart, jsonEnd);

            // 提取msg字段(使用简单的字符串查找,避免依赖JSON库)
            int msgIndex = jsonStr.indexOf("\"msg\"");
            if (msgIndex == -1) {
                return null;
            }

            // 找到msg值的起始位置
            int colonIndex = jsonStr.indexOf(":", msgIndex);
            if (colonIndex == -1) {
                return null;
            }

            // 跳过冒号和可能的空格/引号
            int valueStart = colonIndex + 1;
            while (valueStart < jsonStr.length() &&
                   (jsonStr.charAt(valueStart) == ' ' || jsonStr.charAt(valueStart) == '"')) {
                valueStart++;
            }

            // 找到值的结束位置(引号或逗号)
            int valueEnd = valueStart;
            while (valueEnd < jsonStr.length()) {
                char c = jsonStr.charAt(valueEnd);
                if (c == '"' || c == ',' || c == '}') {
                    break;
                }
                valueEnd++;
            }

            if (valueEnd > valueStart) {
                return jsonStr.substring(valueStart, valueEnd).trim();
            }

            return null;
        } catch (Exception e) {
            // 提取失败,返回null
            return null;
        }
    }
}
