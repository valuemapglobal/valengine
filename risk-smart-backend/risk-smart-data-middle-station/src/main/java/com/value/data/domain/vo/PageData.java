package com.value.data.domain.vo;

import lombok.Data;

import java.util.List;

/**
 * 分页数据封装类
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@Data
public class PageData {
    private Long total;
    private Long pageNum;
    private Long pageSize;
    private List rows;
}
