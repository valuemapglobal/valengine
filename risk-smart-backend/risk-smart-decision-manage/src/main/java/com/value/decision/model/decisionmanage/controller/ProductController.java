package com.value.decision.model.decisionmanage.controller;


import cn.hutool.core.bean.BeanUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.value.decision.common.constant.OperationType;
import com.value.decision.framework.aspectj.lang.annotation.RequirLoginUser;
import com.value.decision.model.decisionmanage.model.Product;
import com.value.decision.model.decisionmanage.model.dto.ProductQueryDTO;
import com.value.decision.model.decisionmanage.model.vo.ProductVO;
import com.value.decision.model.decisionmanage.service.ProductService;
import com.value.decision.model.decisionmanage.service.RuoYiService;
import com.risksmart.common.core.web.AjaxResult;
import com.value.decision.common.security.LoginUser;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 产品表 前端控制器
 * </p>
 *
 * @author Vida
 * @since 2024-11-07
 */
@RestController
@AllArgsConstructor
@RequestMapping("/product")
public class ProductController {
    private ProductService service;
    private RuoYiService ruoYiService;

    @PostMapping
    @RequirLoginUser
    public AjaxResult save(@Validated(OperationType.Create.class) @RequestBody Product product,LoginUser loginUser) {
        product.setUserId(loginUser.getUserid());
        product.setDeptId(loginUser.getSysUser().getDeptId());
        product.setDeptFlag((short) (ruoYiService.getStandardDepts().contains(loginUser.getSysUser().getDeptId())?1:2));
        return AjaxResult.success(service.save(product));
    }

    @DeleteMapping("/{id}")
    public AjaxResult deleteCheck(@PathVariable Long id){
        // 校验产品是否被引用
        boolean isReferenced = service.checkIfProductIsReferenced(id);
        if (isReferenced) {
            return AjaxResult.success("检测到目前正在被流程使用，无法删除",false);
        }
        boolean b = service.removeById(id);
        return AjaxResult.success("操作成功",b);
    }

    @PutMapping
    public AjaxResult update(@Validated(OperationType.Update.class) @RequestBody Product product) {
        return AjaxResult.success(service.updateById(product));
    }

    @PostMapping("/_search")
    @RequirLoginUser
    public AjaxResult QueryList(@RequestBody(required = false) ProductQueryDTO product,LoginUser loginUser) {
        if (product != null && (product.getPageNum()!=null && product.getPageSize()!=null)){
            PageHelper.startPage(product.getPageNum(),product.getPageSize());
        }
        product.setDeptId(loginUser.getSysUser().getDeptId());
        final List<Product> list = service.queryList(product);
        final PageInfo<Product> pageInfo = new PageInfo<>(list);
        if (list!=null && list.size()>0){
            final List<ProductVO> productVOList = list.stream().map(ProductVO::of).collect(Collectors.toList());
            final PageInfo<ProductVO> page = new PageInfo<>();
            BeanUtil.copyProperties(pageInfo,page);
            page.setList(productVOList);
            return AjaxResult.success(page);
        }
        return AjaxResult.success(pageInfo);
    }


    @GetMapping("/_standard")
    @RequirLoginUser
    public AjaxResult standardList() {
        List<Product> list = service.standardList();
        return AjaxResult.success(list);
    }

    @GetMapping("/{id}")
    public AjaxResult QueryOne(@PathVariable Long id){
        return AjaxResult.success(service.getById(id));
    }

    public static void main(String[] args) {
        final Product product = new Product();
        product.setId(123456789L);
        final ProductVO productVO = new ProductVO();
        BeanUtil.copyProperties(product,productVO);
        System.out.println(productVO.getId());
    }
}

