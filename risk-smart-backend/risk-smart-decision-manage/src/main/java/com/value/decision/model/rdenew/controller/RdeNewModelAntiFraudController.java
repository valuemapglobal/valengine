package com.value.decision.model.rdenew.controller;

import cn.hutool.core.util.StrUtil;
import com.value.decision.common.utils.security.SecurityUtils;
import com.value.decision.framework.aspectj.lang.annotation.Log;
import com.value.decision.framework.aspectj.lang.annotation.RequirLoginUser;
import com.value.decision.framework.aspectj.lang.enums.BusinessType;
import com.risksmart.common.core.web.controller.BaseController;
import com.risksmart.common.core.web.AjaxResult;
import com.value.decision.model.decisionmanage.model.dto.model.ModelAntiFraudVO;
import com.value.decision.model.rdenew.domain.RdeModelAntiFraud;
import com.value.decision.model.rdenew.function.CommonRuleFunctionDataNew;
import com.value.decision.model.rdenew.service.ProcessPolicyReferenceService;
import com.value.decision.model.rdenew.service.RdeModelAntiFraudService;
import com.value.decision.model.rdenew.vo.RdeModelAntiFraudDTO;
import com.value.decision.model.rdenew.vo.RdeModelAntiFraudVO;
import com.value.decision.model.rdenew.vo.RdeUpdateStateEntryVO;
import com.value.decision.snapshot.domain.RdeModelAntiFraudSnapshot;
import com.value.decision.version.domain.ModelVersionClassification;
import com.value.decision.version.service.ModelVersionClassificationService;
import com.value.decision.version.service.RdeModelAntiFraudVersionService;
import com.value.decision.version.vo.VersionControlVO;
import com.value.decision.common.security.LoginUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/rdenew/model/antiFraud")
public class RdeNewModelAntiFraudController extends BaseController {
    @Autowired
    private RdeModelAntiFraudService service;

    @Autowired
    private ModelVersionClassificationService modelVersionClassificationService;

    @Autowired
    private RdeModelAntiFraudVersionService rdeModelAntiFraudVersionService;

    @Autowired
    private CommonRuleFunctionDataNew commonRuleFunctionDataNew;

    @Autowired
    private ProcessPolicyReferenceService processPolicyReferenceService;

    /**
     * 页面列表
     * @param rdeModelAntiFraud
     * @return
     */
    @PostMapping("/newList")
    @RequirLoginUser
    public AjaxResult newList(@RequestBody ModelAntiFraudVO rdeModelAntiFraud){
        List<RdeModelAntiFraud> RdeModelAntiFraudList = service.newlist(rdeModelAntiFraud);
        if (rdeModelAntiFraud.getPageNum() == null && rdeModelAntiFraud.getPageSize() == null){
            return AjaxResult.success(RdeModelAntiFraudList);
        }
        AjaxResult paging = commonRuleFunctionDataNew.paging(RdeModelAntiFraudList, rdeModelAntiFraud.getPageNum(), rdeModelAntiFraud.getPageSize());
        Object data = paging.get("data");
        return AjaxResult.success(data);
    }


    /**
     * 规则复用-自建列表
     * @param rdeModelAntiFraud
     * @return
     */
    @PostMapping("/newBuildList")
    @RequirLoginUser
    public AjaxResult newBuildList(@RequestBody ModelAntiFraudVO rdeModelAntiFraud){
        List<RdeModelAntiFraud> RdeModelAntiFraudList = service.newBuildList(rdeModelAntiFraud);
        AjaxResult paging = commonRuleFunctionDataNew.paging(RdeModelAntiFraudList, rdeModelAntiFraud.getPageNum(), rdeModelAntiFraud.getPageSize());
        Object data = paging.get("data");
        return AjaxResult.success(data);
    }

    /**
     * 规则复用-标准列表
     * @param rdeModelAntiFraud
     * @return
     */
    @PostMapping("/standardList")
    @RequirLoginUser
    public AjaxResult standardList(@RequestBody ModelAntiFraudVO rdeModelAntiFraud){
        List<RdeModelAntiFraudSnapshot> RdeModelAntiFraudList = service.standardList(rdeModelAntiFraud);
        AjaxResult paging = commonRuleFunctionDataNew.paging(RdeModelAntiFraudList, rdeModelAntiFraud.getPageNum(), rdeModelAntiFraud.getPageSize());
        Object data = paging.get("data");
        return AjaxResult.success(data);
    }

