package com.risksmart.system.api.factory;

import com.risksmart.common.core.domain.R;
import com.risksmart.system.api.RemoteDictDataService;
import com.risksmart.system.api.domain.SysDictData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 字典数据服务降级处理
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@Component
public class RemoteDictDataFallbackFactory implements FallbackFactory<RemoteDictDataService> {

    private static final Logger log = LoggerFactory.getLogger(RemoteDictDataFallbackFactory.class);

    @Override
    public RemoteDictDataService create(Throwable throwable) {
        log.error("Dictionary data service call failed: {}", throwable.getMessage());
        return new RemoteDictDataService() {
            @Override
            public R<List<SysDictData>> dictType(String dictType) {
                return R.fail("Failed to get dictionary data: " + throwable.getMessage());
            }
        };
    }
}
