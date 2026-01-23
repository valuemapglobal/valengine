package com.value.data.domain.vo;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

@Data
public class FindVariablesGroupVO implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 特征变量主题标识
     */
    private String no;

    /**
     * 特征变量分组标识
     */
    private String groupNo;

    /**
     * 分组名称
     */
    private String groupName;

    /**
     * 分组code
     */
    private String groupCode;

    /**
     * 关联的接口编号
     */
    private String interfaceManageNo;

    private String associatedInterfaces;

    /**
     * 版本号
     */
    private String interfaceVersion;

    @JSONField(alternateNames = {"no","characteristicVariablesNo"})
    public void setNo(String no) {
        this.no = no;
    }

    @JSONField(alternateNames = {"groupNo","characteristicVariablesGroupNo"})
    public void setGroupNo(String groupNo) {
        this.groupNo = groupNo;
    }
}
