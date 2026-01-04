package com.value.decision.common.enums;

import lombok.Getter;

@Getter
public enum VMNoticeTypeEnum {
	CENTER("1","中心公告"),
	PLEDGE("2","抵质押公告"),
	TENDER("3","竞价公示"),
	WINNING("4","竞价结果公示"),
	DEAL("5","流转成交公示"),
	LAW("6","相关法律"),
	POLICY("7","政策文件"),
	CIRCULATION("8","流转类"),
	CONTRACT("9","合同类"),
	OTHER("10","其他"),
	news("11","新闻动态"),
	bidding("12","视频动态"),
	financing("13","融资备案公示"),
	rightChange("14","权属变更"),
	financingRegister("15","登记公示"),
	MORTGAGE("17","抵质押公示")
	;
	private final String id;
	private final String meaning;

	VMNoticeTypeEnum(String id, String meaning) {
		this.id = id;
		this.meaning = meaning;
	}

	public static VMNoticeTypeEnum get(String id) {
		for (VMNoticeTypeEnum node : VMNoticeTypeEnum.values()) {
			if (node.id.equals(id)) {
				return node;
			}
		}
		return null;
	}

}