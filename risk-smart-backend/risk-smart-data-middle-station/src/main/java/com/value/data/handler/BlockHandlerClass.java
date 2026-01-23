package com.value.data.handler;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.value.data.common.utils.AjaxResult;

public class BlockHandlerClass {

    public static AjaxResult handlerException(BlockException e){
        return AjaxResult.error(429,"请求频率超出限流策略");
    }
}
