package com.value.data.service;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.risksmart.common.core.utils.StringUtils;
import com.value.data.common.model.LoginUser;
import com.value.data.common.service.IdGeneratorService;
import com.value.data.constant.IdConstant;
import com.value.data.domain.dto.DeleteRecordDTO;
import com.value.data.domain.dto.RdeRiskVariableRecordDTO;
import com.value.data.domain.entity.InterfaceFieldIdManage;
import com.value.data.domain.entity.RdeRiskVariableGroup;
import com.value.data.domain.entity.RdeRiskVariableRecord;
import com.value.data.domain.vo.PageData;
import com.value.data.domain.vo.RdeRiskVariableRecordVO;
import com.value.data.mapper.RdeRiskVariableGroupMapper;
import com.value.data.mapper.RdeRiskVariableRecordMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RdeRiskVariableRecordService implements BaseService {
    private final RdeRiskVariableRecordMapper mapper;
    private final RdeRiskVariableGroupMapper groupMapper;
    private final IdGeneratorService idGeneratorService;

    /**
     * 查询列表
     */
    public PageData list(RdeRiskVariableRecordDTO query) {
        RdeRiskVariableRecord record = new RdeRiskVariableRecord();
        BeanUtils.copyProperties(query, record);
        LambdaQueryWrapper<RdeRiskVariableRecord> wrapper = new LambdaQueryWrapper<>(record);
        List<RdeRiskVariableRecordVO> voList = new ArrayList<>();
        //分页
        Page<RdeRiskVariableRecord> page = new Page<>(query.getPageNum(), query.getPageSize());
        Page<RdeRiskVariableRecord> pages = mapper.selectPage(page, wrapper);
        List<RdeRiskVariableRecord> list = pages.getRecords();

        for (RdeRiskVariableRecord rdeRiskVariableRecord : list) {
            RdeRiskVariableRecordVO vo = new RdeRiskVariableRecordVO();
            BeanUtils.copyProperties(rdeRiskVariableRecord, vo);
            //处理属性父属性
            String parentNo = rdeRiskVariableRecord.getParentNo();
            if (StringUtils.isNotEmpty(parentNo)) {
                LambdaQueryWrapper<RdeRiskVariableRecord> wrapper1 = new LambdaQueryWrapper<>();
                wrapper1.eq(RdeRiskVariableRecord::getRecordNo, parentNo);
                RdeRiskVariableRecord record1 = mapper.selectOne(wrapper1);
                if (record1 != null)
                    vo.setParentName(record1.getName());
            }
            voList.add(vo);
        }

        PageData pageData = new PageData();
        pageData.setPageNum(pages.getCurrent());
        pageData.setPageSize(pages.getSize());
        pageData.setTotal(pages.getTotal());
        pageData.setRows(voList);
        return pageData;
    }

    /**
     * 新增
     */
    public void submit(RdeRiskVariableRecordDTO record, LoginUser user) {
        //适配接口管理
        if (StringUtils.isEmpty(record.getGroupNo()) && StringUtils.isNotEmpty(record.getInterfaceManageNo())) {
            LambdaQueryWrapper<RdeRiskVariableGroup> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(RdeRiskVariableGroup::getInterfaceManageNo, record.getInterfaceManageNo());
            RdeRiskVariableGroup group = groupMapper.selectOne(wrapper);
            if (group == null) {
                return;
            }
            record.setGroupNo(group.getGroupNo());
        }

        record.setRecordNo(IdConstant.DATA_MIDDLE_STATION_PREFIX.concat(String.valueOf(idGeneratorService.generateUniqueId())));
        RdeRiskVariableRecord record1 = new RdeRiskVariableRecord();
        BeanUtils.copyProperties(record, record1);
        record1.setCreateUserId(user.getUserid().intValue());
        record1.setUserId(user.getUserid())
                .setDeptId(user.getSysUser().getDeptId());
        mapper.insert(record1);
    }

    /**
     * 修改
     */
    public int update(RdeRiskVariableRecordDTO record, LoginUser user) throws Exception {
        //适配接口管理
        if (StringUtils.isEmpty(record.getRecordNo()) && StringUtils.isNotEmpty(record.getInterfaceFieldIdManage())) {
            LambdaQueryWrapper<RdeRiskVariableRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(RdeRiskVariableRecord::getInterfaceFieldIdManage, record.getInterfaceFieldIdManage());
            RdeRiskVariableRecord record2 = mapper.selectOne(wrapper);
            if (record2 == null) {
                return 0;
            }
            record.setRecordNo(record2.getRecordNo());
            record.setGroupNo(record2.getGroupNo());
        }

        RdeRiskVariableRecord record1 = new RdeRiskVariableRecord();
        BeanUtils.copyProperties(record, record1);
        LambdaQueryWrapper<RdeRiskVariableRecord> recordWrapper = new LambdaQueryWrapper<>();
        recordWrapper.eq(RdeRiskVariableRecord::getRecordNo, record.getRecordNo())
                .eq(RdeRiskVariableRecord::getDeptId, user.getSysUser().getDeptId());
        return mapper.update(record1, recordWrapper);
    }

    /**
     * 根据编号删除
     */
    public int delete(DeleteRecordDTO deleteRecordDTO, LoginUser user) {
        if (deleteRecordDTO == null) {
            return 0;
        }

        LambdaQueryWrapper<RdeRiskVariableRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RdeRiskVariableRecord::getDeptId, user.getSysUser().getDeptId());
        if (StringUtils.isNotEmpty(deleteRecordDTO.getRecordNo())) {
            wrapper.eq(RdeRiskVariableRecord::getRecordNo, deleteRecordDTO.getRecordNo());
        }
        if (StringUtils.isNotEmpty(deleteRecordDTO.getInterfaceFieldIdManage())) {
            wrapper.eq(RdeRiskVariableRecord::getInterfaceFieldIdManage, deleteRecordDTO.getInterfaceFieldIdManage());
        }
        RdeRiskVariableRecord rdeRiskVariableRecord = mapper.selectOne(wrapper);
        int delete = mapper.delete(wrapper);
        //删除子属性
        if (rdeRiskVariableRecord != null) {
            deleteSon(rdeRiskVariableRecord.getRecordNo());
        }
        return delete;
    }

    public void deleteSon(String no) {
        //查询所有子属性
        LambdaQueryWrapper<RdeRiskVariableRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RdeRiskVariableRecord::getParentNo, no);
        List<RdeRiskVariableRecord> sonList = mapper.selectList(wrapper);

        LambdaQueryWrapper<RdeRiskVariableRecord> wrapper1 = new LambdaQueryWrapper<>();
        wrapper1.eq(RdeRiskVariableRecord::getParentNo, no);
        mapper.delete(wrapper1);
        //删除子属性
        if (sonList != null && !sonList.isEmpty()) {
            sonList.forEach(son -> {
                //递归删除子属性
                deleteSon(son.getRecordNo());
            });
        }
    }

    public void syncFieldData(List<InterfaceFieldIdManage> fields) {
        if (CollectionUtil.isEmpty(fields)) {
            return;
        }
        List<String> manageNos = fields.stream().map(InterfaceFieldIdManage::getInterfaceManageNo).distinct().collect(Collectors.toList());
        groupMapper.selectList(
                Wrappers.lambdaQuery(RdeRiskVariableGroup.class).in(RdeRiskVariableGroup::getInterfaceManageNo, manageNos)
        );
    }
}
