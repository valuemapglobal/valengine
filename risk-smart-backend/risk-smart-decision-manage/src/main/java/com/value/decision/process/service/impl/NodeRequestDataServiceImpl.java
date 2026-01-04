package com.value.decision.process.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.value.decision.process.mapper.NodeRequestDataMapper;
import com.value.decision.process.model.NodeRequestData;
import com.value.decision.process.service.INodeRequestDataService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 节点请求数据存储表 服务实现类
 * </p>
 *
 * @author hc
 * @since 2023-08-16
 */
@Service
public class NodeRequestDataServiceImpl extends ServiceImpl<NodeRequestDataMapper, NodeRequestData> implements INodeRequestDataService {

}
