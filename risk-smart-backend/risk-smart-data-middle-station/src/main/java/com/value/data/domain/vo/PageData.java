package com.value.data.domain.vo;

import lombok.Data;

import java.util.List;

/**
 * @author Vida
 * @date 2023年08月07日 11:36
 * @description
 */
@Data
public class PageData {
    private Long total;
    private Long pageNum;
    private Long pageSize;
    private List rows;
}
