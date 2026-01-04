package com.value.data.controller;


import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.value.data.common.constant.InterfaceFieldIdTypeEnum;
import com.value.data.common.model.LoginUser;
import com.value.data.common.utils.AjaxResult;
import com.value.data.common.utils.SecurityUtils;
import com.value.data.domain.entity.InterfaceFieldIdManage;
import com.value.data.service.InterfaceFieldIdManageService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/interfaceFieldId")
@AllArgsConstructor
public class InterfaceFieldIdManageController {
    private final InterfaceFieldIdManageService service;
    private final ObjectMapper objectMapper;

    @PostMapping("/import")
    public AjaxResult importInterfaceFieldId(@RequestParam String interfaceManageNo,
                                           @RequestParam String interfaceNo,
                                           @RequestParam InterfaceFieldIdTypeEnum type,
                                           @RequestParam String fieldJson,
                                           HttpServletRequest request) throws JsonProcessingException {
        LoginUser user = SecurityUtils.getLoginUser(request);
        //校验
        if (!JSONUtil.isJson(fieldJson)) {
            return AjaxResult.badRequest("格式错误，请检查格式是否为json");
        }
        JsonNode jsonNode = objectMapper.readTree(fieldJson);
        if (jsonNode.isArray()){jsonNode = jsonNode.path(0);}
        if (jsonNode.isNull() || jsonNode.isMissingNode()){return AjaxResult.success(0);}

        //解析字段并填充数据
        final List<InterfaceFieldIdManage> fields = service.analyzeInterfaceField(interfaceManageNo,interfaceNo,type,jsonNode, user);
        //查询已存在的字段
        final List<String> names = fields.stream().map(InterfaceFieldIdManage::getInterfaceFieldIdName).collect(Collectors.toList());
        final List<InterfaceFieldIdManage> existingList = service.lambdaQuery()
                .eq(InterfaceFieldIdManage::getInterfaceManageNo, interfaceManageNo)
                .eq(InterfaceFieldIdManage::getInterfaceFieldIdType,type.getCode())
                .in(InterfaceFieldIdManage::getInterfaceFieldIdName, names)
                .list();
        final Map<String, InterfaceFieldIdManage> existingMap = existingList.stream()
                .collect(Collectors.toMap(InterfaceFieldIdManage::getInterfaceFieldIdName, field -> field));

        final List<InterfaceFieldIdManage> saveFields = fields.stream()
                .filter(f -> existingMap.get(f.getInterfaceFieldIdName())==null)
                .filter(service::checkInterfaceField)
                .collect(Collectors.toList());

        return AjaxResult.success(service.saveFields(saveFields));
    }
}
