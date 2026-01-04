package com.value.decision.common.enums;

import lombok.Getter;

@Getter
public enum VMElementNoticeTypeEnum {
	CIRCULATION("1","开始流转要素"),
	BIDING_START("2","开始竞价要素"),
	BIDING_END("3","竞价结束要素"),
	FINISHED("4","已完成交易要素"),
	;
	private final String id;
	private final String meaning;

	VMElementNoticeTypeEnum(String id, String meaning) {
		this.id = id;
		this.meaning = meaning;
	}

	public static VMElementNoticeTypeEnum get(String id) {
		for (VMElementNoticeTypeEnum node : VMElementNoticeTypeEnum.values()) {
			if (node.id.equals(id)) {
				return node;
			}
		}
		return null;
	}

}