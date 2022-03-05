package com.boredou.user.controller;

import cn.hutool.core.bean.BeanUtil;
import com.boredou.common.annotation.SysLog;
import com.boredou.common.config.SwaggerConfig;
import com.boredou.common.module.entity.Response;
import com.boredou.user.model.dto.CompanyEditDto;
import com.boredou.user.model.dto.NewDeptDto;
import com.boredou.user.model.dto.UpdateDeptDto;
import com.boredou.user.model.vo.CompanyEditVo;
import com.boredou.user.model.vo.NewDeptVo;
import com.boredou.user.model.vo.UpdateDeptVo;
import com.boredou.user.service.CompanyDeptService;
import com.boredou.user.service.CompanyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 企业管理接口
 *
 * @author yb
 * @since 2021/5/27
 */
@RestController
@RequestMapping("/boss")
@Api(tags = SwaggerConfig.COMPANY)
public class CompanyCtrl {

    @Resource
    private CompanyService companyService;
    @Resource
    private CompanyDeptService companyDeptService;

    @ApiOperation("获取公司信息")
    @GetMapping("detail")
    public Response<Object> getCoById(@ApiParam(name = "id", value = "公司id", required = true) @RequestParam("id") String id) {
        return Response.success(companyService.getCoById(id));
    }

    @SysLog
    @ApiOperation("企业信息编辑及登入设置")
    @GetMapping("edit")
    public Response<Object> edit(@RequestBody @Validated @ApiParam(name = "CompanyEditVo", value = "企业信息编辑对象", required = true) CompanyEditVo vo) {
        companyService.edit(BeanUtil.copyProperties(vo, CompanyEditDto.class));
        return Response.success();
    }

    @ApiOperation("获取部门信息")
    @GetMapping("dept/detail")
    public Response<Object> getCoDeptById(@ApiParam(name = "id", value = "公司id", required = true) @RequestParam("id") Integer id) {
        return Response.success(companyService.getCoDeptById(id));
    }

    @ApiOperation("获取企业架构信息")
    @GetMapping("dept/struct")
    public Response<Object> getCoDeptStructById(@ApiParam(name = "id", value = "公司id", required = true) @RequestParam("id") String id) {
        return Response.success(companyService.getCoStructById(id));
    }

    @SysLog
    @ApiOperation("添加部门")
    @PostMapping("newDept")
    public Response<Object> newDept(@RequestBody @Validated @ApiParam(name = "NewDeptVo", value = "新建部门对象", required = true) NewDeptVo vo) {
        companyDeptService.newCoDept(BeanUtil.copyProperties(vo, NewDeptDto.class));
        return Response.success();
    }

    @SysLog
    @ApiOperation("编辑部门")
    @PostMapping("updateDept")
    public Response<Object> updateDept(@RequestBody @Validated @ApiParam(name = "UpdateDeptVo", value = "编辑部门对象", required = true) UpdateDeptVo vo) {
        companyDeptService.updateDept(BeanUtil.copyProperties(vo, UpdateDeptDto.class));
        return Response.success();
    }
}