package com.boredou.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boredou.user.model.entity.SysLog;
import com.boredou.user.model.mapper.SysLogMapper;
import com.boredou.user.service.SysLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RefreshScope
@Transactional
public class SysLogServiceImpl extends ServiceImpl<SysLogMapper, SysLog> implements SysLogService {

    @Override
    public List<SysLog> getLogsByName(String name) {
        return this.list(new LambdaQueryWrapper<SysLog>().eq(SysLog::getName, name));
    }

}
