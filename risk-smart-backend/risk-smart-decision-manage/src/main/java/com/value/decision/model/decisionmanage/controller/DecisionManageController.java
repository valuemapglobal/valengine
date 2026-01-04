package com.value.decision.model.decisionmanage.controller;

import cn.hutool.core.util.IdUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.risksmart.common.core.web.AjaxResult;
import com.value.decision.model.decisionmanage.model.dto.ImportRuleWithJsonDTO;
import com.value.decision.model.decisionmanage.service.DecisionManageService;
import com.value.decision.common.utils.security.SecurityUtils;
import com.value.decision.common.security.LoginUser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Vida
 * @date 2025年03月05日 14:45
 * @description
 */
@RestController
@RequestMapping("/decision-manage")
@AllArgsConstructor
@Slf4j
public class DecisionManageController {
    private final DecisionManageService service;

    @GetMapping(value = "/export-rule/json",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> exportRuleWithJson(@RequestParam Integer strategyId){
        // 创建JSON内容
        String jsonContent;
        try {
            jsonContent = service.getRuleJson(strategyId);
        }catch (JsonProcessingException e) {
            log.error("JSON序列化失败", e);
            jsonContent = String.format("{\"error\": %s}",e.getMessage());
        }


        // 设置HTTP头部信息
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setContentDispositionFormData("attachment", getName(strategyId)+".json");
        headers.setCacheControl("no-cache, no-store, must-revalidate");

        // 返回ResponseEntity，包含JSON内容、HTTP头部和状态码
        return new ResponseEntity<>(jsonContent, headers, HttpStatus.OK);
    }

    @GetMapping(value = "/export-rule/excel")
    public void exportRuleWithExcel(@RequestParam Integer strategyId, HttpServletResponse response) throws IOException {
        final Path excel = Paths.get(getName(strategyId)+".xlsx");
        service.exportRuleToExcel(strategyId,excel);

        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader( "Content-Disposition", String.format("attachment; filename=\"%s\"",excel.toFile().getName()));
        Files.copy(excel, response.getOutputStream());
        response.getOutputStream().flush();
        Files.deleteIfExists(excel);
    }

    private String getName(Integer strategyId){
       return String.format("strategy-%s-%s",IdUtil.fastSimpleUUID(),strategyId);
    }

    @PostMapping("/import-rule/json")
    public AjaxResult importRuleWithJson(@Validated ImportRuleWithJsonDTO params, HttpServletRequest request){
        final LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser==null){ return AjaxResult.error("未登录"); }
        final int count = service.importRuleByJson(params, loginUser);
        if (count<0){
            return AjaxResult.error("导入失败："+count);
        }
        return AjaxResult.success("导入成功："+count);
    }
}
