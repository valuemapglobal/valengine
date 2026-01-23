package com.value.decision.framework.config;

import com.baomidou.mybatisplus.extension.ddl.IDdl;
import com.baomidou.mybatisplus.extension.ddl.history.IDdlGenerator;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DdlConfig {
    @Bean
    public IDdl ddl() {
        return new IDdl() {
            @Override
            public void runScript(Consumer<DataSource> consumer) {
                // no-op
            }

            @Override
            public IDdlGenerator getDdlGenerator() {
                return null;
            }

            @Override
            public List<String> getSqlFiles() {
                // No-op to avoid startup failure when no DDL is configured.
                return Collections.emptyList();
            }
        };
    }
}
