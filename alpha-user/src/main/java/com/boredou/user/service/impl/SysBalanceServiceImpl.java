package com.boredou.user.service.impl;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boredou.user.model.entity.SysBalance;
import com.boredou.user.model.mapper.SysBalanceMapper;
import com.boredou.user.model.vo.FlowDetailVo;
import com.boredou.user.service.SysBalanceService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RefreshScope
@DSTransactional
public class SysBalanceServiceImpl extends ServiceImpl<SysBalanceMapper, SysBalance> implements SysBalanceService {

    @Override
    public IPage<SysBalance> flowDetail(FlowDetailVo vo) {
        return this.baseMapper.selectPage(new Page<>(vo.getCurrent(), vo.getSize()), new LambdaQueryWrapper<SysBalance>()
                .eq(SysBalance::getCompanyId, vo.getId())
                .eq(StringUtils.isNotBlank(vo.getOrderId()), SysBalance::getOrderId, vo.getOrderId())
                .eq(StringUtils.isNotBlank(vo.getTransactionType()), SysBalance::getTransactionType, vo.getTransactionType())
                .ge(Optional.ofNullable(vo.getStartDate()).isPresent(), SysBalance::getCreateTime, vo.getStartDate())
                .le(Optional.ofNullable(vo.getFinishDate()).isPresent(), SysBalance::getCreateTime, vo.getFinishDate()));
    }
}
