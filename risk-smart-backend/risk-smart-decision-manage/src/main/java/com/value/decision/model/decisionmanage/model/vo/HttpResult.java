package com.value.decision.model.decisionmanage.model.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Vida
 * @date 2025年03月19日 13:56
 * @description
 */
@Data
@Accessors(chain = true)
public class HttpResult<T> {
    private Integer code;
    private String msg;
    private T data;

    public static HttpResult fail(String msg){
        final HttpResult httpResult = new HttpResult();
        httpResult.setCode(500)
                .setMsg(msg);
        return httpResult;
    }
}
