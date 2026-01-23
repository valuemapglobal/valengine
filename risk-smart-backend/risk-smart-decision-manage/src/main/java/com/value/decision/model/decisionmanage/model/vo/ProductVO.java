package com.value.decision.model.decisionmanage.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.risksmart.common.core.constant.OperationType;
import com.value.decision.model.decisionmanage.model.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 产品VO
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductVO implements Serializable {

    private static final long serialVersionUID=1L;

    private String id;

    private String name;

    private Long userId;

    private Long deptId;

    private Short deptFlag;

    public static ProductVO of(Product product){
        return new ProductVO(
                product.getId().toString(),
                product.getName(),
                product.getUserId(),
                product.getDeptId(),
                product.getDeptFlag()
        );
    }
}
