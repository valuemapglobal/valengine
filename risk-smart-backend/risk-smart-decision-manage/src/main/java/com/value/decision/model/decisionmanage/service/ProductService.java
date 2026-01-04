package com.value.decision.model.decisionmanage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.value.decision.model.decisionmanage.model.Product;

import java.util.List;

/**
 * <p>
 * 产品表 服务类
 * </p>
 *
 * @author Vida
 * @since 2024-11-07
 */
public interface ProductService extends IService<Product> {
    List<Product> queryList(Product product);

    List<Product> standardList();

    boolean checkIfProductIsReferenced(Long id);
}
