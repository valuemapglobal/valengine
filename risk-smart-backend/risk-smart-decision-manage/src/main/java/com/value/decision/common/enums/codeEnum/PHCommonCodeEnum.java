package com.value.decision.common.enums.codeEnum;

import lombok.Getter;

@Getter
public enum PHCommonCodeEnum {
    HZ_P_IDEN_020("HZ-P-IDEN-020","客户的司机标签认证成功"),
	HZ_P_IDEN_025("HZ-P-IDEN-025","客户里程数足够"),
	VM_P_lift_3("VM-P-lift-3","申请人在降额策略中的降额幅度 >= 60%")
	
    
    ;
	private final String id;
	private final String meaning;

	PHCommonCodeEnum(String id, String meaning) {
		this.id = id;
		this.meaning = meaning;
	}
    
}
