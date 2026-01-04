package com.value.data.controller;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.JsonObject;
import com.value.data.common.model.LoginUser;
import com.value.data.common.utils.AjaxResult;
import com.value.data.common.utils.ExcelUtils;
import com.value.data.common.utils.SecurityUtils;
import com.value.data.constant.IdConstant;
import com.value.data.constant.ServiceConstant;
import com.value.data.converter.WrapperConverter;
import com.value.data.domain.dto.*;
import com.value.data.domain.entity.InterfaceFieldIdManage;
import com.value.data.domain.entity.InterfaceManage;
import com.value.data.domain.entity.InterfaceSourceManage;
import com.value.data.domain.vo.*;
import com.value.data.mapper.InterfaceManageMapper;
import com.value.data.service.InterfaceService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/interfaceManage")
@Slf4j(topic = "[InterfaceManageController]")
public class InterfaceManageController {

    private final ServiceConstant serviceConstant;
    private final InterfaceService interfaceService;

    @Resource
    private InterfaceManageMapper interfaceManageMapper;

    @Autowired
    public InterfaceManageController(ServiceConstant serviceConstant, InterfaceService interfaceService) {
        this.serviceConstant = serviceConstant;
        this.interfaceService = interfaceService;
    }



    /**
     * 接口计费列表
     */
    @PostMapping("/charging")
    public AjaxResult interfaceCharging(@RequestBody InterfaceChargingDTO interfaceChargingDTO,HttpServletRequest request){
        LoginUser loginUser = SecurityUtils.getLoginUser(request);
        Long deptId = loginUser.getSysUser().getDeptId();
        interfaceChargingDTO.setDeptId(deptId);
        InterfaceChargingVo interfaceChargingVo = interfaceService.interfaceCharging(interfaceChargingDTO);
        return AjaxResult.success(interfaceChargingVo);
    }
    /**
     * 接口计费导出
     */
    @PostMapping("/export")
    public void export(@RequestBody InterfaceChargingDTO interfaceChargingDTO,HttpServletResponse response,HttpServletRequest request) {
        LoginUser loginUser = SecurityUtils.getLoginUser(request);
        if (loginUser.getSysUser()!=null){
            interfaceChargingDTO.setDeptId(loginUser.getSysUser().getDeptId());
        }
        List<InterfaceChargingListVo> listVos =  interfaceManageMapper.queryInterfaceChargingList(interfaceChargingDTO);
        List<ChargingExportDTO> chargingExportDTOS = new ArrayList<>();
        listVos.forEach(vo->{
            ChargingExportDTO chargingExportDTO = new ChargingExportDTO();

            vo.setOrderNo(vo.getOrderId());
            BeanUtils.copyProperties(vo,chargingExportDTO);
            if(vo.getInterfaceType()!=null){
                if(vo.getInterfaceType()==1){
                    chargingExportDTO.setInterfaceType("企业");
                }
                if(vo.getInterfaceType()==0){
                    chargingExportDTO.setInterfaceType("个人");
                }
            }
            //是否计费
            if(vo.getChargingFlag()!=null){
                if(vo.getChargingFlag()==1){
                    chargingExportDTO.setChargingFlag("是");
                }
                if(vo.getChargingFlag()==0){
                    chargingExportDTO.setChargingFlag("否");
                }
            }


            chargingExportDTOS.add(chargingExportDTO);
        });

        ExcelUtils.download(response, chargingExportDTOS, ChargingExportDTO.class, "接口计费统计");
    }





    /**
     * 保存供应商信息
     */
    @PostMapping("/saveSourceInfo")
    public AjaxResult saveSourceInfo(@Valid @RequestBody SaveSourceManageDTO saveSourceManageDTO, HttpServletRequest request){
        //效验用户登录
        LoginUser loginUser = SecurityUtils.getLoginUser(request);
        InterfaceSourceManage interfaceSourceManage = new InterfaceSourceManage();
        BeanUtil.copyProperties(saveSourceManageDTO, interfaceSourceManage, false);
        //唯一标识
        String no = IdConstant.DATA_MIDDLE_STATION_PREFIX.concat(String.valueOf(serviceConstant.idGeneratorService.generateUniqueId()));
        interfaceSourceManage.setInterfaceSourceNo(no);

        //保存供应商
        interfaceService.saveSourceInfo(interfaceSourceManage,loginUser);
        return AjaxResult.success();
    }

