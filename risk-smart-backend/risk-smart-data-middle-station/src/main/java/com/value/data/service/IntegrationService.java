package com.value.data.service;

import java.util.List;
import java.util.Map;

/**
 * 联调接口服务
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
public interface IntegrationService {
    //如下新增
    Map<String,Object> tree(Long deptId);
    List<Map<String,Object>> metadataTree(Long deptId);
}