    @PostMapping("/getById")
    public AjaxResult getById(@RequestBody RdeModelAntiFraudVO record, HttpServletRequest request) {
        return AjaxResult.success("成功",service.get(record));
    }

    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable Integer id) {
        return AjaxResult.success(service.selectById(id));
    }

    @Log(title = "反欺诈模型新增", businessType = BusinessType.INSERT)
    @RequestMapping("submit")
    public AjaxResult submit(@Validated @RequestBody RdeModelAntiFraudDTO dto) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        RdeModelAntiFraudVO record = new RdeModelAntiFraudVO();
        BeanUtils.copyProperties(dto,record);
        if(loginUser == null){
            return AjaxResult.error("用户未登录");
        }
        if(StrUtil.isBlank(record.getProjectCode())){
            return AjaxResult.error("左侧产品导航标识不能为空");
        }

        // 判断是新增还是修改
        boolean isInsert = (record.getId() == null);

        int submit = service.submit(record, null);
        if (submit > 0 && record.getJudgment() == 0){
            record.setDeptId(loginUser.getSysUser().getDeptId().intValue());
            RdeModelAntiFraud rdeModelAntiFraud = service.selectModelId(record);

            //版本归类表插入新纪录 只更新版本号字段
            ModelVersionClassification modelVersionClassification = new ModelVersionClassification();
            modelVersionClassification.setModelId(rdeModelAntiFraud.getId());
            modelVersionClassification.setModelName(record.getName());
            modelVersionClassification.setVersionControl(rdeModelAntiFraud.getVersionControl());
            modelVersionClassification.setDeptId(loginUser.getSysUser().getDeptId().intValue());
            modelVersionClassification.setProjectCode(record.getProjectCode());
            modelVersionClassification.setBusinessCode(record.getBusinessCode());
            modelVersionClassification.setRuleCode(record.getRuleCode());
            modelVersionClassification.setCreateTime(new Date());
            modelVersionClassification.setNewVersion(1);
            modelVersionClassificationService.save(modelVersionClassification);
            //保留原版本
            VersionControlVO versionControlVO = new VersionControlVO();
            versionControlVO.setDeptId(loginUser.getSysUser().getDeptId().intValue());
            versionControlVO.setBusinessCode(record.getBusinessCode());
            versionControlVO.setProjectCode(record.getProjectCode());
            versionControlVO.setRuleCode(record.getRuleCode());
            versionControlVO.setModelName(record.getName());
            versionControlVO.setBusinessName(record.getBusinessName());
            versionControlVO.setPersonOrCompany(record.getPersonOrCompany());
            rdeModelAntiFraudVersionService.versionReserveFraud(versionControlVO);
        }
        return toAjax(submit);
    }

    @Log(title = "反欺诈模型编辑", businessType = BusinessType.UPDATE)
    @RequestMapping("update")
    public AjaxResult update(@Validated @RequestBody RdeModelAntiFraudVO record) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(loginUser == null){
            return AjaxResult.error("用户未登录");
        }
        if(StrUtil.isBlank(record.getProjectCode())){
            return AjaxResult.error("左侧产品导航标识不能为空");
        }
        return toAjax(service.submit(record,null));
    }

    @PostMapping("/checkName")
    public AjaxResult checkName(@RequestBody RdeModelAntiFraud record){
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(loginUser == null){
            return AjaxResult.error("用户未登录");
        }
        return service.checkName(record,SecurityUtils.getLoginUser());
    }

//    @PostMapping("/delete/{id}")
//    @Log(title = "反欺诈模型删除", businessType = BusinessType.DELETE)
//    public AjaxResult delete(@RequestBody RdeNewModelAntiFraudVO record) {
//        return toAjax(service.delete(record));
//    }

    @RequestMapping("/delete")
    @Log(title = "反欺诈模型删除", businessType = BusinessType.DELETE)
    public AjaxResult deleteById(@RequestBody RdeModelAntiFraudVO record) {
        // 校验当前规则明细是否被流程策略引用
        boolean isReferenced = processPolicyReferenceService.checkIfReferencedByProcessPolicy(record.getId());
        if (isReferenced) {
            return AjaxResult.success("检测到目前正在被流程使用，无法删除",false);
        }
        int i = service.deleteById(record.getId());
        return AjaxResult.success("删除成功",true);
    }

    @Log(title = "反欺诈模型更新状态", businessType = BusinessType.INSERT)
    @PostMapping("/updateState")
    public AjaxResult updateState(@RequestBody RdeUpdateStateEntryVO rdeUpdateStateEntryVO){
        service.updateState(rdeUpdateStateEntryVO,null);
        return AjaxResult.success();
    }
}