    /**
     * 修改供应商信息
     */
    @PostMapping("/updateSourceInfo")
    public AjaxResult updateSourceInfo(@Valid @RequestBody UpdateSourceInfoDTO updateSourceInfoDTO, HttpServletRequest request){
        //效验用户登录
        LoginUser loginUser = SecurityUtils.getLoginUser(request);
        //效验被修改数据是否存在
        Boolean validNoBool = serviceConstant.interfaceSourceManageService.validSourceNo(updateSourceInfoDTO.getSourceNo());
        if (!validNoBool){
            //拦截不允许修改
            return AjaxResult.error("供应商信息不存在");
        }

        interfaceService.updateSourceInfo(updateSourceInfoDTO,loginUser);

        return AjaxResult.success();
    }

    /**
     * 移除供应商信息
     */
    @PostMapping("/removeSourceInfo")
    public AjaxResult removeSourceInfo(@Valid @RequestBody RemoveSourceInfo removeSourceInfo, HttpServletRequest request){
        //效验用户登录
        final LoginUser loginUser = SecurityUtils.getLoginUser(request);
        //效验被移除数据是否存在
        Boolean validNoBool = serviceConstant.interfaceSourceManageService.validSourceNo(removeSourceInfo.getSourceNo());
        if (!validNoBool){ return AjaxResult.error("供应商信息不存在"); }
        //并效验子集数据是否存在
        Boolean validSourceNoBool = serviceConstant.interfaceManageService.validSourceNo(removeSourceInfo.getSourceNo());
        if (validSourceNoBool){ return AjaxResult.error("存在接口信息，无法移除"); }
        interfaceService.removeSourceInfo(removeSourceInfo,loginUser);

        return AjaxResult.success();
    }

    /**
     * 获取供应商信息
     */
    @PostMapping("/findSourceInfo")
    public AjaxResult findSourceInfo(@Valid @RequestBody FindSourceInfoDTO findSourceInfoDTO, HttpServletRequest request){
        //效验用户登录
        final LoginUser loginUser = SecurityUtils.getLoginUser(request);
        final LambdaQueryWrapper<InterfaceSourceManage> queryWrapper = WrapperConverter.convert(findSourceInfoDTO);
        queryWrapper.eq(InterfaceSourceManage::getDeptId,loginUser.getSysUser().getDeptId());
        //分页
        if(findSourceInfoDTO.getPageNum()!=null && findSourceInfoDTO.getPageSize()!=null){
            PageHelper.startPage(findSourceInfoDTO.getPageNum(), findSourceInfoDTO.getPageSize());
        }
        List<InterfaceSourceManage> sourceInfoList = serviceConstant.interfaceSourceManageService.list(queryWrapper);
        final PageInfo<InterfaceSourceManage> page = new PageInfo<>(sourceInfoList);

        //反序列化 深拷贝集合
        List<FindSourceInfoVO> sourceInfoVOList = JSON.parseArray(
                JSON.toJSONString(sourceInfoList, JSONWriter.Feature.ReferenceDetection),
                FindSourceInfoVO.class
        );

        PageInfo<FindSourceInfoVO> pageInfo = new PageInfo<>();
        BeanUtil.copyProperties(page,pageInfo);
        pageInfo.setList(sourceInfoVOList);

        return AjaxResult.success(pageInfo);
    }


    /**
     * 保存接口信息
     */
    @PostMapping("/saveInterfaceInfo")
    public AjaxResult saveInterfaceInfo(@Valid @RequestBody SaveInterfaceInfoDTO saveInterfaceInfoDTO, HttpServletRequest request){
        //效验用户登录
        LoginUser loginUser = SecurityUtils.getLoginUser(request);
        //对象填充
        InterfaceManage interfaceManage = new InterfaceManage();
        BeanUtil.copyProperties(saveInterfaceInfoDTO, interfaceManage, false);
        interfaceManage.setInterfaceSourceNo(saveInterfaceInfoDTO.getSourceNo());
        //保存
        interfaceService.saveInterfaceInfo(interfaceManage,loginUser);
        return AjaxResult.success();
    }


