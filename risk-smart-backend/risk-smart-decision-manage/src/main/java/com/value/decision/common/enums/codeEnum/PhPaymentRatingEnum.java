package com.value.decision.common.enums.codeEnum;

import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 涉及评分基础额度的规则编号
 */
@Getter
public enum PhPaymentRatingEnum {

    HZ_P_IDEN_016("HZ-P-IDEN-016",0.0,"客户的司机标签认证失败，且个人KYC支付评分较差"),
    HZ_P_IDEN_017("HZ-P-IDEN-017",15000.0,"客户的司机标签认证失败，且个人KYC支付评分一般"),
    HZ_P_IDEN_018("HZ-P-IDEN-018",20000.0,"客户的司机标签认证失败，且个人KYC支付评分较好"),
    HZ_P_IDEN_019("HZ-P-IDEN-019",25000.0,"客户的司机标签认证失败，且个人KYC支付评分很好"),
    HZ_P_IDEN_021("HZ-P-IDEN-021",0.0,"客户里程数不足，且个人KYC支付评分较差"),
    HZ_P_IDEN_022("HZ-P-IDEN-022",0.0,"客户里程数不足，且个人KYC支付评分略差"),
    HZ_P_IDEN_023("HZ-P-IDEN-023",15000.0,"客户里程数不足，且个人KYC支付评分一般"),
    HZ_P_IDEN_024("HZ-P-IDEN-024",20000.0,"客户里程数不足，且个人KYC支付评分较好"),
    HZ_P_IDEN_024_1("HZ-P-IDEN-024-1",30000.0,"客户里程数不足，且个人KYC支付评分很好"),
    ;
    private final String id;
    private final Double qua;
    private final String meaning;

    PhPaymentRatingEnum(String id, Double qua ,String meaning) {
        this.id = id;
        this.qua = qua;
        this.meaning = meaning;
    }

    /**
     * 根据id值获取枚举
     */
    public static PhPaymentRatingEnum get(String id){
        for (PhPaymentRatingEnum value : values()) {
            if (value.getId().equals(id)){
                return value;
            }
        }
        return null;
    }

    /**
     * 评分相关的规则集
     * @return
     */
    public static List<String> getCodes(){
        return Arrays.stream(PhPaymentRatingEnum.values()).map(x->x.getId()).collect(Collectors.toList());
    }

    /**
     * 获取额度集合，取最小值
     * @return
     */
    public static Double getQua(List<String> hitCodes,Double qua){
        if(CollectionUtils.isNotEmpty(hitCodes)){
            return Arrays.stream(PhPaymentRatingEnum.values())
                    .filter(x->hitCodes.contains(x.getId()))
                    .map(y->y.getQua())
                    .collect(Collectors.summarizingDouble(z->z))
                    .getMin();
        }
        return qua;
    }

    /**
     * SDK数据身份验证
     * @return 
     */
    public static List<PhPaymentRatingEnum> getIdentityVerificationCodeList(){
        return Arrays.asList(
                HZ_P_IDEN_016,
                HZ_P_IDEN_017,
                HZ_P_IDEN_018,
                HZ_P_IDEN_019
        );
        
    }
    
    /**
     * 数据宝高速公路里程数
     * @return 
     */
    public static List<PhPaymentRatingEnum> getMileageCodeList(){
        return Arrays.asList(
                HZ_P_IDEN_021,
                HZ_P_IDEN_022,
                HZ_P_IDEN_023,
                HZ_P_IDEN_024,
                HZ_P_IDEN_024_1
        );
    }
    
}
