package com.value.data.controller;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.risksmart.common.core.utils.StringUtils;
import com.value.data.common.model.LoginUser;
import com.value.data.common.utils.AjaxResult;
import com.value.data.common.utils.SecurityUtils;
import com.value.data.constant.ServiceConstant;
import com.value.data.domain.dto.FindAllLogDTO;
import com.value.data.domain.entity.InterfaceLog;
import com.value.data.domain.entity.InterfaceManage;
import com.value.data.domain.vo.CountLogVO;
import com.value.data.domain.vo.FindAllLogVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/interfaceLog")
@Slf4j(topic = "[InterfaceLogController]")
public class InterfaceLogController {

    private ServiceConstant serviceConstant;

    @Autowired
    public InterfaceLogController(ServiceConstant serviceConstant){
        this.serviceConstant = serviceConstant;
    }

    /**
     * 获取日志信息
     * @param findAllLogDTO
     * @param request
     * @return
     */
    @PostMapping("/findAllLog")
    public AjaxResult findAllLog(@Valid @RequestBody FindAllLogDTO findAllLogDTO, HttpServletRequest request){
        //效验用户登录
        LoginUser user = SecurityUtils.getLoginUser(request);
        try {
            LambdaQueryWrapper<InterfaceLog> lambdaQuery = Wrappers.lambdaQuery();
            if (StrUtil.isNotBlank(findAllLogDTO.getInterfaceNameZh())){
                List<InterfaceManage> interfaceManages = serviceConstant.interfaceManageService.lambdaQuery()
                        .like(InterfaceManage::getInterfaceName, findAllLogDTO.getInterfaceNameZh())
                        .select(InterfaceManage::getInterfaceNo)
                        .list();
                List<String> list = interfaceManages.stream().map(InterfaceManage::getInterfaceNo).collect(Collectors.toList());
                if (CollUtil.isEmpty(list)){
                    return AjaxResult.success(new PageInfo<>(new ArrayList<FindAllLogVO>()));
                }
                lambdaQuery.in(InterfaceLog::getInterfaceName,list);
            }
            //搜索某个供应商下的接口
            if (StringUtils.isNotEmpty(findAllLogDTO.getSourceNo())){
                LambdaQueryWrapper<InterfaceManage> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(InterfaceManage::getInterfaceSourceNo,findAllLogDTO.getSourceNo());
                List<InterfaceManage> list = serviceConstant.interfaceManageService.list(wrapper);
                if (list!=null){
                    List<String> manageNos = list.stream().map(inter -> inter.getInterfaceManageNo()).collect(Collectors.toList());
                    if (manageNos.isEmpty()){
                        return AjaxResult.success(new PageInfo<>(new ArrayList<FindAllLogVO>()));
                    }
                    lambdaQuery.in(InterfaceLog::getInterfaceManageNo,manageNos);
                }
            }

            lambdaQuery.eq(InterfaceLog::getDeptId,user.getSysUser().getDeptId());
            lambdaQuery.like(StrUtil.isNotBlank(findAllLogDTO.getCreateBy()),InterfaceLog::getCreateBy,findAllLogDTO.getCreateBy());
            lambdaQuery.like(StrUtil.isNotBlank(findAllLogDTO.getInterfaceName()),InterfaceLog::getInterfaceName,findAllLogDTO.getInterfaceName());
            lambdaQuery.like(StrUtil.isNotBlank(findAllLogDTO.getUserDept()),InterfaceLog::getUserDept, findAllLogDTO.getUserDept());
            lambdaQuery.like(StrUtil.isNotBlank(findAllLogDTO.getUserId()),InterfaceLog::getUserId, findAllLogDTO.getUserId());
            if (StrUtil.isNotBlank(findAllLogDTO.getState())){
                lambdaQuery.eq(Integer.parseInt(findAllLogDTO.getState()) == 0,InterfaceLog::getCode, "200");
                lambdaQuery.eq(Integer.parseInt(findAllLogDTO.getState()) == 1,InterfaceLog::getCode, "500");
            }
            lambdaQuery.between(InterfaceLog::getAccessTime,findAllLogDTO.getStartTime(),findAllLogDTO.getEndTime());
            lambdaQuery.orderByDesc(InterfaceLog::getId);
            PageHelper.startPage(findAllLogDTO.getPageNum(), findAllLogDTO.getPageSize());
            List<InterfaceLog> interfaceLogList = serviceConstant.interfaceLogService.list(lambdaQuery);
            PageInfo<InterfaceLog> page = new PageInfo<>(interfaceLogList);

            List<FindAllLogVO> findAllLogVOList = JSON.parseArray(JSON.toJSONString(interfaceLogList, JSONWriter.Feature.ReferenceDetection),FindAllLogVO.class);
            //接口中文名称
            if (findAllLogVOList!=null && !findAllLogVOList.isEmpty()){
                findAllLogVOList.forEach(log -> {
                    LambdaQueryWrapper<InterfaceManage> wrapper = new LambdaQueryWrapper<>();
                    wrapper.eq(InterfaceManage::getInterfaceManageNo,log.getInterfaceManageNo());
                    InterfaceManage one = serviceConstant.interfaceManageService.getOne(wrapper);
                    if (one==null)
                        return;
                    log.setInterfaceNameZh(one.getInterfaceName());
                });
            }
            PageInfo<FindAllLogVO> pageInfo = new PageInfo<>();
            BeanUtil.copyProperties(page,pageInfo);
            pageInfo.setList(findAllLogVOList);

            return AjaxResult.success(pageInfo);
        }catch (Exception e){
            log.error(e.getMessage(), e);
            return AjaxResult.error("业务中断");
        }
    }


    /**
     * 数据报表统计
     * @param request
     * @return
     */
    @PostMapping("/countLog")
    public AjaxResult countLog(HttpServletRequest request){
        //效验用户登录
        LoginUser user = SecurityUtils.getLoginUser(request);
        try {
            CountLogVO countLogVO = serviceConstant.interfaceLogService.countLog(user.getSysUser().getDeptId());
            return AjaxResult.success(countLogVO);
        }catch (Exception e){
            log.error(e.getMessage(), e);
            return AjaxResult.error("业务中断");
        }
    }

}
