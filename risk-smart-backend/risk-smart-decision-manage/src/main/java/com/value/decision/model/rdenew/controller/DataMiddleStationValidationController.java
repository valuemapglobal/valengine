package com.value.decision.model.rdenew.controller;


import com.risksmart.common.core.web.AjaxResult;
import com.value.decision.model.rdenew.service.DataMiddleStationValidationService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Austin
 * Create by 2025/6/18 9:29
 */
@Log4j2
@RestController
@RequestMapping("/rdenew/dataMiddleStation")
public class DataMiddleStationValidationController {

    @Autowired
    private DataMiddleStationValidationService validationService;

    /**
     * 检查DATA_MIDDLE_STATION标识符是否被引用
     * @param id 接口id
     * @return 检查结果
     */
    @GetMapping("/checkReferences/{id}")
    public AjaxResult checkReferences(@PathVariable String id) {
        try {
            boolean isReferenced = validationService.checkDataMiddleStationReference(id);

            if (!isReferenced) {
                return AjaxResult.success("没有找到被引用的接口",true);
            } else {
                return AjaxResult.success("接口被引用，无法删除: " + id,false);
            }
        } catch (Exception e) {
            log.error("检查DATA_MIDDLE_STATION标识符引用时发生异常", e);
            return AjaxResult.error("检查DATA_MIDDLE_STATION标识符引用时发生异常: " + e.getMessage());
        }
    }
}
