package com.value.decision.model.decisionmanage.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author hc
 * @since 2023-08-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QuotaEnterprise extends Model {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 评级
     */
    private String rate;

    /**
     * 基准额度
     */
    private String benchmarkLimit;

    /**
     * 700分以下
     */
    private Double under700;

    /**
     * 700~750
     */
    private Double between700750;

    /**
     * 750~800
     */
    private Double between750800;

    /**
     * 800~850
     */
    private Double between800850;

    /**
     * 850~900
     */
    private Double between850900;

    /**
     * 900~950
     */
    private Double between900950;

    /**
     * 950~1000
     */
    private Double between9501000;

    /**
     * 1000以上
     */
    private Double up1000;


}
