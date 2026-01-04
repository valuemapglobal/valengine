package com.value.decision.model.rdenew.vo;

import lombok.Data;

@Data
public class AiRiskPayExcelCashflowVO {

    private Integer id;

    /**
     * 流水号
     */
    private String orderNumber;

    /**
     * 流水批次
     */
    private String excelNum;

    /**
     * 企业名称
     */
    private String companyName;

    /**
     *  银行账号
     */
    private String account;

    /**
     * 开户行名称
     */
    private String bankName;

    /**
     * 对方交易账户
     */
    private String dealAccount;

    /**
     * 交易时间
     */
    private String tradeDate;

    /**
     * 币别
     */
    private String currency;

    /**
     * 贷款收入
     */
    private String borrow;

    /**
     * 取款支出
     */
    private String lend;

    /**
     * 流水类型（微信、支付宝、其他）
     */
    private String flowType;

    /**
     * 收支类型 0-普通 1-其他
     */
    private Integer balanceType;

    /**
     *  余额
     */
    private String balance;

    /**
     * 交易方式
     */
    private String tradeWay;

    /**
     * 交易订单号
     */
    private String tradeOrder;

    /**
     * 商家订单号
     */
    private String merchantOrder;

    /**
     * 对方账户名称
     */
    private String dealAccountName;

    /**
     * 交易方开户行
     */
    private String dealBankName;

    /**
     * 经营性类别 1-经营性收入 2-非经营性收入 3-经营性支出 4-非经营性支出
     */
    private String categoryType;

    /**
     * 流水分类
     */
    private String purposeCategory;

    /**
     * 用途
     */
    private String purpose;

    /**
     * 备注
     */
    private String other;

    /**
     * 摘要
     */
    private String remark;

    /**
     * 0企业，1个人
     */
    private Integer type;

    /**
     * 0 有效 1 无效
     */
    private Integer status;

    /**
     * 数据状态 0正常，1删除
     */
    private Integer dataStatus;

    /**
     * 所属公司id
     */
    private Integer deptId;

    /**
     * 用户id
     */
    private Long userId;

    private Integer createBy;

    private Integer updateBy;

    /**
     * 规则描述
     */
    private String content;
}
