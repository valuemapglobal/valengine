package com.value.data.domain.dto;

import com.value.data.domain.entity.InterfaceFieldIdManage;
import com.value.data.domain.entity.InterfaceManage;
import com.value.data.domain.entity.InterfaceSourceManage;
import lombok.Data;

/**
 * 搜索接口数据源树形结构数据传输对象
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@Data
public class SearchInterfaceSourceTreeDTO {
    private Integer pageNum = 1;
    private Integer pageSize = 10;

    private InterfaceSourceManage source;
    private InterfaceManage interfaceManage;
    private InterfaceFieldIdManage field;
}
