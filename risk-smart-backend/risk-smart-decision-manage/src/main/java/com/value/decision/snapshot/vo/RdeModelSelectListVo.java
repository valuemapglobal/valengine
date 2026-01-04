package com.value.decision.snapshot.vo;

import lombok.Data;

import java.util.Date;

/**
 * @ClassName RdeModelSelectListVo
 * @Description TODO
 * @Authot Administrator
 * @Date 2023/5/8 13:31
 * 测试数据集合vo类
 **/
@Data
public class RdeModelSelectListVo{
    /**
     * 序号
     */
    private Integer id;

    /**
     * 测试时间
     */
    private Date testTime;

    /**
     * 企业名称
     */
    private String companyName;

    /**
     * 尽调方式
     */
    private String accessWay;

    /**
     * 尽调 Id
     */
    private String orderNo;

    /**
     * 测试状态
     */
    private String testState;


}
