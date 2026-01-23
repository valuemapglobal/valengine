package com.value.decision.model.decisionmanage.model.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.value.decision.model.decisionmanage.model.ScoreCardRecord;
import com.value.decision.model.decisionmanage.model.ScoreCardRecordSnapshot;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ScoreCardRecordVO {


    private Integer id;

    /**
     * 评分卡名称
     */
    private String scoreCard;

    /**
     *
     */
    private Integer standardProjectCode;


    /**
     * 左侧产品导航标识
     */
    @NotBlank(message = "左侧产品导航标识不能为空")
    private String projectCode;

    /**
     * 业务场景导航标识
     */
    @NotNull(message = "业务场景导航标识不能为空")
    private String businessCode;

    /**
     * 策略类型导航标识
     */
    @NotNull(message = "策略类型导航标识不能为空")
    private String ruleCode;

    /**
     * 部门标识 1超级管理员 2普通用户
     */
    private Integer deptFlag;

    /**
     * 部门ID
     */
    private Integer deptId;

    /**
     * 部门ID 页面传入，用于查看已配置的部门数据
     */
    @TableField(exist = false)
    private String deptIdNew;

    /**
     * 按钮状态 1启用 0禁用
     */
    private Integer buttonState;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime updateTime;

    /**
     * 操作人ID
     */
    private Integer createUserId;

    /**
     * 版本控制
     */
    private String versionControl;

    /**
     * 模型总分数
     */
    private String modelScore;

    //操作功能点
    @TableField(exist = false)
    private String operationPoints;

    //策略模型名称
    @TableField(exist = false)
    private String policyModelName;

    //功能点标志
    @TableField(exist = false)
    private String points;

    private Integer pageNum;
    private Integer pageSize;

    /**
     * 用户选择新增的数据
     */
    private List<ScoreCardRecord> scoreCardDataList;

    /**
     * 用户选择移除的数据
     */
    private List<ScoreCardRecord> scoreCardDataDelList;

    /**
     * 用户选择新增的标准数据
     */
    private List<ScoreCardRecordSnapshot> scoreCardStandardDataList;

    /**
     * 用户选择移除的标准数据
     */
    private List<ScoreCardRecordSnapshot> scoreCardStandardDataDelList;

    /**
     * 自建产品id
     */
    private Integer buildProjectCode;

    /**
     * 自建业务场景id
     */
    private Integer buildBusinessCode;

    /**
     * 自建策略导航id
     */
    private Integer buildRuleCode;

}
