package com.value.decision.common.enums;

import lombok.Getter;

@Getter
public enum VMDataStatusEnum {
	INVALID("0","无效"),
	VALID("1","有效"),
	;
	private final String id;
	private final String meaning;

	VMDataStatusEnum(String id, String meaning) {
		this.id = id;
		this.meaning = meaning;
	}

	public static VMDataStatusEnum get(String id) {
		for (VMDataStatusEnum node : VMDataStatusEnum.values()) {
			if (node.id.equals(id)) {
				return node;
			}
		}
		return null;
	}

}