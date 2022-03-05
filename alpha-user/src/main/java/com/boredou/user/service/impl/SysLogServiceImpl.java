package com.boredou.user.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boredou.user.model.dto.RecentDynamicDto;
import com.boredou.user.model.entity.SysLog;
import com.boredou.user.model.mapper.SysLogMapper;
import com.boredou.user.service.SysLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RefreshScope
@DSTransactional
public class SysLogServiceImpl extends ServiceImpl<SysLogMapper, SysLog> implements SysLogService {

    @Override
    public IPage<SysLog> getLogs(RecentDynamicDto dto) {
        return this.baseMapper.selectPage(new Page<>(dto.getCurrent(), dto.getSize()), new LambdaQueryWrapper<SysLog>()
                .eq(StringUtils.isNotBlank(dto.getUsername()), SysLog::getName, dto.getUsername())
                .eq(StringUtils.isNotBlank(dto.getModule()), SysLog::getModule, dto.getModule())
                .ge(Optional.ofNullable(dto.getStartDate()).isPresent(), SysLog::getOperatorTime, dto.getStartDate())
                .le(Optional.ofNullable(dto.getFinishDate()).isPresent(), SysLog::getOperatorTime, dto.getFinishDate()));
    }

    @Override
    @DS("write")
    public void saveLog(SysLog sysLog) {
        this.save(sysLog);
    }
}