    /**
     * 修改接口信息
     */
    @PostMapping("/updateInterfaceInfo")
    public AjaxResult updateInterfaceInfo(@Valid @RequestBody UpdateInterfaceInfo updateInterfaceInfo, HttpServletRequest request){
        //效验用户登录
        LoginUser loginUser = SecurityUtils.getLoginUser(request);
        //效验被移除数据是否存在
        Boolean validSourceNoBool = serviceConstant.interfaceSourceManageService.validSourceNo(updateInterfaceInfo.getSourceNo());
        if (!validSourceNoBool){ return AjaxResult.error("供应商信息不存在"); }
        //效验被修改数据是否存在
        Boolean validNoBool = serviceConstant.interfaceManageService.validManageNo(updateInterfaceInfo.getManageNo());
        if (!validNoBool){ return AjaxResult.error("接口信息不存在"); }
        //效验编号
        Long count = serviceConstant.interfaceManageService.lambdaQuery()
                .eq(InterfaceManage::getInterfaceNo, updateInterfaceInfo.getInterfaceNo())
                .ne(InterfaceManage::getInterfaceManageNo, updateInterfaceInfo.getManageNo())
                .eq(InterfaceManage::getDeptId, loginUser.getSysUser().getDeptId())
                .count();
        if (count > 0){return AjaxResult.error("编号信息已存在");}

        interfaceService.updateInterfaceInfo(updateInterfaceInfo,loginUser);
        //修改主题
        return AjaxResult.success();
    }

    /**
     * 移除接口信息
     */
    @PostMapping("/removeInterfaceInfo")
    public AjaxResult removeInterfaceInfo(@Valid @RequestBody RemoveInterfaceInfoDTO removeInterfaceInfoDTO, HttpServletRequest request){
        //效验用户登录
        LoginUser user = SecurityUtils.getLoginUser(request);
        //效验被删除数据是否存在
        Boolean validNoBool = serviceConstant.interfaceManageService.validManageNo(removeInterfaceInfoDTO.getManageNo());
        if (!validNoBool){ return AjaxResult.error("接口信息不存在"); }
        //效验是否有子数据
        Boolean validManageNoBool = serviceConstant.interfaceFieldIdManageService.validManageNo(removeInterfaceInfoDTO.getManageNo());
        if (validManageNoBool){ return AjaxResult.error("存在参数信息，无法移除"); }

        interfaceService.removeInterfaceInfo(removeInterfaceInfoDTO,user);
        //删除主题
        return AjaxResult.success();
    }


    /**
     * 获取接口信息
     */
    @PostMapping("/findInterfaceInfo")
    public AjaxResult findInterfaceInfo(@Valid @RequestBody FindInterfaceInfoDTO findInterfaceInfoDTO, HttpServletRequest request){
        //效验用户登录
        LoginUser loginUser = SecurityUtils.getLoginUser(request);

        LambdaQueryWrapper<InterfaceManage> lambdaQuery = Wrappers.lambdaQuery();
        lambdaQuery.eq(InterfaceManage::getDataStatus, 0)
                .eq(InterfaceManage::getInterfaceSourceNo, findInterfaceInfoDTO.getSourceNo())
                .like(StrUtil.isNotBlank(findInterfaceInfoDTO.getInterfaceName()),InterfaceManage::getInterfaceName,findInterfaceInfoDTO.getInterfaceName())
                .eq(!Objects.isNull(findInterfaceInfoDTO.getInterfaceType()),InterfaceManage::getInterfaceType,findInterfaceInfoDTO.getInterfaceType())
                .between(
                        !Objects.isNull(findInterfaceInfoDTO.getBeginTime())&&!Objects.isNull(findInterfaceInfoDTO.getEndTime()),
                        InterfaceManage::getCreateTime,findInterfaceInfoDTO.getBeginTime(),findInterfaceInfoDTO.getEndTime()
                )
                .eq(InterfaceManage::getDeptId,loginUser.getSysUser().getDeptId());
        //分页
        if (findInterfaceInfoDTO.getPageNum()!=null && findInterfaceInfoDTO.getPageSize()!=null)
            PageHelper.startPage(findInterfaceInfoDTO.getPageNum(), findInterfaceInfoDTO.getPageSize());
        List<InterfaceManage> sourceInfoList = serviceConstant.interfaceManageService.list(lambdaQuery);
        //反序列化 深拷贝集合
        log.error(JSON.toJSONString(sourceInfoList, JSONWriter.Feature.ReferenceDetection));
        List<FindInterfaceInfoVO> interfaceInfoVOList = JSON.parseArray(JSON.toJSONString(sourceInfoList, JSONWriter.Feature.ReferenceDetection),FindInterfaceInfoVO.class);
        PageInfo<FindInterfaceInfoVO> pageInfo = new PageInfo<>(interfaceInfoVOList);
        pageInfo.setTotal(serviceConstant.interfaceManageService.count(lambdaQuery));

        return AjaxResult.success(pageInfo);
    }

