package com.value.decision.model.decisionmanage.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.value.decision.model.decisionmanage.mapper.ProductMapper;
import com.value.decision.model.decisionmanage.model.Product;
import com.value.decision.model.decisionmanage.service.ProductService;
import com.value.decision.process.mapper.ProcessPolicyMapper;
import com.value.decision.process.model.ProcessPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 产品表 服务实现类
 * </p>
 *
 * @author Vida
 * @since 2024-11-07
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Autowired
    private ProcessPolicyMapper processPolicyMapper;

    @Override
    public List<Product> queryList(Product product) {
        final String name = product.getName();
        product.setName(null);

        final LambdaQueryWrapper<Product> queryWrapper = Wrappers.lambdaQuery(product);
        if (StrUtil.isNotBlank(name)){
            queryWrapper.like(Product::getName,name)
            .orderByDesc(Product::getCreateTime);
        }else if (product.getId() != null){
            queryWrapper.eq(Product::getId,product.getId());
        }else {
            queryWrapper.orderByDesc(Product::getCreateTime);
        }
        return list(queryWrapper);
    }

    @Override
    public List<Product> standardList() {
        LambdaQueryWrapper<Product> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Product::getDeptFlag,1)
        .eq(Product::getDataStatus,0)
        .orderByDesc(Product::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public boolean checkIfProductIsReferenced(Long id) {
        if (id == null) {
            return false;
        }
        //检查是否被流程策略引用
        LambdaQueryWrapper<ProcessPolicy> policyWrapper = new LambdaQueryWrapper<>();
        policyWrapper.eq(ProcessPolicy::getProductId, id)
                .eq(ProcessPolicy::getDataStatus, 0)
                .last("limit 1");
        long policyCount = processPolicyMapper.selectCount(policyWrapper);

        return policyCount > 0;
    }
}
