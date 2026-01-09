package com.value.decision.model.decisionmanage.feign;

import com.risksmart.common.core.domain.R;
import com.risksmart.system.domain.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RuoYiUserFeignFallbackFactory implements FallbackFactory<RuoYiUserFeign> {
    @Override
    public RuoYiUserFeign create(Throwable cause) {
        log.error("调用system远程接口失败",cause);
        return new RuoYiUserFeign(){
            @Override
            public R<SysUser> selectOne(SysUser user, String source) {
                return R.fail("用户接口异常："+cause.getMessage());
            }
        };
    }
}
