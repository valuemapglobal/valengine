package com.value.data.controller;

import com.value.data.common.utils.AjaxResult;
import com.value.data.domain.dto.InterfaceQueryDTO;
import com.value.data.service.DecisionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 决策策略控制器
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@RestController
@RequestMapping("/decision")
@Slf4j
@AllArgsConstructor
public class DecisionController {
    private final DecisionService decisionService;

    @PostMapping("/quoted-information")
    public AjaxResult decisionQI(@RequestBody InterfaceQueryDTO dto ){
        return AjaxResult.success(decisionService.decisionQI(dto));
    }
}
