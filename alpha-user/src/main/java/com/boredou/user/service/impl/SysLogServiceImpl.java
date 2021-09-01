package com.boredou.user.service.impl;

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
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RefreshScope
@Transactional
public class SysLogServiceImpl extends ServiceImpl<SysLogMapper, SysLog> implements SysLogService {

    @Override
    public IPage<SysLog> getLogsByName(RecentDynamicDto dto) {
        Page<SysLog> page = new Page<>(dto.getCurrent(), dto.getSize());
        LambdaQueryWrapper<SysLog> wrapper = new LambdaQueryWrapper<SysLog>().eq(SysLog::getName, dto.getUsername());
        Optional.ofNullable(dto.getStartDate())
                .ifPresent(s ->
                        wrapper.ge(SysLog::getOperatorTime, s)
                );
        Optional.ofNullable(dto.getFinishDate())
                .ifPresent(f ->
                        wrapper.le(SysLog::getOperatorTime, f)
                );
        if (StringUtils.isNotBlank(dto.getId())) {
            wrapper.eq(SysLog::getOperatorModule, dto.getId());
        }
        return this.baseMapper.selectPage(page, wrapper);
    }
}
