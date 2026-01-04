package com.value.decision.process.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.value.decision.process.mapper.ProcessNodeMapper;
import com.value.decision.process.model.ProcessNode;
import com.value.decision.process.service.IProcessNodeService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 流程节点表 服务实现类
 * </p>
 *
 * @author hc
 * @since 2023-08-16
 */
@Service
public class ProcessNodeServiceImpl extends ServiceImpl<ProcessNodeMapper, ProcessNode> implements IProcessNodeService {

}
