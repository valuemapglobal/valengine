package com.value.data.service;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.risksmart.common.core.utils.StringUtils;
import com.value.data.common.model.LoginUser;
import com.value.data.common.service.IdGeneratorService;
import com.value.data.constant.IdConstant;
import com.value.data.domain.dto.DeleteThemeDTO;
import com.value.data.domain.dto.RdeRiskVariableThemeDTO;
import com.value.data.domain.entity.InterfaceSourceManage;
import com.value.data.domain.entity.RdeRiskVariableGroup;
import com.value.data.domain.entity.RdeRiskVariableRecord;
import com.value.data.domain.entity.RdeRiskVariableTheme;
import com.value.data.domain.vo.FieldTree;
import com.value.data.domain.vo.PageData;
import com.value.data.domain.vo.RdeRiskVariableThemeVO;
import com.value.data.domain.vo.TreeList;
import com.value.data.mapper.RdeRiskVariableGroupMapper;
import com.value.data.mapper.RdeRiskVariableRecordMapper;
import com.value.data.mapper.RdeRiskVariableThemeMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RdeRiskVariableThemeService implements BaseService {
    @Autowired
    private RdeRiskVariableThemeMapper themeMapper;
    @Autowired
    private RdeRiskVariableGroupMapper groupMapper;
    @Autowired
    private RdeRiskVariableRecordMapper recordMapper;
    @Autowired
    private IdGeneratorService idGeneratorService;

    /**
     * 查询列表
     */
    public PageData list(RdeRiskVariableThemeDTO query, LoginUser user) {
        RdeRiskVariableTheme theme = new RdeRiskVariableTheme();
        BeanUtils.copyProperties(query, theme);

        LambdaQueryWrapper<RdeRiskVariableTheme> wrapper = new LambdaQueryWrapper<>(theme);
        wrapper.eq(RdeRiskVariableTheme::getPackageType, "元数据对象")
                .eq(RdeRiskVariableTheme::getDeptId, user.getSysUser().getDeptId());
        Page<RdeRiskVariableTheme> pages = themeMapper.selectPage(
                new Page<>(query.getPageNum(), query.getPageSize()), wrapper
        );
        PageData pageData = getPage(pages);
        pageData.setRows(getVOList(pages.getRecords(), RdeRiskVariableThemeVO.class));
        return pageData;
    }

    public boolean isExisted(RdeRiskVariableThemeDTO themeDTO) {
        if (StringUtils.isEmpty(themeDTO.getName()))
            return false;
        LambdaQueryWrapper<RdeRiskVariableTheme> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RdeRiskVariableTheme::getName, themeDTO.getName());
        RdeRiskVariableTheme theme = themeMapper.selectOne(wrapper);
        if (theme == null)
            return false;
        if (StringUtils.isEmpty(themeDTO.getThemeNo()))
            return true;
        if (theme.getThemeNo().equals(themeDTO.getThemeNo()))
            return false;
        return true;
    }

    public boolean validName(String name, String no) {
        LambdaQueryWrapper<RdeRiskVariableTheme> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RdeRiskVariableTheme::getName, name);
        RdeRiskVariableTheme theme = themeMapper.selectOne(wrapper);
        if (theme == null)
            return false;
        if (StringUtils.isNotEmpty(no) && theme.getThemeNo().equals(no))
            return false;
        return true;
    }

    /**
     * 插入
     */
    public int submit(RdeRiskVariableThemeDTO record, LoginUser user) {
        RdeRiskVariableTheme theme = new RdeRiskVariableTheme();
        BeanUtils.copyProperties(record, theme);
        theme.setCreateUserId(user.getUserid().intValue());
        theme.setUserId(user.getUserid());
        theme.setDeptId(user.getSysUser().getDeptId());
        theme.setThemeNo(IdConstant.DATA_MIDDLE_STATION_PREFIX.concat(String.valueOf(idGeneratorService.generateUniqueId())));
        int result = themeMapper.insert(theme);
        return result;
    }

    /**
     * 修改主题
     */
    public int update(RdeRiskVariableThemeDTO record, LoginUser user) throws Exception {
        //兼容接口管理
        if (StringUtils.isEmpty(record.getThemeNo()) && StringUtils.isNotEmpty(record.getInterfaceSourceNo())) {
            LambdaQueryWrapper<RdeRiskVariableTheme> wrapper1 = new LambdaQueryWrapper<>();
            wrapper1.eq(RdeRiskVariableTheme::getInterfaceSourceNo, record.getInterfaceSourceNo());
            RdeRiskVariableTheme theme = themeMapper.selectOne(wrapper1);
            if (theme == null) {
                return 0;
            }
            record.setThemeNo(theme.getThemeNo());
        }

        RdeRiskVariableTheme theme = new RdeRiskVariableTheme();
        BeanUtils.copyProperties(record, theme);

        LambdaQueryWrapper<RdeRiskVariableTheme> themeWrapper = new LambdaQueryWrapper<>();
        themeWrapper.eq(RdeRiskVariableTheme::getThemeNo, record.getThemeNo())
                .eq(RdeRiskVariableTheme::getDeptId, user.getSysUser().getDeptId());
        int result = themeMapper.update(theme, themeWrapper);
        return result;
    }

    /**
     * 根据ID查询
     */
    public RdeRiskVariableThemeVO select(String themeNo) {
        LambdaQueryWrapper<RdeRiskVariableTheme> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RdeRiskVariableTheme::getThemeNo, themeNo);
        List<RdeRiskVariableTheme> lists = themeMapper.selectList(wrapper);
        //判空
        if (lists != null && lists.size() > 0) {
            RdeRiskVariableTheme theme = lists.get(0);
            RdeRiskVariableThemeVO themeVO = new RdeRiskVariableThemeVO();
            BeanUtils.copyProperties(theme, themeVO);
            return themeVO;
        }
        return null;
    }

    /**
     * 根据ID删除
     */
    public int delete(DeleteThemeDTO deleteThemeDTO, LoginUser user) {
        //适配接口管理
        LambdaQueryWrapper<RdeRiskVariableTheme> themeWrapper = new LambdaQueryWrapper<>();
        themeWrapper.eq(RdeRiskVariableTheme::getDeptId, user.getSysUser().getDeptId());
        if (StringUtils.isNotEmpty(deleteThemeDTO.getThemeNo())) {
            themeWrapper.eq(RdeRiskVariableTheme::getThemeNo, deleteThemeDTO.getThemeNo());
        }
        if (StringUtils.isNotEmpty(deleteThemeDTO.getInterfaceSourceNo())) {
            themeWrapper.eq(RdeRiskVariableTheme::getInterfaceSourceNo, deleteThemeDTO.getInterfaceSourceNo());
        }
        RdeRiskVariableTheme theme = themeMapper.selectOne(themeWrapper);
        if (theme == null) {
            return 0;
        }
        deleteThemeDTO.setThemeNo(theme.getThemeNo());

        //删除主题
        int delete = themeMapper.delete(themeWrapper);
        //删除下面的属性
        LambdaQueryWrapper<RdeRiskVariableGroup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RdeRiskVariableGroup::getThemeNo, deleteThemeDTO.getThemeNo())
                .eq(RdeRiskVariableGroup::getDeptId, user.getSysUser().getDeptId());
        List<RdeRiskVariableGroup> rdeRiskVariableGroups = groupMapper.selectList(wrapper);
        if (rdeRiskVariableGroups == null || rdeRiskVariableGroups.size() <= 0) {
            return delete;
        }
        List<String> groupNos = rdeRiskVariableGroups.stream().map(group -> group.getGroupNo()).collect(Collectors.toList());
        if (groupNos != null && groupNos.size() > 0) {
            LambdaQueryWrapper<RdeRiskVariableRecord> recordWapper = new LambdaQueryWrapper<>();
            recordWapper.in(RdeRiskVariableRecord::getGroupNo, groupNos)
                    .eq(RdeRiskVariableRecord::getDeptId, user.getSysUser().getDeptId());
            recordMapper.delete(recordWapper);
        }
        //删除下面的分组
        groupMapper.delete(wrapper);

        return delete;
    }

    public List<RdeRiskVariableThemeVO> getAll() {
        List<RdeRiskVariableTheme> themes = themeMapper.selectList(null);
        List<RdeRiskVariableThemeVO> voList = getVOList(themes, RdeRiskVariableThemeVO.class);
        return voList;
    }

    /**
     * 树级列表
     */
    public List<TreeList> treeLists(List<String> themeNos) {
        LambdaQueryWrapper<RdeRiskVariableTheme> wrapper = null;
        if (themeNos != null && themeNos.size() > 0) {
            wrapper = new LambdaQueryWrapper<>();
            wrapper.in(RdeRiskVariableTheme::getThemeNo, themeNos);
        }
        List<RdeRiskVariableTheme> themes = themeMapper.selectList(wrapper);
        if (themes == null || themes.size() <= 0)
            return null;
        List<TreeList> themesTree = themes.stream().map(theme -> {
            TreeList treeList = new TreeList();
            treeList.setName(theme.getName());
            treeList.setCode(theme.getKeycode());
            treeList.setNo(theme.getThemeNo());
            return treeList;
        }).collect(Collectors.toList());

        //寻找主题下面的对象
        themesTree.stream().forEach(theme -> {
            LambdaQueryWrapper<RdeRiskVariableGroup> groupWrapper = new LambdaQueryWrapper<>();
            groupWrapper.eq(RdeRiskVariableGroup::getThemeNo, theme.getNo());
            List<RdeRiskVariableGroup> groups = groupMapper.selectList(groupWrapper);
            List<TreeList> groupTree = groups.stream().map(group -> {
                TreeList treeList = new TreeList();
                treeList.setName(group.getName());
                treeList.setCode(group.getKeycode());
                treeList.setNo(group.getGroupNo());
                return treeList;
            }).collect(Collectors.toList());
            if (groupTree != null && groupTree.size() > 0)
                theme.setChridren(groupTree);

            //寻找对象下面的属性
            groupTree.stream().forEach(group -> {
                LambdaQueryWrapper<RdeRiskVariableRecord> recordWrapper = new LambdaQueryWrapper<>();
                recordWrapper.eq(RdeRiskVariableRecord::getGroupNo, group.getNo());
                List<RdeRiskVariableRecord> records = recordMapper.selectList(recordWrapper);
                List<TreeList> recordTree = records.stream().map(record -> {
                    FieldTree treeList = new FieldTree();
                    treeList.setName(record.getName());
                    treeList.setCode(record.getCode());
                    treeList.setNo(record.getRecordNo());
                    treeList.setFieldType(Integer.valueOf(record.getType()));
                    return treeList;
                }).collect(Collectors.toList());
                if (recordTree != null && recordTree.size() > 0)
                    group.setChridren(recordTree);
            });
        });
        return themesTree;
    }

    public void syncSourceData(List<InterfaceSourceManage> source) {
        if (CollUtil.isEmpty(source)) {
            return;
        }
        source.stream().map(s -> {
            RdeRiskVariableTheme theme = new RdeRiskVariableTheme();
            theme.setName(s.getDataName());
            theme.setKeycode(s.getDataName());
            theme.setPackageType(getPackageType(s.getInterfaceDataType()));
            theme.setCreateUserId(s.getUserId().intValue());
            theme.setThemeNo(IdConstant.DATA_MIDDLE_STATION_PREFIX.concat(String.valueOf(idGeneratorService.generateUniqueId())));
            theme.setInterfaceSourceNo(s.getInterfaceSourceNo());
            theme.setUserId(s.getUserId());
            theme.setDeptId(s.getDeptId());
            return theme;
        }).forEach(themeMapper::insert);
    }

    private String getPackageType(Integer interfaceDataType) {
        final String packageType;
        switch (interfaceDataType) {
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
}
