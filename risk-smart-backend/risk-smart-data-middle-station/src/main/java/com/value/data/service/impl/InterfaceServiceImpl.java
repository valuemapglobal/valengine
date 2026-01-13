package com.value.data.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.risksmart.common.core.utils.StringUtils;
import com.risksmart.common.core.exception.ServiceException;
import com.value.data.common.constant.HttpStatus;
import com.value.data.common.model.LoginUser;
import com.value.data.common.utils.AjaxResult;
import com.value.data.constant.IdConstant;
import com.value.data.constant.ServiceConstant;
import com.value.data.domain.VMHttpResult;
import com.value.data.domain.dto.*;
import com.value.data.domain.entity.InterfaceFieldIdManage;
import com.value.data.domain.entity.InterfaceManage;
import com.value.data.domain.entity.InterfaceSourceManage;
import com.value.data.domain.entity.RdeRiskVariableRecord;
import com.value.data.domain.vo.InterfaceChargingListVo;
import com.value.data.domain.vo.InterfaceChargingVo;
import com.value.data.mapper.InterfaceManageMapper;
import com.value.data.mapper.RdeRiskVariableRecordMapper;
import com.value.data.service.InterfaceService;
import com.value.data.service.feign.DecisionManageFeignService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Vida
 * @date 2023年08月16日 19:48
 * @description
 */
@Service
@Slf4j
@AllArgsConstructor
public class InterfaceServiceImpl implements InterfaceService {
    private final ServiceConstant serviceConstant;
    private final InterfaceManageMapper interfaceManageMapper;
    private final RdeRiskVariableRecordMapper rdeRiskVariableRecordMapper;
    private final DecisionManageFeignService decisionManageFeignService;


    @Override
    public InterfaceChargingVo interfaceCharging(InterfaceChargingDTO interfaceChargingDTO) {
        //查询今日接口计费总额
        List<BigDecimal> todayBigDecimals = interfaceManageMapper.queryTodayChargingCount(interfaceChargingDTO.getDeptId());
        BigDecimal todayChargingCount = BigDecimal.ZERO;
        if(CollectionUtil.isNotEmpty(todayBigDecimals)){
            for (BigDecimal num : todayBigDecimals){
                todayChargingCount = todayChargingCount.add(num);
            }
        }
        //查询今日接口计费更新时间
        String todayUpdateTime = interfaceManageMapper.queryTodayUpdateTime(interfaceChargingDTO.getDeptId());
        //查询总的接口计费总额
        List<BigDecimal> allBigDecimals = interfaceManageMapper.queryAllChargingCount(interfaceChargingDTO.getDeptId());
        BigDecimal allChargingCount = BigDecimal.ZERO;
        if(CollectionUtil.isNotEmpty(allBigDecimals)){
            for (BigDecimal num : allBigDecimals){
                allChargingCount = allChargingCount.add(num);
            }
        }
        //查询总的接口计费更新时间
        String allUpdateTime = interfaceManageMapper.queryAllUpdateTime(interfaceChargingDTO.getDeptId());
        //查询接口计费列表
        //根据nickName查询用户

        PageHelper.startPage(interfaceChargingDTO.getPageNum(),interfaceChargingDTO.getPageSize());
        List<InterfaceChargingListVo> listVos =  interfaceManageMapper.queryInterfaceChargingList(interfaceChargingDTO);
        List<BigDecimal> countList = new ArrayList<>();
        BigDecimal currentCount = BigDecimal.ZERO;
        listVos.forEach(vo->{
            //根据username查询用户信息
            vo.setOrderNo(vo.getOrderId());
            if(vo.getChargingFlag()!=null&&vo.getChargingFlag()==1){
                if(vo.getPrice()!=null){
                    countList.add(vo.getPrice());
                }
            }
        });
        if(CollectionUtil.isNotEmpty(countList)){
            for (BigDecimal count : countList){
                currentCount = currentCount.add(count);
            }
        }

        PageInfo<InterfaceChargingListVo> pageInfo = new PageInfo<>(listVos);
        InterfaceChargingVo interfaceChargingVo = new InterfaceChargingVo();
        interfaceChargingVo.setTotal((int) pageInfo.getTotal());
        interfaceChargingVo.setListVo(listVos);
        interfaceChargingVo.setCount(currentCount);
        interfaceChargingVo.setTodayChargingCount(todayChargingCount);
        interfaceChargingVo.setTodayUpdateTime(todayUpdateTime);
        interfaceChargingVo.setAllChargingCount(allChargingCount);
        interfaceChargingVo.setAllUpdateTime(allUpdateTime);
        return interfaceChargingVo;

    }