    /**
     * 新增接口参数
     */
    @PostMapping("/saveInterfaceFieldIdInfo")
    public AjaxResult saveInterfaceFieldIdInfo(@Valid @RequestBody SaveInterfaceFieldIdInfoDTO saveInterfaceFieldIdInfoDTO, HttpServletRequest request) throws Exception {
        //效验用户登录
        LoginUser loginUser = SecurityUtils.getLoginUser(request);
        //参数复制
        InterfaceFieldIdManage interfaceFieldIdManage = new InterfaceFieldIdManage();
        BeanUtil.copyProperties(saveInterfaceFieldIdInfoDTO, interfaceFieldIdManage, false);
        interfaceFieldIdManage.setInterfaceManageNo(saveInterfaceFieldIdInfoDTO.getManageNo());

        interfaceService.saveInterfaceFieldIdInfo(interfaceFieldIdManage,loginUser);
        return AjaxResult.success();
    }

    /**
     * 接口参数更新
     */
    @SneakyThrows
    @PostMapping("/updateInterfaceFieldIdInfo")
    public AjaxResult updateInterfaceFieldIdInfo(@Valid @RequestBody UpdateInterfaceFieldIdInfoDTO updateInterfaceFieldIdInfoDTO, HttpServletRequest request){
        //效验用户登录
        LoginUser loginUser = SecurityUtils.getLoginUser(request);
        //效验被数据是否存在
        Boolean validNoBool = serviceConstant.interfaceManageService.validManageNo(updateInterfaceFieldIdInfoDTO.getManageNo());
        if (!validNoBool){ return AjaxResult.error("接口信息不存在"); }
        Boolean aBoolean = serviceConstant.interfaceFieldIdManageService.validName(
                updateInterfaceFieldIdInfoDTO.getManageNo(),
                updateInterfaceFieldIdInfoDTO.getInterfaceFieldIdName(),
                Integer.parseInt(updateInterfaceFieldIdInfoDTO.getInterfaceFieldIdType()),
                updateInterfaceFieldIdInfoDTO.getInterfaceFieldIdFather(),
                updateInterfaceFieldIdInfoDTO.getFieldIdNo()
        );
        if (aBoolean){ return AjaxResult.error("参数名称已存在"); }
        Boolean bBoolean = serviceConstant.interfaceFieldIdManageService.validAlias(
                updateInterfaceFieldIdInfoDTO.getManageNo(),
                updateInterfaceFieldIdInfoDTO.getInterfaceFieldIdAlias(),
                Integer.parseInt(updateInterfaceFieldIdInfoDTO.getInterfaceFieldIdType()),
                updateInterfaceFieldIdInfoDTO.getInterfaceFieldIdFather(),
                updateInterfaceFieldIdInfoDTO.getFieldIdNo()
        );
        if (bBoolean){ return AjaxResult.error("参数别名已存在"); }
        //同步更新接口平台和数据平台
        interfaceService.updateInterfaceFieldIdInfo(updateInterfaceFieldIdInfoDTO,loginUser);
        return AjaxResult.success();
    }


    /**
     * 移除接口参数信息
     */
    @PostMapping("/removeInterfaceFieldIdInfo")
    public AjaxResult removeInterfaceFieldIdInfo(@Valid @RequestBody RemoveInterfaceFieldIdInfoDTO removeInterfaceFieldIdInfoDTO, HttpServletRequest request){
        //效验用户登录
        LoginUser user = SecurityUtils.getLoginUser(request);

        Boolean validInterfaceFieldIdBool = serviceConstant.interfaceFieldIdManageService.validInterfaceFieldIdNo(removeInterfaceFieldIdInfoDTO.getFieldIdNo());
        if (!validInterfaceFieldIdBool){return AjaxResult.error("参数信息不存在");}
        interfaceService.removeInterfaceFieldIdInfo(removeInterfaceFieldIdInfoDTO,user);
        return AjaxResult.success(Boolean.TRUE);
    }

