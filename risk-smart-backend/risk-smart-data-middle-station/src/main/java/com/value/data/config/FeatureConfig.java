package com.value.data.config;

import delight.nashornsandbox.NashornSandbox;
import delight.nashornsandbox.NashornSandboxes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;

/**
 * 特征变量配置
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@Configuration
public class FeatureConfig {
    @Bean
    public NashornSandbox nashornSandbox(FeatureProperties properties) throws NoSuchMethodException {
        final FeatureProperties.SandBox sandBox = properties.getSandBox();
        NashornSandbox sandbox = NashornSandboxes.create();

        // 设置脚本执行允许的最大CPU时间（以毫秒为单位），超过则会报异常,防止死循环脚本
        sandbox.setMaxCPUTime(sandBox.getMaxCPUTime());
        //设置JS执行程序线程可以分配的最大内存（以字节为单位），超过会报ScriptMemoryAbuseException错误
        sandbox.setMaxMemory(sandBox.getMaxMemory());
        // 是否允许不使用大括号
        sandbox.allowNoBraces(sandBox.getAllowNoBraces());
        // 是否允许nashorn加载全局函数
        sandbox.allowLoadFunctions(sandBox.getAllowLoadFunctions());
        // LRU初缓存的初始化大小，默认为0
        sandbox.setMaxPreparedStatements(sandBox.getMaxPreparedStatements());
        // 指定执行程序服务，该服务用于在CPU时间运行脚本
        sandbox.setExecutor(Executors.newSingleThreadExecutor());
        //允许访问某个类
//        sandbox.allow(DateUtil.class);

        return sandbox;
    }
}
