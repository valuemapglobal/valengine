package com.value.decision.common.enums;

import lombok.Getter;

/**
 * redis-key
 * @author Raysen
 * @create 2021/10/28 17:38
 */
@Getter
public enum RedisKeysEnum {
    /*获取用户LIST*/
    USER_LIST("user_list"),
    /*短信验证码与注册*/
    SMS("sms"),
    /*重复请求*/
    REPEAT_SUBMIT("repeat_submit"),
    /*用户登录*/
    USER_LOGIN("user_login"),
    /*获取B端客户信息*/
    VAS_B_INFO("vas_b_info"),
    /*B端名单导出*/
    FREE_B_INFO("free_b_info"),
    /*第三方应用推送suite_ticket*/
    SUITE_TICKET("suite_ticket"),
    /*第三方应用授权的auth_code*/
    AUTH_CODE("auth_code"),
    /*网页授权回调*/
    VISIT_CALLBACK_CODE("callback_code"),
    /*朋友圈统计手机号*/
    COLLECT_FRIEND_CIRCLE_PHONE("collect_phone"),
    /*用户姓名、身份证*/
    USER_NAME_ID_NUMBER("user_name_id_number"),
    /*运营用户信息收集*/
    OPERATE_USER_COLLECT("operate_user_collect"),
    /*通联绑卡短信验证*/
    TL_BIND_SMS_CODE("tl:bind_sms")
    ;

    private final String prefix;

    RedisKeysEnum(String prefix){
        this.prefix = prefix;
    }

    public String join(Object... args){
        StringBuilder sb = new StringBuilder(prefix);
        if(null!=args && args.length!=0 && null!=args[0]){
            sb.append("_by");
            for(Object arg : args){
                sb.append("_").append(arg);
            }
        }
        return sb.toString();
    }

}
