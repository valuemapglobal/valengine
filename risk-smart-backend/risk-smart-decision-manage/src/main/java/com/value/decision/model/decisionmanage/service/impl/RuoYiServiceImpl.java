package com.value.decision.model.decisionmanage.service.impl;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.risksmart.common.core.exception.ServiceException;
import com.value.decision.model.decisionmanage.feign.RuoYiUserFeign;
import com.value.decision.model.decisionmanage.service.RuoYiService;
import com.risksmart.common.core.constant.SecurityConstants;
import com.risksmart.common.core.domain.R;
import com.risksmart.system.api.RemoteDictDataService;
import com.risksmart.system.api.domain.SysDictData;
import com.risksmart.system.domain.SysUser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 若依服务实现类
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@Service
@Slf4j
@AllArgsConstructor
public class RuoYiServiceImpl implements RuoYiService {
    private final RemoteDictDataService ruoYiDicService;
    private final RuoYiUserFeign userService;

    @Override
    public List<Long> getStandardDepts() {
        final com.risksmart.common.core.domain.R<List<SysDictData>> result = ruoYiDicService.dictType("decision_standard");
        if (result.getCode()!= 200){
            throw new ServiceException("获取标准部门失败");
        }
        final List<SysDictData> data = result.getData();
        if (data==null || data.size()==0){
            throw new ServiceException("未配置标准部门");
        }
        final List<String> list = data.stream()
                .filter(d -> d.getDictValue()!= null)
                .map(d -> d.getDictValue())
                .collect(Collectors.toList());
        if (list==null || list.size()==0){
            throw new ServiceException("未设置标准部门值");
        }
        final List<Long> depts = new ArrayList<>();
        list.stream().forEach(p -> {
            if (!JSONUtil.isJsonArray(p)){
                return ;
            }
            depts.addAll(JSON.parseArray(p, Long.class));
        });
        if (depts==null || depts.size()==0){
            throw new ServiceException("非法标准部门值");
        }
        return depts;
    }

    @Override
    public SysUser getUserById(@NonNull Long id) {
        SysUser user = new SysUser();
        user.setUserId(id);
        R<SysUser> result = userService.selectOne(user, SecurityConstants.INNER);
        return result.getData();
    }
}
