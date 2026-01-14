package com.value.data.service;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.risksmart.common.core.utils.StringUtils;
import com.value.data.common.model.LoginUser;
import com.value.data.common.service.IdGeneratorService;
import com.value.data.constant.IdConstant;
import com.value.data.domain.dto.DeleteGroupDTO;
import com.value.data.domain.dto.RdeRiskVariableGroupDTO;
import com.value.data.domain.entity.InterfaceManage;
import com.value.data.domain.entity.RdeRiskVariableGroup;
import com.value.data.domain.entity.RdeRiskVariableRecord;
import com.value.data.domain.entity.RdeRiskVariableTheme;
import com.value.data.domain.vo.PageData;
import com.value.data.domain.vo.RdeRiskVariableGroupVO;
import com.value.data.mapper.RdeRiskVariableGroupMapper;
import com.value.data.mapper.RdeRiskVariableRecordMapper;
import com.value.data.mapper.RdeRiskVariableThemeMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RdeRiskVariableGroupService implements BaseService {
    @Autowired
    private RdeRiskVariableGroupMapper mapper;
    @Autowired
    private RdeRiskVariableRecordMapper recordMapper;
    @Autowired
    private RdeRiskVariableThemeMapper themeMapper;
    @Autowired
    private IdGeneratorService idGeneratorService;
    @Autowired
    private InterfaceManageService interfaceManageService;

    public boolean validName(String name, String themeNo, String groupNo) {
        LambdaQueryWrapper<RdeRiskVariableGroup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RdeRiskVariableGroup::getName, name);
        wrapper.eq(RdeRiskVariableGroup::getThemeNo, themeNo);
        RdeRiskVariableGroup group = mapper.selectOne(wrapper);
        if (group == null)
            return false;
        if (StringUtils.isNotEmpty(groupNo) && group.getGroupNo().equals(groupNo))
            return false;
        return true;
    }

    /**
     * 查询列表
     */
    public PageData list(RdeRiskVariableGroupDTO query, LoginUser user) {
        RdeRiskVariableGroup group = new RdeRiskVariableGroup();
        BeanUtils.copyProperties(query, group);
        Page<RdeRiskVariableGroup> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<RdeRiskVariableGroup> wrapper = new LambdaQueryWrapper<>(group);
        wrapper.eq(RdeRiskVariableGroup::getDeptId, user.getSysUser().getDeptId());
        Page<RdeRiskVariableGroup> pages = mapper.selectPage(page, wrapper);
        List<RdeRiskVariableGroupVO> voList = getVOList(pages.getRecords(), RdeRiskVariableGroupVO.class);
        //处理关联接口信息
        if (voList != null && voList.size() > 0) {
            voList.stream().forEach(data -> {
                LambdaQueryWrapper<InterfaceManage> wrapper1 = new LambdaQueryWrapper<>();
                wrapper1.eq(InterfaceManage::getInterfaceManageNo, data.getInterfaceManageNo());
                InterfaceManage one1 = interfaceManageService.getOne(wrapper1);
                data.setAssociatedInterfaces(one1 == null ? "" : one1.getInterfaceName());
            });
        }
        PageData page1 = getPage(pages);
        page1.setRows(voList);
        return page1;
    }

    public boolean isExisted(RdeRiskVariableGroupDTO groupDTO) {
        if (StringUtils.isEmpty(groupDTO.getName()) || StringUtils.isEmpty(groupDTO.getThemeNo()))
            return false;
        //判断是否存在
        LambdaQueryWrapper<RdeRiskVariableGroup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RdeRiskVariableGroup::getName, groupDTO.getName());
        wrapper.eq(RdeRiskVariableGroup::getThemeNo, groupDTO.getThemeNo());
        RdeRiskVariableGroup group = mapper.selectOne(wrapper);
        if (group == null || group.getGroupNo().equals(groupDTO.getGroupNo()))
            return false;
        return true;
    }

    /**
     * 新增
     */
    public int submit(RdeRiskVariableGroupDTO record, LoginUser user) {
        //适配接口管理
        if (StringUtils.isEmpty(record.getThemeNo()) && StringUtils.isNotEmpty(record.getInterfaceSourceNo())) {
            LambdaQueryWrapper<RdeRiskVariableTheme> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(RdeRiskVariableTheme::getInterfaceSourceNo, record.getInterfaceSourceNo());
            RdeRiskVariableTheme theme = themeMapper.selectOne(wrapper);
            if (theme == null)
                return 0;
            record.setThemeNo(theme.getThemeNo());
        }

        RdeRiskVariableGroup group = new RdeRiskVariableGroup();
        BeanUtils.copyProperties(record, group);

        group.setGroupNo(IdConstant.DATA_MIDDLE_STATION_PREFIX.concat(String.valueOf(idGeneratorService.generateUniqueId())));
        group.setCreateUserId(user.getUserid().intValue());
        group.setUserId(user.getUserid());
        group.setDeptId(user.getSysUser().getDeptId());
        int result = mapper.insert(group);
        return result;
    }

    /**
     * 修改
     */
    public int update(RdeRiskVariableGroupDTO record, LoginUser user) {
        //适配接口管理
        if (StringUtils.isEmpty(record.getGroupNo()) && StringUtils.isNotEmpty(record.getInterfaceManageNo())) {
            LambdaQueryWrapper<RdeRiskVariableGroup> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(RdeRiskVariableGroup::getInterfaceManageNo, record.getInterfaceManageNo());
            RdeRiskVariableGroup group1 = mapper.selectOne(wrapper);
            if (group1 == null)
                return 0;
            record.setGroupNo(group1.getGroupNo());
            record.setThemeNo(group1.getThemeNo());
        }

        RdeRiskVariableGroup group = new RdeRiskVariableGroup();
        BeanUtils.copyProperties(record, group);

        //更新
        LambdaQueryWrapper<RdeRiskVariableGroup> groupWrapper = new LambdaQueryWrapper<>();
        groupWrapper.eq(RdeRiskVariableGroup::getGroupNo, record.getGroupNo())
                .eq(RdeRiskVariableGroup::getDeptId, user.getSysUser().getDeptId());
        int result = mapper.update(group, groupWrapper);
        return result;
    }

    public RdeRiskVariableGroupVO get(RdeRiskVariableGroupDTO query) {
        RdeRiskVariableGroup group = mapper.selectById(query.getId());
        RdeRiskVariableGroupVO groupVO = new RdeRiskVariableGroupVO();
        BeanUtils.copyProperties(group, groupVO);
        return groupVO;
    }

    /**
     * 根据ID查询
     */
    public RdeRiskVariableGroupVO select(String groupNo) {
        LambdaQueryWrapper<RdeRiskVariableGroup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RdeRiskVariableGroup::getGroupNo, groupNo);
        List<RdeRiskVariableGroup> list = mapper.selectList(wrapper);
        //判空
        RdeRiskVariableGroupVO groupVO = new RdeRiskVariableGroupVO();
        if (list != null && list.size() > 0) {
            BeanUtils.copyProperties(list.get(0), groupVO);
        }

        return groupVO;
    }

    /**
     * 根据ID删除
     */
    public int delete(DeleteGroupDTO deleteGroupDTO, LoginUser user) {
        //兼容接口管理
        if (StringUtils.isEmpty(deleteGroupDTO.getGroupNo()) && StringUtils.isNotEmpty(deleteGroupDTO.getInterfaceManageNo())) {
            LambdaQueryWrapper<RdeRiskVariableGroup> groupWarpper = new LambdaQueryWrapper<>();
            groupWarpper.eq(RdeRiskVariableGroup::getInterfaceManageNo, deleteGroupDTO.getInterfaceManageNo());
            RdeRiskVariableGroup rdeRiskVariableGroup = mapper.selectOne(groupWarpper);
            if (rdeRiskVariableGroup == null) {
                return 0;
            }
            deleteGroupDTO.setGroupNo(rdeRiskVariableGroup.getGroupNo());
        }

        //删除对象分组
        LambdaQueryWrapper<RdeRiskVariableGroup> warpper = new LambdaQueryWrapper<>();
        warpper.eq(RdeRiskVariableGroup::getGroupNo, deleteGroupDTO.getGroupNo())
                .eq(RdeRiskVariableGroup::getDeptId, user.getSysUser().getDeptId());
        int delete = mapper.delete(warpper);
        //删除组后删除下面的属性
        LambdaQueryWrapper<RdeRiskVariableRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RdeRiskVariableRecord::getGroupNo, deleteGroupDTO.getGroupNo())
                .eq(RdeRiskVariableRecord::getDeptId, user.getSysUser().getDeptId());
        recordMapper.delete(wrapper);

        return delete;
    }

    public void syncInterfaceData(List<InterfaceManage> interfaceManages) {
        if (CollUtil.isEmpty(interfaceManages)) {
            return;
        }
        List<String> sourceNo = interfaceManages.stream().map(InterfaceManage::getInterfaceSourceNo).distinct().collect(Collectors.toList());
        Map<String, String> themeNoMap = themeMapper.selectList(
                Wrappers.lambdaQuery(RdeRiskVariableTheme.class)
                        .in(RdeRiskVariableTheme::getInterfaceSourceNo, sourceNo)
                        .eq(RdeRiskVariableTheme::getDataStatus, 0)
        ).stream().collect(Collectors.toMap(RdeRiskVariableTheme::getInterfaceSourceNo, RdeRiskVariableTheme::getThemeNo));

        interfaceManages.stream().map(i -> {
            RdeRiskVariableGroup group = new RdeRiskVariableGroup();
            group.setName(i.getInterfaceName());
            group.setKeycode(i.getInterfaceName());
            group.setType("3");
            group.setCreateUserId(i.getUserId().intValue());
            group.setThemeNo(themeNoMap.get(i.getInterfaceSourceNo()));
            group.setGroupNo(IdConstant.DATA_MIDDLE_STATION_PREFIX.concat(String.valueOf(idGeneratorService.generateUniqueId())));
            group.setInterfaceManageNo(i.getInterfaceManageNo());
            group.setInterfaceVersion(i.getInterfaceVersion());
            group.setUserId(i.getUserId());
            group.setDeptId(i.getDeptId());
            return group;
        }).forEach(mapper::insert);
    }
}
