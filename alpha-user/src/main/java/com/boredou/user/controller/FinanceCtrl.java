package com.boredou.user.controller;

import com.boredou.common.annotation.SysLog;
import com.boredou.common.config.SwaggerConfig;
import com.boredou.common.module.entity.Response;
import com.boredou.user.model.vo.ContractListVo;
import com.boredou.user.model.vo.FlowDetailVo;
import com.boredou.user.model.vo.OrderListVo;
import com.boredou.user.model.vo.RechargeVo;
import com.boredou.user.service.CompanyService;
import com.boredou.user.service.SysBalanceService;
import com.boredou.user.service.SysContractService;
import com.boredou.user.service.SysOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 财务管理接口
 *
 * @author yb
 * @since 2021/9/29
 */
@RestController
@RequestMapping("/finance")
@Api(tags = SwaggerConfig.FINANCE)
public class FinanceCtrl {
    @Resource
    private CompanyService companyService;
    @Resource
    private SysBalanceService sysBalanceService;
    @Resource
    private SysOrderService sysOrderService;
    @Resource
    private SysContractService sysContractService;

    @ApiOperation("获取可用余额")
    @GetMapping("getBalance")
    public Response<Object> getBalance(@ApiParam(name = "id", value = "公司Id", required = true) @RequestParam("id") String id) {
        return Response.success(companyService.getBalance(id));
    }

    @SysLog
    @ApiOperation("充值")
    @PostMapping("recharge")
    public Response<Object> recharge(@RequestBody @Valid @ApiParam(name = "RechargeVo", value = "充值入参对象", required = true) RechargeVo vo) {
        companyService.recharge(vo);
        return Response.success();
    }

    @ApiOperation("获取收支明细")
    @PostMapping("flowDetail")
    public Response<Object> flowDetail(@RequestBody @Valid @ApiParam(name = "FlowDetailVo", value = "收支明细入参对象", required = true) FlowDetailVo vo) {
        return Response.success(sysBalanceService.flowDetail(vo));
    }

    @ApiOperation("获取订单列表")
    @PostMapping("orderList")
    public Response<Object> orderList(@RequestBody @Valid @ApiParam(name = "OrderListVo", value = "订单列表入参对象", required = true) OrderListVo vo) {
        return Response.success(sysOrderService.orderList(vo));
    }

    @ApiOperation("获取合同列表")
    @PostMapping("contractList")
    public Response<Object> contractList(@RequestBody @Valid @ApiParam(name = "ContractListVo", value = "合同列表入参对象", required = true) ContractListVo vo) {
        return Response.success(sysContractService.contractList(vo));
    }
}