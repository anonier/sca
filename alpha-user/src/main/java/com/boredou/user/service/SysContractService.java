package com.boredou.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.boredou.user.model.entity.SysContract;
import com.boredou.user.model.vo.ContractListVo;

public interface SysContractService extends IService<SysContract> {

    /**
     * 合同列表
     *
     * @param contractListVo {@link ContractListVo}
     * @return {@link IPage<SysContract>}
     */
    IPage<SysContract> contractList(ContractListVo contractListVo);
}
