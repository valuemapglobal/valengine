package com.value.decision.model.rdenew.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 流水详情明细数据 -- 分类
 */

@Data
public class BankExcelCashflowRuleVO {

    private Integer id;

    /**
     *  银行账号
     */
    private String account;

    /**
     * 开户行名称
     */
    private String companyname;

    /**
     * 开户行名称
     */
    private String bankName;

    /**
     * 对方交易账户
     */
    private String dealaccount;

    /**
     * 交易时间
     */
    private String tradedate;

    /**
     * 币别
     */
    private String currency;

    /**
     * 贷款收入
     */
    private BigDecimal borrow;

    /**
     * 取款支出
     */
    private BigDecimal lend;

    /**
     *  余额
     */
    private String balance;

    /**
     * 对方账户名称
     */
    private String dealaccountname;

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
     * 其它内容
     */
    private String other;

    /**
     * 备注附言
     */
    private String remark;

    /**
     * 对应模版ID
     */
    private Integer templateId;

    /**
     * 对应的project Id
     */
    private String appId;

    /**
     * 0企业，1个人
     */
    private Integer type;

    /**
     * 0 有效 1 无效
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createtime;

    /**
     * 更新时间
     */
    private Date updatetime;

    /**
     * 数据状态 0正常，1删除
     */
    private Byte datastatus;

    /**
     * 所属公司id
     */
    private Integer deptid;

    /**
     * 流水分类
     */
    private String dealBankName;
}
