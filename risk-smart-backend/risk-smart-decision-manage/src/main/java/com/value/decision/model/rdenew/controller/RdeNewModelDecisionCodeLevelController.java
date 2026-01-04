package com.value.decision.model.rdenew.controller;

import com.value.decision.common.utils.security.SecurityUtils;
import com.value.decision.framework.aspectj.lang.annotation.Log;
import com.value.decision.framework.aspectj.lang.enums.BusinessType;
import com.risksmart.common.core.web.controller.BaseController;
import com.risksmart.common.core.web.AjaxResult;
import com.risksmart.common.core.web.page.TableDataInfo;
import com.value.decision.engine.cache.DroolsKieBaseCache;
import com.value.decision.model.rdenew.domain.RdeModelDecisionCodeLevel;
import com.value.decision.model.rdenew.service.RdeModelDecisionCodeLevelService;
import com.value.decision.model.rdenew.vo.RdeModelDecisionCodeLevelVO;
import com.value.decision.model.rdenew.vo.RdeModelDecisionCodeLevelVO2;
import com.value.decision.common.security.LoginUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;

@RestController
@RequestMapping("rdenew/model/decisionCodeLevel")
public class RdeNewModelDecisionCodeLevelController extends BaseController {
    @Autowired
    private RdeModelDecisionCodeLevelService service;

    @Autowired
    private DroolsKieBaseCache droolsKieBaseCache;

    @PostMapping("/list")
    public TableDataInfo list(@RequestBody RdeModelDecisionCodeLevelVO record,HttpServletRequest request) {
        startPage(record);
        if(StringUtils.isNotBlank(record.getThemeIds())) {
        	record.setThemeArray(Arrays.asList(record.getThemeIds().split(",")));
        }
        return service.list(record,request);
    }
    /**
     * 检查code是否存在
     * @param record
     * @return
     */
    @PostMapping("/checkCode")
    public AjaxResult checkCode(@RequestBody RdeModelDecisionCodeLevelVO record) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(loginUser == null){
            return AjaxResult.error("用户未登录");
        }
    	return AjaxResult.success("成功",service.checkCode(record,loginUser));
    }
    
    @PostMapping("/official/list")
    public TableDataInfo officialList(RdeModelDecisionCodeLevelVO record) {
        startPage();
        if(StringUtils.isNotBlank(record.getThemeIds())) {
        	record.setThemeArray(Arrays.asList(record.getThemeIds().split(",")));
        }
        return getDataTable(service.officialList(record));
    }
    
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable Integer id) {
        return AjaxResult.success(service.selectById(id));
    }

    @PostMapping("/getById")
    public AjaxResult getById(@RequestBody RdeModelDecisionCodeLevel record) {
        return AjaxResult.success("成功",service.get(record));
    }

    @PostMapping("/submit")
    @Log(title = "反欺诈模型规则新增", businessType = BusinessType.INSERT)
    public AjaxResult submit(@Validated @RequestBody RdeModelDecisionCodeLevelVO2 record) {
        record.setContent(record.getContent().replaceAll("&gt;",">").replaceAll("&lt;","<"));
    	int num = service.submit(record,null);

    	// 清空Drools缓存,确保使用最新规则
    	if (num > 0) {
    	    droolsKieBaseCache.clearAll();
    	    logger.info("【规则模型新增】清空Drools缓存: codeLevel={}", record.getCode());
    	}

    	return AjaxResult.success(num);
    }

    @PostMapping("/update")
    @Log(title = "反欺诈模型规则编辑", businessType = BusinessType.UPDATE)
    public AjaxResult update(@Validated @RequestBody RdeModelDecisionCodeLevelVO2 record) {
        int num = service.submit(record,null);

        // 清空Drools缓存,确保使用最新规则
        if (num > 0) {
            droolsKieBaseCache.clearAll();
            logger.info("【规则模型编辑】清空Drools缓存: codeLevel={}", record.getCode());
        }

        return AjaxResult.success(num);
    }


    /**
     * 开放，关闭
     * @param record
     * @return
     */
    @PostMapping("/openUp")
    @Log(title = "操作", businessType = BusinessType.UPDATE)
    public AjaxResult openUp(@RequestBody RdeModelDecisionCodeLevelVO2 record) {
        int num = service.openUp(record);
        return AjaxResult.success(num);
    }

    @PostMapping("/delete")
    @Log(title = "反欺诈模型规则删除", businessType = BusinessType.DELETE)
    public AjaxResult delete(@RequestBody RdeModelDecisionCodeLevel record) {
        return toAjax(service.delete(record));
    }


    @PostMapping("/logicDelete")
    @Log(title = "反欺诈模型规则逻辑删除", businessType = BusinessType.DELETE)
    public AjaxResult logicDelete(@RequestBody RdeModelDecisionCodeLevel record) {
        return toAjax(service.logicDelete(record));
    }

    @GetMapping("/delete/{id}")
    @Log(title = "删除", businessType = BusinessType.DELETE)
    public AjaxResult deleteById(@PathVariable Integer id) {
        return toAjax(service.deleteById(id));
    }


    @PostMapping("/checkLogicDelete")
    @Log(title = "反欺诈模型规则逻辑删除", businessType = BusinessType.DELETE)
    public AjaxResult logicDeletedd(@RequestBody RdeModelDecisionCodeLevel record) {

        // 获取当前登录用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            return AjaxResult.error("用户未登录",false);
        }

        int i = service.logicDelete(record);
        return AjaxResult.success("删除成功", i);
    }

}
