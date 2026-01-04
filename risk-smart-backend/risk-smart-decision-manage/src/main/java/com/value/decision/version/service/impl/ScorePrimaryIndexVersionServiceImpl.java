package com.value.decision.version.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.value.decision.version.domain.ScorePrimaryIndexVersion;
import com.value.decision.version.mapper.ScorePrimaryIndexVersionMapper;
import com.value.decision.version.service.ScorePrimaryIndexVersionService;
import org.springframework.stereotype.Service;

@Service
public class ScorePrimaryIndexVersionServiceImpl extends ServiceImpl<ScorePrimaryIndexVersionMapper, ScorePrimaryIndexVersion> implements ScorePrimaryIndexVersionService {
}
