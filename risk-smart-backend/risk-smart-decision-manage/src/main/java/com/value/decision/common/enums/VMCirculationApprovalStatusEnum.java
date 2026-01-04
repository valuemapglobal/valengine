package com.value.decision.common.enums;

import lombok.Getter;

/**
 * 要素流转审批状态枚举类
 * @author JackCheng
 * 2021年10月14日
 */
@Getter
public enum VMCirculationApprovalStatusEnum {
	APPLY("0","新申请"),
	UNDER_APPROVAL("1","审批中"),
	APPROVED("2","已通过"),
	REJECT("3","已拒绝"),
	;
	private final String id;
	private final String meaning;

	VMCirculationApprovalStatusEnum(String id, String meaning) {
		this.id = id;
		this.meaning = meaning;
	}

	public static VMCirculationApprovalStatusEnum get(String id) {
		for (VMCirculationApprovalStatusEnum node : VMCirculationApprovalStatusEnum.values()) {
			if (node.id.equals(id)) {
				return node;
			}
		}
		return null;
	}

}