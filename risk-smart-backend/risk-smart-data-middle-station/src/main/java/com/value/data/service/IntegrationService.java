package com.value.data.service;

import java.util.List;
import java.util.Map;

/**
 * @author Vida
 * @date 2023年08月21日 11:55
 * @description 联调接口服务
 */
public interface IntegrationService {
    //如下新增
    Map<String,Object> tree(Long deptId);
    List<Map<String,Object>> metadataTree(Long deptId);
}