    /**
     * 获取接口参数信息
     */
    @PostMapping("/findInterfaceFieldIdInfo")
    public AjaxResult findInterfaceFieldIdInfo(@Valid @RequestBody FindInterfaceFieldIdInfoDTO findInterfaceFieldIdInfoDTO, HttpServletRequest request){
        //效验用户登录
        LoginUser user = SecurityUtils.getLoginUser(request);

        final LambdaQueryWrapper<InterfaceFieldIdManage> lambdaQuery = WrapperConverter.convert(findInterfaceFieldIdInfoDTO);
        lambdaQuery.eq(InterfaceFieldIdManage::getDeptId,user.getSysUser().getDeptId());

        //分页
        if (findInterfaceFieldIdInfoDTO.getPageNum()!=null && findInterfaceFieldIdInfoDTO.getPageSize()!=null){
            PageHelper.startPage(findInterfaceFieldIdInfoDTO.getPageNum(), findInterfaceFieldIdInfoDTO.getPageSize());
        }
        List<InterfaceFieldIdManage> sourceInfoList = serviceConstant.interfaceFieldIdManageService.list(lambdaQuery);


        //反序列化 深拷贝集合
        List<FindInterfaceFieldIdInfoVO> findInterfaceFieldIdInfoVOList = JSON.parseArray(JSON.toJSONString(sourceInfoList, JSONWriter.Feature.ReferenceDetection),FindInterfaceFieldIdInfoVO.class);

        PageInfo<FindInterfaceFieldIdInfoVO> pageInfo = new PageInfo<>(findInterfaceFieldIdInfoVOList);
        pageInfo.setTotal(serviceConstant.interfaceFieldIdManageService.count(lambdaQuery));
        return AjaxResult.success(pageInfo);
    }


    /**
     * 接口应用开关
     */
    @PostMapping("/interfaceOn")
    public AjaxResult interfaceOn(@Valid @RequestBody InterfaceOnDTO interfaceOnDTO,HttpServletRequest request){
        //效验用户登录
        LoginUser loginUser = SecurityUtils.getLoginUser(request);
        //效验被数据是否存在
        Boolean validNoBool = serviceConstant.interfaceManageService.validManageNo(interfaceOnDTO.getManageNo());
        if (!validNoBool){
            //拦截不允许修改
            return AjaxResult.error("接口信息不存在");
        }
        //效验被移除数据是否存在
        Boolean validSourceNoBool = serviceConstant.interfaceSourceManageService.validSourceNo(interfaceOnDTO.getSourceNo());
        if (!validSourceNoBool){
            //拦截不允许修改
            return AjaxResult.error("供应商信息不存在");
        }

        LambdaQueryWrapper<InterfaceManage> lambdaQuery = Wrappers.lambdaQuery();
        lambdaQuery.eq(InterfaceManage::getDataStatus, 0)
                .eq(InterfaceManage::getInterfaceSourceNo, interfaceOnDTO.getSourceNo())
                .eq(InterfaceManage::getInterfaceManageNo, interfaceOnDTO.getManageNo())
                .eq(InterfaceManage::getDeptId,loginUser.getSysUser().getDeptId());

        InterfaceManage interfaceManage = new InterfaceManage();
        BeanUtil.copyProperties(interfaceOnDTO, interfaceManage, false);
        interfaceManage.setUpdateBy(loginUser.getSysUser().getUserName());

        serviceConstant.interfaceManageService.update(interfaceManage, lambdaQuery);
        return AjaxResult.success();
    }

