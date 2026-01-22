package com.value.decision.model.decisionmanage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.value.decision.model.decisionmanage.model.Product;

import java.util.List;

/**
 * 产品表服务类
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
public interface ProductService extends IService<Product> {
    List<Product> queryList(Product product);

    List<Product> standardList();

    boolean checkIfProductIsReferenced(Long id);
}
