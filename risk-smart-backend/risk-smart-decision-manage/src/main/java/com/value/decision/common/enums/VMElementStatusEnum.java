package com.value.decision.common.enums;

import lombok.Getter;

@Getter
public enum VMElementStatusEnum {
	INIT("0","初始化"),
	SUPPLEMENT_INFO("1","补充信息完成"),
	LIGHT_INFO("2","权属信息完成"),
	ASSESS_INFO("3","评估信息完成"),
//	WAIT_CHECK("10","待审核"),
//	APPROVED("1","审核通过"),
	FAIL("11","审核失败"),
//	REGISTER("40","已登记")
	;
	
//	0        登记中1        登记成功2        已确权3        已评估4        流转冻结11      登记失败12      确权中5        确权失败7        融资冻结8        预备案9        融资备案10      风险处置
	private final String id;
	private final String meaning;

	VMElementStatusEnum(String id, String meaning) {
		this.id = id;
		this.meaning = meaning;
	}

	public static VMElementStatusEnum get(String id) {
		for (VMElementStatusEnum node : VMElementStatusEnum.values()) {
			if (node.id.equals(id)) {
				return node;
			}
		}
		return null;
	}

}