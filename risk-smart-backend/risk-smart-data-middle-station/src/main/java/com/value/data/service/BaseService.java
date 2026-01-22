package com.value.data.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.value.data.domain.vo.PageData;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 基础服务接口
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
public interface BaseService {
    default  <T> List<T> getVOList(List lists,Class<T> voClass){
        if (lists==null||lists.size()<=0)
            return null;
        ArrayList<T> ts = new ArrayList<>();
        try {
            for (Object list:lists){
                T t = voClass.newInstance();
                BeanUtils.copyProperties(list,t);
                ts.add(t);
            }
        }finally {
            return ts;
        }
    }

    default PageData getPage(Page page){
        PageData pageData = new PageData();
        if (page==null)
            return pageData;
        pageData.setPageNum(page.getCurrent());
        pageData.setPageSize(page.getSize());
        pageData.setTotal(page.getTotal());
        pageData.setRows(page.getRecords());
        return pageData;
    }
}
