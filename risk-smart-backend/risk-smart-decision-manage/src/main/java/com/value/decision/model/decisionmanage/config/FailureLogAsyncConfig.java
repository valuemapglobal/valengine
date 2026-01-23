package com.value.decision.model.decisionmanage.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@Slf4j
public class FailureLogAsyncConfig {

    /**
     * 失败日志记录线程池
     * 专门用于异步记录任务执行失败日志,不阻塞主流程
     */
    @Bean(name = "failureLogExecutor")
    public Executor failureLogExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 核心线程数: 2个线程足够处理失败日志记录
        executor.setCorePoolSize(2);

        // 最大线程数: 高峰期最多4个线程
        executor.setMaxPoolSize(4);

        // 队列容量: 100个待处理的失败日志
        executor.setQueueCapacity(100);

        // 线程名前缀
        executor.setThreadNamePrefix("failure-log-");

        // 线程空闲时间(秒)
        executor.setKeepAliveSeconds(60);

        // 拒绝策略: CallerRunsPolicy会让调用者线程执行,确保日志不丢失
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);

        // 等待时间(秒)
        executor.setAwaitTerminationSeconds(30);

        executor.initialize();

        return executor;
    }
}