    /**
     * 获取树级列表
     */
    @GetMapping("/tree")
    public AjaxResult getTree(HttpServletRequest request){
        //效验用户登录
        LoginUser user = SecurityUtils.getLoginUser(request);

        LambdaQueryWrapper<InterfaceSourceManage> queryWrapper = Wrappers.lambdaQuery(InterfaceSourceManage.class);
        queryWrapper.eq(InterfaceSourceManage::getDeptId,user.getSysUser().getDeptId());
        List<InterfaceSourceManage> list = serviceConstant.interfaceSourceManageService.list(queryWrapper);
        if (list==null){return AjaxResult.success();}

        List<HashMap<String, Object>> collect = list.stream().map(source -> {
            HashMap<String, Object> sourceMap = new HashMap<>();
            sourceMap.put("sourceNo", source.getInterfaceSourceNo());
            sourceMap.put("sourceName", source.getDataName());

            LambdaQueryWrapper<InterfaceManage> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(InterfaceManage::getInterfaceSourceNo, source.getInterfaceSourceNo())
                    .eq(InterfaceManage::getDeptId,user.getSysUser().getDeptId());
            List<InterfaceManage> interfaces = serviceConstant.interfaceManageService.list(wrapper);
            if (interfaces == null) {
                sourceMap.put("total", 0);
                return sourceMap;
            }
            sourceMap.put("total", list.size());
            List<HashMap<String, String>> interfaceList = interfaces.stream().map(inter -> {
                HashMap<String, String> interfaceMap = new HashMap<>();
                interfaceMap.put("sourceNo", inter.getInterfaceSourceNo());
                interfaceMap.put("name", inter.getInterfaceName());
                interfaceMap.put("manageNo", inter.getInterfaceManageNo());
                interfaceMap.put("interfaceNo", inter.getInterfaceNo());
                return interfaceMap;
            }).collect(Collectors.toList());
            sourceMap.put("interfaces", interfaceList);
            return sourceMap;
        }).collect(Collectors.toList());

        return AjaxResult.success(collect);
    }

