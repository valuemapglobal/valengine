package com.value.decision.common.configure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "model-task-record")
@RefreshScope
@Data
public class ModelTaskRecordProperties {
    private String aesKey;
    private QuotaProperties quota;

    @Data
    public static class QuotaProperties{
        private String sourceNo;
        private String manageNo;
        private String interfaceNo;
    }
}
