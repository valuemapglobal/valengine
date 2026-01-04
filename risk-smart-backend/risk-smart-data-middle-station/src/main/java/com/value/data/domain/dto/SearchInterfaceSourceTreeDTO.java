package com.value.data.domain.dto;

import com.value.data.domain.entity.InterfaceFieldIdManage;
import com.value.data.domain.entity.InterfaceManage;
import com.value.data.domain.entity.InterfaceSourceManage;
import lombok.Data;

/**
 * @author Vida
 * @date 2025年04月18日 13:39
 * @description
 */
@Data
public class SearchInterfaceSourceTreeDTO {
    private Integer pageNum = 1;
    private Integer pageSize = 10;

    private InterfaceSourceManage source;
    private InterfaceManage interfaceManage;
    private InterfaceFieldIdManage field;
}