    @PostMapping("/getInterfaceDetail")
    public AjaxResult getInterfaceDetail(@RequestBody GetInterfaceDetailDTO params){
        LambdaQueryWrapper<InterfaceManage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InterfaceManage::getInterfaceSourceNo,params.getSourceNo())
                .eq(InterfaceManage::getInterfaceManageNo,params.getManageNo());
        InterfaceManage result = serviceConstant.interfaceManageService.getOne(wrapper);
        return AjaxResult.success(result);
    }

    /**
     * 根据接口的标识列表获取所有入参的并集并根据英文名去重
     */
    @PostMapping("/getInterfaceInputParameter")
    public AjaxResult getInterfaceInputParameter(@RequestBody @Validated GetInterfaceInputParameterDTO params){
        LambdaQueryWrapper<InterfaceFieldIdManage> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(InterfaceFieldIdManage::getInterfaceManageNo,params.getManageNoList())
                .eq(InterfaceFieldIdManage::getInterfaceFieldIdType,0);//入参
        List<InterfaceFieldIdManage> list = serviceConstant.interfaceFieldIdManageService.list(wrapper);
        if (list==null || list.isEmpty())
            return AjaxResult.success(new JSONArray());
        List<InterfaceInputParameterVO> result = new ArrayList<>();
        ConcurrentHashMap<String, List<InterfaceFieldIdManage>> map = new ConcurrentHashMap<>();
        //按照属性英文名称进行分组
        for (InterfaceFieldIdManage field : list) {
            List<InterfaceFieldIdManage> interfaceFieldIdManages = map.get(field.getInterfaceFieldIdName());
            if (interfaceFieldIdManages==null || interfaceFieldIdManages.isEmpty()){
                ArrayList<InterfaceFieldIdManage> tList = new ArrayList<>();
                tList.add(field);
                map.put(field.getInterfaceFieldIdName(),tList);
            }else {
                interfaceFieldIdManages.add(field);
            }
        }
        //解析
        for (Map.Entry<String, List<InterfaceFieldIdManage>> entry : map.entrySet()) {
            InterfaceInputParameterVO inputParameterVO = new InterfaceInputParameterVO();
            InterfaceInputParameterVO.ParameterInfo parameterInfo = new InterfaceInputParameterVO.ParameterInfo();
            inputParameterVO.setParameterInfo(parameterInfo);

            List<InterfaceFieldIdManage> value = entry.getValue();
            parameterInfo.setNameEn(value.get(0).getInterfaceFieldIdName());
            parameterInfo.setNameZh(value.get(0).getInterfaceFieldIdDescription());
            parameterInfo.setType(value.get(0).getInterfaceFieldIdDataType());

            List<InterfaceInputParameterVO.InterfaceInfo> collect = value.stream().map(p -> {
                InterfaceInputParameterVO.InterfaceInfo interfaceInfo = new InterfaceInputParameterVO.InterfaceInfo();
                interfaceInfo.setManageNo(p.getInterfaceManageNo());
                interfaceInfo.setInterfaceNo(p.getInterfaceNo());
                return interfaceInfo;
            }).collect(Collectors.toList());
            inputParameterVO.setInterfaceInfoList(collect);
            result.add(inputParameterVO);
        }
        return AjaxResult.success(result);
    }

    @GetMapping("/{manageNo}/fields")
    public AjaxResult getFields(@PathVariable String manageNo){
        final InterfaceManage interfaceInfo = serviceConstant.interfaceManageService.getOne(new LambdaQueryWrapper<InterfaceManage>()
                .eq(InterfaceManage::getInterfaceManageNo, manageNo));
        if (interfaceInfo == null){
            return AjaxResult.error("接口不存在");
        }
        final HashMap<String, Object> result = new HashMap<>();
        result.put("manageNo",interfaceInfo.getInterfaceManageNo());//接口编号
        result.put("interfaceName",interfaceInfo.getInterfaceNo());//接口名称
        result.put("interfaceNameZh",interfaceInfo.getInterfaceName());//接口名称(中文）
        result.put("responseType",interfaceInfo.getReturnType());//接口返回值类型
        //处理参数
        final List<InterfaceFieldIdManage> list = serviceConstant.interfaceFieldIdManageService.list(new LambdaQueryWrapper<InterfaceFieldIdManage>()
                .eq(InterfaceFieldIdManage::getInterfaceManageNo, manageNo));
        if (list==null || list.isEmpty()){
            return AjaxResult.success(result);
        }
        final HashMap<String, List<Map<String,Object>>> fields = new HashMap<>();
        //入参
        fields.put("input",list.stream().filter(f -> f.getInterfaceFieldIdType()==0)
                .map(f -> new HashMap<String, Object>() {{
                    put("name",StrUtil.isNotBlank(f.getInterfaceFieldIdAlias())?f.getInterfaceFieldIdAlias():f.getInterfaceFieldIdName());
                    put("type",f.getInterfaceFieldIdDataType());
                    put("nameZh",f.getInterfaceFieldIdDescription());
                    put("isRequired",f.getInterfaceFieldIdRequired()==0);
                }}).collect(Collectors.toList())
        );
        //出参
        fields.put("output",list.stream().filter(f -> f.getInterfaceFieldIdType()==1)
                .map(f -> new HashMap<String, Object>() {{
                    put("name",StrUtil.isNotBlank(f.getInterfaceFieldIdAlias())?f.getInterfaceFieldIdAlias():f.getInterfaceFieldIdName());
                    put("type",f.getInterfaceFieldIdDataType());
                }}).collect(Collectors.toList())
        );
        result.put("fields",fields);
        return AjaxResult.success(result);
    }

    @PostMapping("/query/list")
    public AjaxResult queryList(@RequestBody InterfaceQueryDTO dto ){
        final List<InterfaceManage> list = serviceConstant.interfaceManageService.list(WrapperConverter.convert(dto));
        return AjaxResult.success(list);
    }

    /**
     * 同步方法
     * 用于同步接口平台的数据到数据平台
     */
    @GetMapping("synchronizeInterfaceData")
    public AjaxResult synchronizeInterfaceData(){
        //查询所有未同步的供应商
        List<InterfaceSourceManage> source = serviceConstant.interfaceSourceManageService.listNotSync();
        //查询所有未同步的接口
        List<InterfaceManage> interfaceManages = serviceConstant.interfaceManageService.listNotSync();
        //查询所有未同步的属性
        List<InterfaceFieldIdManage> fields = serviceConstant.interfaceFieldIdManageService.listNotSync();
        fields = fields.stream().filter(f -> f.getInterfaceFieldIdType()==1).collect(Collectors.toList());//只要出参

        //同步供应商
        serviceConstant.rdeRiskVariableThemeService.syncSourceData(source);
        //同步接口
        serviceConstant.rdeRiskVariableGroupService.syncInterfaceData(interfaceManages);
        //同步参数
        serviceConstant.rdeRiskVariableRecordService.syncFieldData(fields);
        return AjaxResult.success();
    }

    /**
     * 接口导出 - 直接返回二进制流格式供客户端下载
     */
    @PostMapping("/interfaceExport")
    public ResponseEntity<byte[]> interfaceExport(@RequestBody List<String> interfaceManageNos){
        try {
            //查询所有要导出接口
            List<InterfaceManage> interfaceList = serviceConstant.interfaceManageService.lambdaQuery()
                    .in(InterfaceManage::getInterfaceManageNo, interfaceManageNos)
                    .list();
            //查询所有接口参数
            List<InterfaceFieldIdManage> fieldList = serviceConstant.interfaceFieldIdManageService.lambdaQuery()
                    .in(InterfaceFieldIdManage::getInterfaceManageNo, interfaceManageNos)
                    .list();
            //将参数按接口分组
            Map<String, List<InterfaceFieldIdManage>> fieldMap = fieldList.stream()
                    .collect(Collectors.groupingBy(InterfaceFieldIdManage::getInterfaceManageNo));
            //封装导出数据
            List<HashMap<String, Object>> result = new ArrayList<>();
            // 优化数据结构，避免重复数据
            for (InterfaceManage interfaceManage : interfaceList) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("interface", interfaceManage);
                map.put("fields", fieldMap.getOrDefault(interfaceManage.getInterfaceManageNo(), Collections.emptyList()));
                result.add(map);
            }
            //将result序列化成JSON
            String jsonString = JSON.toJSONString(result);
            byte[] jsonBytes = jsonString.getBytes(StandardCharsets.UTF_8);
            
            //生成文件名
            String filename = "interface_export_" + System.currentTimeMillis() + ".json";
            
            //构建ResponseEntity，设置为二进制流格式
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            // 对文件名进行URL编码，支持中文文件名
            String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8.toString());
            headers.setContentDispositionFormData("attachment", encodedFilename);
            headers.setContentLength(jsonBytes.length);
            // 添加缓存控制头，防止缓存
            headers.setCacheControl("no-cache, no-store, must-revalidate");
            headers.setPragma("no-cache");
            headers.setExpires(0);
            
            return new ResponseEntity<>(jsonBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            //异常处理
            log.error("导出接口失败", e);
            try {
                String errorJson = JSON.toJSONString(AjaxResult.error("导出失败: " + e.getMessage()));
                byte[] errorBytes = errorJson.getBytes(StandardCharsets.UTF_8);
                
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentLength(errorBytes.length);
                
                return new ResponseEntity<>(errorBytes, headers, HttpStatus.INTERNAL_SERVER_ERROR);
            } catch (Exception ex) {
                log.error("构造错误响应失败", ex);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
    }

    /**
     * 接口导入
     */
    @PostMapping("/interfaceImport/{sourceNo}")
    public AjaxResult interfaceImport(@PathVariable String sourceNo, MultipartFile jsonFile,HttpServletRequest request) throws IOException {
        LoginUser loginUser = SecurityUtils.getLoginUser(request);

        if (jsonFile.isEmpty()){
            return AjaxResult.error("文件不能为空");
        }
        byte[] bytes = jsonFile.getBytes();
        String jsonStr = new String(bytes, StandardCharsets.UTF_8);
        if (!JSONUtil.isJsonArray(jsonStr)){
            return AjaxResult.error("文件内容格式错误，不是JSON数组");
        }

        //解析出数据
        List<JSONObject> jsonObjects = JSONArray.parseArray(jsonStr, JSONObject.class);
        //保存接口
        for (JSONObject jsonObject : jsonObjects) {
            InterfaceManage interfaceInfo = jsonObject.getJSONObject("interface").toJavaObject(InterfaceManage.class);
            List<InterfaceFieldIdManage> fields = jsonObject.getJSONArray("fields").toJavaList(InterfaceFieldIdManage.class);
            //保存接口
            interfaceInfo.setId(null).setInterfaceSourceNo(sourceNo);
            interfaceService.saveInterfaceInfo(interfaceInfo,loginUser);
            //保存参数
            fields.stream().forEach(f -> {
                f.setId(null).setInterfaceManageNo(interfaceInfo.getInterfaceManageNo());
                interfaceService.saveInterfaceFieldIdInfo(f,loginUser);
            });
        }
        return AjaxResult.success();
    }
}
