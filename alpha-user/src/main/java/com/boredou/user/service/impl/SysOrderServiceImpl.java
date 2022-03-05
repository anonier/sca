package com.boredou.user.service.impl;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boredou.user.model.entity.SysOrder;
import com.boredou.user.model.mapper.SysOrderMapper;
import com.boredou.user.model.vo.OrderListVo;
import com.boredou.user.service.SysOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RefreshScope
@DSTransactional
public class SysOrderServiceImpl extends ServiceImpl<SysOrderMapper, SysOrder> implements SysOrderService {

    @Override
    public IPage<SysOrder> orderList(OrderListVo vo) {
        return this.baseMapper.selectPage(new Page<>(vo.getCurrent(), vo.getSize()), new LambdaQueryWrapper<SysOrder>()
                .eq(SysOrder::getCompanyId, vo.getId())
                .eq(StringUtils.isNotBlank(vo.getType()), SysOrder::getType, vo.getType())
                .eq(StringUtils.isNotBlank(vo.getCategory()), SysOrder::getCategory, vo.getCategory())
                .eq(StringUtils.isNotBlank(vo.getStatus()), SysOrder::getStatus, vo.getStatus())
                .eq(StringUtils.isNotBlank(vo.getOrderId()), SysOrder::getOrderId, vo.getOrderId())
                .eq(StringUtils.isNotBlank(vo.getOrderAccount()), SysOrder::getOrderAccount, vo.getOrderAccount())
                .ge(Optional.ofNullable(vo.getStartDate()).isPresent(), SysOrder::getCreateTime, vo.getStartDate())
                .le(Optional.ofNullable(vo.getFinishDate()).isPresent(), SysOrder::getCreateTime, vo.getFinishDate()));
    }
}
