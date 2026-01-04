package com.value.decision.model.decisionmanage.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 评分卡复用关联表
 * </p>
 *
 * @author dianne
 * @since 2024-12-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ScoreCardReuseSnapshot extends Model {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

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

    /**
     * 部门ID
     */
    private Integer deptId;

    /**
     * 标准评分卡id
     */
    private Integer parentCardId;

    /**
     * 按钮状态  1启用0停用
     */
    private Boolean buttonState;

    /**
     * 版本控制
     */
    private String versionControl;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;

    /**
     * 数据状态
     */
    private Boolean dataStatus;


}
