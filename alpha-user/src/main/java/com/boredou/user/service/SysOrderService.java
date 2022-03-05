package com.boredou.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.boredou.user.model.entity.SysOrder;
import com.boredou.user.model.vo.OrderListVo;

import java.util.List;

public interface SysOrderService extends IService<SysOrder> {

    /**
     * 获取订单列表
     *
     * @param orderListVo {@link OrderListVo}
     * @return {@link List<SysOrder>}
     */
    IPage<SysOrder> orderList(OrderListVo orderListVo);
}
