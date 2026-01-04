package com.value.data.config;

import com.baomidou.mybatisplus.extension.ddl.IDdl;
import com.baomidou.mybatisplus.extension.ddl.history.IDdlGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

@Configuration
public class DdlConfig {
    @Bean
    public IDdl noopDdl() {
        return new IDdl() {
            @Override
            public void runScript(Consumer<DataSource> consumer) {
                // no-op: no DDL scripts for local startup
            }

            @Override
            public IDdlGenerator getDdlGenerator() {
                return null;
            }

            @Override
            public List<String> getSqlFiles() {
                return Collections.emptyList();
            }
        };
    }
}
