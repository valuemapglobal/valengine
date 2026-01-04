package com.value.decision.model.rdenew.mapper;

import com.value.decision.common.base.CommonMapper;
import com.value.decision.model.rdenew.domain.RdeModelDecisionCodeLevel;
import com.value.decision.model.rdenew.domain.RdeRiskVariableTheme;
import com.value.decision.model.rdenew.vo.BankClassVO;
import com.value.decision.model.rdenew.vo.RdeModelDecisionCodeLevelVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RdeModelDecisionCodeLevelMapper extends CommonMapper<RdeModelDecisionCodeLevel> {
	List<RdeModelDecisionCodeLevel> selectListQ(RdeModelDecisionCodeLevelVO query);

	List<RdeModelDecisionCodeLevel> selectRuleList(BankClassVO bankClassVO);

	//适用于支付流水决策未发布之前的测试功能
	List<RdeModelDecisionCodeLevel> selectRuleListTest(BankClassVO bankClassVO);

	List<RdeModelDecisionCodeLevel> officialList(RdeModelDecisionCodeLevelVO query);

	List<RdeRiskVariableTheme> selectObjectList(Integer codeId);

	//根据codeList查找详情List
	List<RdeModelDecisionCodeLevel> selectCodeList(@Param("codeList") List<String> codeList, @Param("deptId") Integer deptId);

	//根据模型id找到对应的code规则文件
	List<String> selectCodeFile(List<Integer> modelIdList);

	//根据模型id找到对应的codeId
	List<String> selectCodeId(List<Integer> modelIdList);

	//从原表中获取数据存入快照表 -- 规则表
	public List<RdeModelDecisionCodeLevel> queryList(@Param("ids") List<Integer> ids, @Param("deptId") Integer deptId,@Param("projectCode") String projectCode,@Param("ruleCode") String ruleCode);
    //从原表中获取数据存入快照表 -- 规则表
    public List<RdeModelDecisionCodeLevel> queryList2(@Param("ids") List<Integer> ids, @Param("deptId") Integer deptId,@Param("projectCode") String projectCode,@Param("ruleCode") String ruleCode,@Param("businessCode") String businessCode);

	public List<RdeModelDecisionCodeLevel> queryListByCode(@Param("codes") List<String> codes, @Param("deptId") Integer deptId,@Param("projectCode") String projectCode,@Param("ruleCode") String ruleCode);

	//从原表中删除其他账号下的标准模型数据 -- 规则表
	public void deleteList(@Param("ids") List<Integer> ids, @Param("deptId") Integer deptId,@Param("projectCode") String projectCode,@Param("ruleCode") String ruleCode);

	List<String> getSystematicNameByIndustryName(@Param("categoryType") Integer categoryType,@Param("name") String name,@Param("deptId") Integer deptId,@Param("projectCode") String projectCode);

	//银行流水分类通用
	List<String> getSystematicNameByIndustryNameCommon(@Param("categoryType") Integer categoryType,@Param("modelId") String modelId);

    int selectList2(@Param("id") Integer id,@Param("project_code") String projectCode,@Param("business_code") String businessCode,@Param("rule_code") String ruleCode,@Param("dept_id") Long deptId,@Param("code") String code,@Param("moduleId") String moduleId);


	//批量修改
	void updateVersionList(RdeModelDecisionCodeLevel fraudList);

	//批量插入
	public void insertBatch(@Param("CodeLevelList") List<RdeModelDecisionCodeLevel> CodeLevelList);

	// 根据code查询，不受权限拦截影响（用于监控详情获取字段配置）
	RdeModelDecisionCodeLevel selectByCodeWithoutPermission(@Param("code") String code, @Param("type") Integer type);
}