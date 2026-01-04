package com.value.decision.version.domain;

import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 决策code登记表
 * </p>
 *
 * @author Dianne
 * @since 2023-05-08
 */
@Data
@Table(name = "rde_model_decision_code_level_version")
public class RdeModelDecisionCodeLevelVersion {

    /**
     * id
     */
//    @Id
//    @GeneratedValue(generator = "JDBC")
    @Column(name = "id")
    private Integer id;

    /**
     * code
     */
    @Column(name = "code")
    private String code;

    /**
     * 等级
     */
    @Column(name = "level")
    private String level;

    /**
     * 类型，0默认评分策略，1风险预警策略，2贷前准入策略,3银行流水预警策略
     */
    @Column(name = "type")
    private Integer type;

    /**
     * 风险分类，1运营商风险，2欺诈风险，3身份认证风险，4失信被执行风险，5.黑名单风险，6多头风险，7.交易行为风险 ，8.催收风险 , 9.法人年龄 , 10.法人性别 , 11.关联人潜在风险 ,  12.特殊名单 , 13.借贷意向 , 14.经营年限 , 15.自然人股东占比 , 16.潜在经营性风险 , 17.工商变更-法人变更 , 18.工商变更-高管变更  ,19.行政处罚 , 20.环保处罚 , 21.经营异常 , 22.法院公告  , 23严重违法 , 24.终本案件 , 25.成为箱讯客户时长 , 26.货物品类波动变化水平 , 26.货物路线波动变化水平
     */
    @Column(name = "risk_type")
    private Integer riskType;

    /**
     * 说明
     */
    @Column(name = "content")
    private String content;

    /**
     * 话术
     */
    @Column(name = "risk_description")
    private String riskDescription;

    /**
     * 排序
     */
    @Column(name = "sort_num")
    private Integer sortNum;

    /**
     * 开始比例%
     */
    @Column(name = "start_rate")
    private BigDecimal startRate;

    /**
     * 结束比例%
     */
    @Column(name = "end_rate")
    private BigDecimal endRate;

    /**
     * 状态 0 未开放 1 已开放
     */
    @Column(name = "status")
    private String status;

    /**
     * 条件
     */
    @Column(name = "term")
    private String term;

    /**
     * 规则所用的包
     */
    @Column(name = "term_package")
    private String termPackage;

    /**
     * 处理所需别名
     */
    @Column(name = "term_key")
    private String termKey;

    /**
     * 处理所需code
     */
    @Column(name = "term_value")
    private String termValue;

    /**
     * 处理所需其他条件
     */
    @Column(name = "term_other")
    private String termOther;

    /**
     * 生成规则
     */
    @Column(name = "term_rule")
    private String termRule;

    /**
     * 备注
     */
    @Column(name = "remark")
    private String remark;

    /**
     * 更新人
     */
    @Column(name = "update_by")
    private String updateBy;

    /**
     * 更新名称
     */
    @Column(name = "create_by")
    private String createBy;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private Date updateTime;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 公司id
     */
    @Column(name = "dept_id")
    private Integer deptId;

    /**
     * 数据状态
     */
    @Column(name = "data_status")
    private Integer dataStatus;

    /**
     * 部门标识101为1，其他为2
     */
    @Column(name = "dept_flag")
    private String deptFlag;

    /**
     * 页面标识 分类为1,规则为0
     */
    @Column(name = "rule_code")
    private String ruleCode;

    /**
     * 项目代码;银行流水1001,支付流水1002
     */
    @Column(name = "project_code")
    private String projectCode;

    /**
     * 分类名称
     */
    @Column(name = "purpose_category")
    private String purposeCategory;

    /**
     * 流水性质
     * 1->经营性收入
     * 2->经营性支出
     * 3->经营性支出
     * 4->非经营性支出
     */
    @Column(name = "category_type")
    private String categoryType;

    @Column(name = "business_code")
    private String businessCode;

    //判断条件
    @Column(name = "conditions")
    private String conditions;

    /**
     * 版本控制
     */
    @Column(name = "version_control")
    private String versionControl;

    @Column(name = "data_module")
    private String dataModule;

    @Column(name = "strongly_reject")
    private String stronglyReject;

    @Column(name = "quato_rate")
    private String quatoRate;
}
