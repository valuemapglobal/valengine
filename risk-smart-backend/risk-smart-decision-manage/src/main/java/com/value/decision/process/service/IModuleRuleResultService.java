package com.value.decision.process.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.value.decision.process.model.ModuleRuleResult;
import com.value.decision.process.vo.ProcesPolicyDTO;
import com.value.decision.process.vo.ProcesPolicyVO;

/**
 * <p>
 * 模块任务结果表 服务类
 * </p>
 *
 * @author hc
 * @since 2023-08-16
 */
public interface IModuleRuleResultService extends IService<ModuleRuleResult> {

    /**
     * 回调任务接口 -- 额度结果
     */
    ProcesPolicyDTO quotaResult(ProcesPolicyVO procesPolicyVO);

}
