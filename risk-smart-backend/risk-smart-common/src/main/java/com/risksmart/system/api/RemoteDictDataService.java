package com.risksmart.system.api;

import com.risksmart.common.core.constant.ServiceNameConstants;
import com.risksmart.common.core.domain.R;
import com.risksmart.system.api.domain.SysDictData;
import com.risksmart.system.api.factory.RemoteDictDataFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(contextId = "RemoteDictDataService", value = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteDictDataFallbackFactory.class)
public interface RemoteDictDataService {

    /**
     * 根据字典类型获取字典数据
     * @param dictType
     * @return
     */
    @GetMapping("/system/dict/data/inner/type/{dictType}")
    R<List<SysDictData>> dictType(@PathVariable("dictType") String dictType);
}
