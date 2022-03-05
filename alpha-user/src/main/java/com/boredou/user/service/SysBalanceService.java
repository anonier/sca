package com.boredou.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.boredou.user.model.entity.SysBalance;
import com.boredou.user.model.vo.FlowDetailVo;

import java.util.List;

public interface SysBalanceService extends IService<SysBalance> {

    /**
     * 获取收支明细
     *
     * @param vo {@link FlowDetailVo}
     * @return {@link List<SysBalance>}
     */
    IPage<SysBalance> flowDetail(FlowDetailVo vo);
}