    @Override
    @Transactional
    public void saveSourceInfo(InterfaceSourceManage interfaceSourceManage, LoginUser user){
        //填充供应商信息
        interfaceSourceManage.setCreateBy(user.getUsername());
        interfaceSourceManage.setUserId(user.getUserid());
        interfaceSourceManage.setDeptId(user.getSysUser().getDeptId());
        //填充数据平台信息
        RdeRiskVariableThemeDTO themeDTO = new RdeRiskVariableThemeDTO();
        themeDTO.setName(interfaceSourceManage.getDataName());
        themeDTO.setKeycode(interfaceSourceManage.getDataName());
        themeDTO.setInterfaceSourceNo(interfaceSourceManage.getInterfaceSourceNo());
        themeDTO.setPackageType(getPackageType(interfaceSourceManage.getInterfaceDataType()));

        Object savePointSon = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
        try {
            serviceConstant.interfaceSourceManageService.save(interfaceSourceManage);
            serviceConstant.rdeRiskVariableThemeService.submit(themeDTO,user);
        } catch (Exception e){
            //出现异常回滚事务
            TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savePointSon);
            log.error("事务回滚");
            throw new RuntimeException(e);
        }
    }
    private String getPackageType(Integer interfaceDataType) {
        final String packageType;
        switch (interfaceDataType){
            case 0:
                packageType = "元数据对象";
                break;
            case 1:
                packageType = "特征变量对象";
                break;
            case 2:
                packageType = "分析指标对象";
                break;
            default:
                packageType = "未定义对象";
                break;
        }
        return packageType;
    }

    @Override
    @Transactional
    public void removeSourceInfo(RemoveSourceInfo removeSourceInfo, LoginUser user) {
        //删除供应商
        LambdaQueryWrapper<InterfaceSourceManage> lambdaQuery = Wrappers.lambdaQuery();
        lambdaQuery.eq(InterfaceSourceManage::getDataStatus, 0)
                .eq(InterfaceSourceManage::getInterfaceSourceNo, removeSourceInfo.getSourceNo())
                .eq(InterfaceSourceManage::getDeptId,user.getSysUser().getDeptId());
        //删除主题
        DeleteThemeDTO deleteThemeDTO = new DeleteThemeDTO();
        deleteThemeDTO.setInterfaceSourceNo(removeSourceInfo.getSourceNo());

        Object savePointSon = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
        try {
            serviceConstant.interfaceSourceManageService.remove(lambdaQuery);
            serviceConstant.rdeRiskVariableThemeService.delete(deleteThemeDTO,user);
        } catch (Exception e){
            //出现异常回滚事务
            TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savePointSon);
            log.error("事务回滚");
            throw e;
        }
    }

    @Transactional
    @Override
    public void updateSourceInfo(UpdateSourceInfoDTO updateSourceInfoDTO, LoginUser user){
        //修改供应商
        InterfaceSourceManage interfaceSourceManage = new InterfaceSourceManage();
        BeanUtil.copyProperties(updateSourceInfoDTO, interfaceSourceManage, false);
        LambdaQueryWrapper<InterfaceSourceManage> lambdaQuery = Wrappers.lambdaQuery();
        lambdaQuery.eq(InterfaceSourceManage::getDataStatus, 0)
                .eq(InterfaceSourceManage::getInterfaceSourceNo, updateSourceInfoDTO.getSourceNo())
                .eq(InterfaceSourceManage::getDeptId,user.getSysUser().getDeptId());
        interfaceSourceManage.setUpdateBy(user.getUsername());
        //修改数据平台
        RdeRiskVariableThemeDTO themeDTO = new RdeRiskVariableThemeDTO();
        themeDTO.setName(interfaceSourceManage.getDataName());
        themeDTO.setKeycode(interfaceSourceManage.getDataName());
        themeDTO.setInterfaceSourceNo(updateSourceInfoDTO.getSourceNo());

        Object savePointSon = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
        try {
            serviceConstant.interfaceSourceManageService.update(interfaceSourceManage, lambdaQuery);
            serviceConstant.rdeRiskVariableThemeService.update(themeDTO,user);
        } catch (Exception e) {
            //出现异常回滚事务
            TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savePointSon);
            log.error("事务回滚");
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public int saveInterfaceInfo(InterfaceManage interfaceManage,LoginUser user){
        //效验被移除数据是否存在
        Boolean validNoBool = serviceConstant.interfaceSourceManageService.validSourceNo(interfaceManage.getInterfaceSourceNo());
        if (!validNoBool){ throw new ServiceException("供应商信息不存在"); }
        //效验编号
        Long noCount = serviceConstant.interfaceManageService.lambdaQuery()
                .eq(InterfaceManage::getInterfaceNo, interfaceManage.getInterfaceNo())
                .eq(InterfaceManage::getDeptId, user.getSysUser().getDeptId())
                .count();
        if (noCount > 0){ throw new ServiceException("编号信息已存在"); }

        //填充接口平台数据
        String no = IdConstant.DATA_MIDDLE_STATION_PREFIX.concat(String.valueOf(serviceConstant.idGeneratorService.generateUniqueId()));
        interfaceManage.setInterfaceManageNo(no);
        interfaceManage.setCreateBy(user.getUsername());
        interfaceManage.setUserId(user.getUserid());
        interfaceManage.setDeptId(user.getSysUser().getDeptId());
        //填充数据平台数据
        RdeRiskVariableGroupDTO groupDTO = new RdeRiskVariableGroupDTO();
        groupDTO.setName(interfaceManage.getInterfaceName());
        groupDTO.setKeycode(interfaceManage.getInterfaceName());
        groupDTO.setType("3");
        groupDTO.setInterfaceManageNo(interfaceManage.getInterfaceManageNo());
        groupDTO.setInterfaceSourceNo(interfaceManage.getInterfaceSourceNo());
        groupDTO.setInterfaceVersion(interfaceManage.getInterfaceVersion());

        //开启事务保存
        Object savePointSon = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
        try {
            serviceConstant.rdeRiskVariableGroupService.submit(groupDTO,user);
            serviceConstant.interfaceManageService.save(interfaceManage);
        } catch (Exception e) {
            //出现异常回滚事务
            TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savePointSon);
            log.error("事务回滚");
            throw new RuntimeException(e);
        }
        return 0;
    }

    @Override
    @Transactional
    public int removeInterfaceInfo(RemoveInterfaceInfoDTO removeInterfaceInfoDTO,LoginUser user) {
        //删除接口
        LambdaQueryWrapper<InterfaceManage> lambdaQuery = Wrappers.lambdaQuery();
        lambdaQuery.eq(InterfaceManage::getDataStatus, 0)
                .eq(InterfaceManage::getDeptId,user.getSysUser().getDeptId())
                .eq(InterfaceManage::getInterfaceManageNo, removeInterfaceInfoDTO.getManageNo());
        //删除对象
        DeleteGroupDTO deleteGroupDTO = new DeleteGroupDTO();
        deleteGroupDTO.setInterfaceManageNo(removeInterfaceInfoDTO.getManageNo());

        Object savePointSon = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
        try {
            serviceConstant.interfaceManageService.remove(lambdaQuery);
            serviceConstant.rdeRiskVariableGroupService.delete(deleteGroupDTO,user);
        } catch (Exception e) {
            //出现异常回滚事务
            TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savePointSon);
            log.error("事务回滚");
            throw e;
        }
        return 0;
    }

    @Override
    @Transactional
    public int updateInterfaceInfo(UpdateInterfaceInfo updateInterfaceInfo,LoginUser user){
        InterfaceManage interfaceManage = new InterfaceManage();
        BeanUtil.copyProperties(updateInterfaceInfo, interfaceManage, false);
        //唯一标识
        interfaceManage.setInterfaceManageNo(updateInterfaceInfo.getManageNo());
        LambdaQueryWrapper<InterfaceManage> lambdaQuery = Wrappers.lambdaQuery();
        lambdaQuery.eq(InterfaceManage::getDataStatus, 0)
                .eq(InterfaceManage::getInterfaceSourceNo, updateInterfaceInfo.getSourceNo())
                .eq(InterfaceManage::getDeptId,user.getSysUser().getDeptId())
                .eq(InterfaceManage::getInterfaceManageNo, updateInterfaceInfo.getManageNo());
        interfaceManage.setUpdateBy(user.getUsername());
        //修改数据平台对象
        RdeRiskVariableGroupDTO groupDTO = new RdeRiskVariableGroupDTO();
        groupDTO.setName(updateInterfaceInfo.getInterfaceName());
        groupDTO.setKeycode(updateInterfaceInfo.getInterfaceName());
        groupDTO.setInterfaceManageNo(updateInterfaceInfo.getManageNo());
        groupDTO.setType("3");
        groupDTO.setInterfaceVersion(updateInterfaceInfo.getInterfaceVersion());

        Object savePointSon = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
        try {
            serviceConstant.interfaceManageService.update(interfaceManage, lambdaQuery);
            serviceConstant.rdeRiskVariableGroupService.update(groupDTO,user);
            return 0;
        } catch (Exception e) {
            //出现异常回滚事务
            TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savePointSon);
            log.error("事务回滚");
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public int saveInterfaceFieldIdInfo(SaveInterfaceFieldIdInfoDTO saveInterfaceFieldIdInfoDTO,LoginUser user) {
        InterfaceFieldIdManage interfaceFieldIdManage = new InterfaceFieldIdManage();
        BeanUtil.copyProperties(saveInterfaceFieldIdInfoDTO, interfaceFieldIdManage, false);
        //唯一标识
        String no = IdConstant.DATA_MIDDLE_STATION_PREFIX.concat(String.valueOf(serviceConstant.idGeneratorService.generateUniqueId()));
        interfaceFieldIdManage.setInterfaceFieldIdManage(no);
        interfaceFieldIdManage.setInterfaceManageNo(saveInterfaceFieldIdInfoDTO.getManageNo());
        interfaceFieldIdManage.setCreateBy(user.getUsername());
        interfaceFieldIdManage.setUserId(user.getUserid())
                .setDeptId(user.getSysUser().getDeptId());

        RdeRiskVariableRecordDTO recordDTO = new RdeRiskVariableRecordDTO();
        if (StringUtils.isEmpty(saveInterfaceFieldIdInfoDTO.getInterfaceFieldIdAlias())){
            recordDTO.setCode(saveInterfaceFieldIdInfoDTO.getInterfaceFieldIdName());
        }else {
            recordDTO.setCode(saveInterfaceFieldIdInfoDTO.getInterfaceFieldIdAlias());
        }
        recordDTO.setName(saveInterfaceFieldIdInfoDTO.getInterfaceFieldIdDescription());
        recordDTO.setParentNo(saveInterfaceFieldIdInfoDTO.getInterfaceFieldIdFather());
        recordDTO.setType(saveInterfaceFieldIdInfoDTO.getInterfaceFieldIdDataType());
        recordDTO.setInterfaceManageNo(saveInterfaceFieldIdInfoDTO.getManageNo());
        recordDTO.setInterfaceFieldIdManage(no);

        Object savePointSon = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
        try {
            serviceConstant.interfaceFieldIdManageService.save(interfaceFieldIdManage);
            //同步数据平台。保存属性，只有参数为出参时才会保存
            if(!saveInterfaceFieldIdInfoDTO.getInterfaceFieldIdType().equals("1")){return 1;}
            serviceConstant.rdeRiskVariableRecordService.submit(recordDTO,user);
        } catch (Exception e) {
            //出现异常回滚事务
            TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savePointSon);
            log.error("事务回滚");
            throw e;
        }
        return 1;
    }

    @Override
    @Transactional
    public int saveInterfaceFieldIdInfo(InterfaceFieldIdManage interfaceFieldIdInfo, LoginUser user){
        //合法性校验
        Boolean validNoBool = serviceConstant.interfaceManageService.validManageNo(interfaceFieldIdInfo.getInterfaceManageNo());
        if (!validNoBool){ new ServiceException("接口信息不存在"); }
        Boolean aBoolean = serviceConstant.interfaceFieldIdManageService.validName(
                interfaceFieldIdInfo.getInterfaceManageNo(),
                interfaceFieldIdInfo.getInterfaceFieldIdName(),
                interfaceFieldIdInfo.getInterfaceFieldIdType(),
                interfaceFieldIdInfo.getInterfaceFieldIdFather(),
                null
        );
        if (aBoolean){ new ServiceException("参数名称已存在"); }
        Boolean bBoolean = serviceConstant.interfaceFieldIdManageService.validAlias(
                interfaceFieldIdInfo.getInterfaceManageNo(),
                interfaceFieldIdInfo.getInterfaceFieldIdAlias(),
                interfaceFieldIdInfo.getInterfaceFieldIdType(),
                interfaceFieldIdInfo.getInterfaceFieldIdFather(),
                null
        );
        if (bBoolean){ new ServiceException("参数别名已存在"); }

        //接口平台数据填充
        String no = IdConstant.DATA_MIDDLE_STATION_PREFIX.concat(String.valueOf(serviceConstant.idGeneratorService.generateUniqueId()));
        interfaceFieldIdInfo.setInterfaceFieldIdManage(no)
                .setCreateBy(user.getUsername())
                .setUserId(user.getUserid())
                .setDeptId(user.getSysUser().getDeptId());
        //数据平台数据填充
        RdeRiskVariableRecordDTO recordDTO = new RdeRiskVariableRecordDTO();
        if (StringUtils.isEmpty(interfaceFieldIdInfo.getInterfaceFieldIdAlias())){
            recordDTO.setCode(interfaceFieldIdInfo.getInterfaceFieldIdName());
        }else {
            recordDTO.setCode(interfaceFieldIdInfo.getInterfaceFieldIdAlias());
        }
        recordDTO.setName(interfaceFieldIdInfo.getInterfaceFieldIdDescription());
        recordDTO.setParentNo(interfaceFieldIdInfo.getInterfaceFieldIdFather());
        recordDTO.setType(interfaceFieldIdInfo.getInterfaceFieldIdDataType().toString());
        recordDTO.setInterfaceManageNo(interfaceFieldIdInfo.getInterfaceManageNo());
        recordDTO.setInterfaceFieldIdManage(no);
        //保存
        Object savePointSon = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
        try {
            serviceConstant.interfaceFieldIdManageService.save(interfaceFieldIdInfo);
            //同步数据平台。保存属性，只有参数为出参时才会保存
            if(!interfaceFieldIdInfo.getInterfaceFieldIdType().equals(1)){return 1;}
            serviceConstant.rdeRiskVariableRecordService.submit(recordDTO,user);
        } catch (Exception e) {
            //出现异常回滚事务
            TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savePointSon);
            log.error("事务回滚");
            throw e;
        }
        return 1;
    }

    @Override
    @Transactional
    public int removeInterfaceFieldIdInfo(RemoveInterfaceFieldIdInfoDTO removeInterfaceFieldIdInfoDTO,LoginUser user) {
        //删除之前校验一下决策平台是否引用了该字段
        /*
        RdeRiskVariableRecord field = rdeRiskVariableRecordMapper.selectOne(
                Wrappers.lambdaQuery(RdeRiskVariableRecord.class)
                        .eq(RdeRiskVariableRecord::getInterfaceFieldIdManage, removeInterfaceFieldIdInfoDTO.getFieldIdNo())
        );
        Optional.ofNullable(field).ifPresent(f -> {
            VMHttpResult<Boolean> response = decisionManageFeignService.checkField(f.getRecordNo());
            if (!Boolean.TRUE.equals(response.getData())){
                throw new ServiceException(
                        HttpStatus.SUCCESS,
                        response.getCode().equals(HttpStatus.SUCCESS)?"无法删除存在引用的参数":response.getMsg(),
                        Boolean.FALSE
                );
            }
        });
         */
        //删除参数
        LambdaQueryWrapper<InterfaceFieldIdManage> lambdaQuery = Wrappers.lambdaQuery();
        lambdaQuery.eq(InterfaceFieldIdManage::getInterfaceFieldIdManage, removeInterfaceFieldIdInfoDTO.getFieldIdNo())
                .eq(InterfaceFieldIdManage::getDeptId,user.getSysUser().getDeptId());
        //删除属性
        DeleteRecordDTO recordDTO = new DeleteRecordDTO();
        recordDTO.setInterfaceFieldIdManage(removeInterfaceFieldIdInfoDTO.getFieldIdNo());
        Object savePointSon = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
        try {
            serviceConstant.interfaceFieldIdManageService.remove(lambdaQuery);
            serviceConstant.rdeRiskVariableRecordService.delete(recordDTO,user);
        } catch (Exception e) {
            //出现异常回滚事务
            TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savePointSon);
            log.error("事务回滚");
            throw e;
        }
        return 0;
    }

    @Override
    @Transactional
    public int updateInterfaceFieldIdInfo(UpdateInterfaceFieldIdInfoDTO updateInterfaceFieldIdInfoDTO,LoginUser user) throws Exception {
        //修改参数
        InterfaceFieldIdManage interfaceFieldIdManage = new InterfaceFieldIdManage();
        BeanUtil.copyProperties(updateInterfaceFieldIdInfoDTO, interfaceFieldIdManage, false);
        LambdaQueryWrapper<InterfaceFieldIdManage> lambdaQuery = Wrappers.lambdaQuery();
        lambdaQuery.eq(InterfaceFieldIdManage::getInterfaceManageNo, updateInterfaceFieldIdInfoDTO.getManageNo())
                .eq(InterfaceFieldIdManage::getInterfaceFieldIdManage, updateInterfaceFieldIdInfoDTO.getFieldIdNo())
                .eq(InterfaceFieldIdManage::getDeptId,user.getSysUser().getDeptId());
        InterfaceFieldIdManage one = serviceConstant.interfaceFieldIdManageService.getOne(lambdaQuery);//更新之前先查出来
        interfaceFieldIdManage.setUpdateBy(user.getUsername());

        Object savePointSon = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
        try {
            serviceConstant.interfaceFieldIdManageService.update(interfaceFieldIdManage, lambdaQuery);

            //判断是否由出参向入参或者从入参到出参改变
            if (one==null){ return 0; }
            if (!updateInterfaceFieldIdInfoDTO.getInterfaceFieldIdType().equals(one.getInterfaceFieldIdType().toString())){
                if (updateInterfaceFieldIdInfoDTO.getInterfaceFieldIdType().equals("0")){//由出参变为入参
                    //删除元数据中的属性
                    DeleteRecordDTO deleteRecordDTO = new DeleteRecordDTO();
                    deleteRecordDTO.setInterfaceFieldIdManage(updateInterfaceFieldIdInfoDTO.getFieldIdNo());
                    serviceConstant.rdeRiskVariableRecordService.delete(deleteRecordDTO,user);
                }else if (updateInterfaceFieldIdInfoDTO.getInterfaceFieldIdType().equals("1")){//由入参变为出参
                    //新增一条元数据属性
                    RdeRiskVariableRecordDTO recordDTO = getRdeRiskVariableRecordDTO(updateInterfaceFieldIdInfoDTO);
                    serviceConstant.rdeRiskVariableRecordService.submit(recordDTO,user);
                }
                return 1;
            }
            //修改属性 入参时不需要修改
            if (updateInterfaceFieldIdInfoDTO.getInterfaceFieldIdType().equals("0")){ return 1; }
            RdeRiskVariableRecordDTO recordDTO = new RdeRiskVariableRecordDTO();
            if (StringUtils.isEmpty(updateInterfaceFieldIdInfoDTO.getInterfaceFieldIdAlias())){
                recordDTO.setCode(updateInterfaceFieldIdInfoDTO.getInterfaceFieldIdName());
            }else {
                recordDTO.setCode(updateInterfaceFieldIdInfoDTO.getInterfaceFieldIdAlias());
            }
            recordDTO.setName(updateInterfaceFieldIdInfoDTO.getInterfaceFieldIdDescription());
            recordDTO.setType(updateInterfaceFieldIdInfoDTO.getInterfaceFieldIdDataType());
            recordDTO.setInterfaceFieldIdManage(updateInterfaceFieldIdInfoDTO.getFieldIdNo());
            recordDTO.setParentNo(updateInterfaceFieldIdInfoDTO.getInterfaceFieldIdFather());
            serviceConstant.rdeRiskVariableRecordService.update(recordDTO,user);
        } catch (Exception e) {
            //出现异常回滚事务
            TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savePointSon);
            log.error("事务回滚");
            throw e;
        }
        return 1;
    }

    private static RdeRiskVariableRecordDTO getRdeRiskVariableRecordDTO(UpdateInterfaceFieldIdInfoDTO updateInterfaceFieldIdInfoDTO) {
        RdeRiskVariableRecordDTO recordDTO = new RdeRiskVariableRecordDTO();
        if (StringUtils.isEmpty(updateInterfaceFieldIdInfoDTO.getInterfaceFieldIdAlias())){
            recordDTO.setCode(updateInterfaceFieldIdInfoDTO.getInterfaceFieldIdName());
        }else {
            recordDTO.setCode(updateInterfaceFieldIdInfoDTO.getInterfaceFieldIdAlias());
        }
        recordDTO.setName(updateInterfaceFieldIdInfoDTO.getInterfaceFieldIdDescription());
        recordDTO.setParentNo(updateInterfaceFieldIdInfoDTO.getInterfaceFieldIdFather());
        recordDTO.setType(updateInterfaceFieldIdInfoDTO.getInterfaceFieldIdDataType());
        recordDTO.setInterfaceManageNo(updateInterfaceFieldIdInfoDTO.getManageNo());
        recordDTO.setInterfaceFieldIdManage(updateInterfaceFieldIdInfoDTO.getFieldIdNo());
        return recordDTO;
    }
}
