package com.boredou.user.service.impl;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boredou.user.model.entity.SysContract;
import com.boredou.user.model.mapper.SysContractMapper;
import com.boredou.user.model.vo.ContractListVo;
import com.boredou.user.service.SysContractService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RefreshScope
@DSTransactional
public class SysContractServiceImpl extends ServiceImpl<SysContractMapper, SysContract> implements SysContractService {

    @Override
    public IPage<SysContract> contractList(ContractListVo vo) {
        return this.baseMapper.selectPage(new Page<>(vo.getCurrent(), vo.getSize()), new LambdaQueryWrapper<SysContract>()
                .eq(StringUtils.isNotBlank(vo.getContractId()), SysContract::getContractId, vo.getContractId())
                .eq(StringUtils.isNotBlank(vo.getContractName()), SysContract::getContractName, vo.getContractName())
                .eq(StringUtils.isNotBlank(vo.getType()), SysContract::getType, vo.getType())
                .eq(StringUtils.isNotBlank(vo.getStatus()), SysContract::getStatus, vo.getStatus())
                .eq(StringUtils.isNotBlank(vo.getCertificateStatus()), SysContract::getCertificateStatus, vo.getCertificateStatus())
                .ge(Optional.ofNullable(vo.getStartDate()).isPresent(), SysContract::getCreateTime, vo.getStartDate())
                .le(Optional.ofNullable(vo.getFinishDate()).isPresent(), SysContract::getCreateTime, vo.getFinishDate()));
    }
}
