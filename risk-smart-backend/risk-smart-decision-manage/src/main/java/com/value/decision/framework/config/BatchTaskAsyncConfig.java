package com.value.decision.framework.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@Slf4j
public class BatchTaskAsyncConfig {

    /**
     * 批量任务执行线程池
     * 专门用于处理批量Excel导入的异步任务
     */
    @Bean(name = "batchTaskExecutor")
    public Executor batchTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 核心线程数: 同时处理的批次数
        executor.setCorePoolSize(3);

        // 最大线程数
        executor.setMaxPoolSize(5);

        // 队列容量: 等待处理的批次数
        executor.setQueueCapacity(20);

        // 线程名前缀
        executor.setThreadNamePrefix("batch-task-");

        // 线程空闲时间(秒)
        executor.setKeepAliveSeconds(60);

        // 拒绝策略: CallerRunsPolicy会让调用者线程执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);

        // 等待时间(秒)
        executor.setAwaitTerminationSeconds(60);

        executor.initialize();

        return executor;
    }
}
