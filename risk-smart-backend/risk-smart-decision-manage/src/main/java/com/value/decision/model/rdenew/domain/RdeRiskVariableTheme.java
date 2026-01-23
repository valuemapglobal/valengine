package com.value.decision.model.rdenew.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.risksmart.common.core.web.domain.BaseVO;
import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Date;

@Data
@Table(name = "rde_risk_variable_theme")
public class RdeRiskVariableTheme extends BaseVO {
    @Id
    @GeneratedValue(generator = "JDBC")
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 名称
     */
    private String name;

    /**
     * 唯一标识
     */
    private String keycode;

    /**
     * 0分析主体，1衍生主体
     */
    @Column(name = "package_type")
    private String packageType;

    /**
     * 备注
     */
    private String remark;

    /**
     * 操作人ID
     */
    @Column(name = "create_user_id")
    private Integer createUserId;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private Date updateTime;

    /**
     * 数据状态
     */
    @Column(name = "data_status")
    private Byte dataStatus;
}
