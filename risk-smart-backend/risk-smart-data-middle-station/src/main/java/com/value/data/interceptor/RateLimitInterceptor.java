package com.value.data.interceptor;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 检查请求方法是否为HandlerMethod
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            // 判断方法是否标注了@SentinelResource注解
            if (handlerMethod.hasMethodAnnotation(SentinelResource.class)) {
                // 获取注解信息
                SentinelResource sentinelResource = handlerMethod.getMethodAnnotation(SentinelResource.class);
                // 获取资源名称，可以根据需要自定义，例如根据请求路径或其他信息
                String resourceName = sentinelResource.value();
//                 使用 Sentinel 进行限流判断
                // 在这里根据需要实现自己的限流逻辑，例如调用 Sentinel API 进行限流判断
                List<FlowRule> rules = new ArrayList<>();
                FlowRule rule = new FlowRule();
                rule.setResource(resourceName);
                rule.setGrade(RuleConstant.FLOW_GRADE_QPS); // 限流规则类型为 QPS
                rule.setCount(1); // 限制每秒请求数
                rules.add(rule);
                // 动态设置限流规则
                FlowRuleManager.loadRules(rules);
            }
        }
        return true;
    }
}
