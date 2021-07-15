package com.boredou.user.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.boredou.common.config.SwaggerConfig;
import com.boredou.common.entity.Response;
import com.boredou.common.enums.ResponseMsgEnum;
import com.boredou.user.model.dto.NewDeptDto;
import com.boredou.user.model.dto.UpdateDeptDto;
import com.boredou.user.model.entity.Company;
import com.boredou.user.model.entity.CompanyDept;
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
import java.util.List;

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
    public Response getCoById(@ApiParam(value = "公司id", required = true) @RequestParam("id") Integer id) {
        Company co = companyService.getCoById(id);
        return ObjectUtil.isEmpty(co) ? new Response<>(ResponseMsgEnum.USER_ILLEGAL) : Response.success(co);
    }

    @ApiOperation("获取部门信息")
    @GetMapping("/dept/detail")
    public Response getCoDeptById(@ApiParam(value = "公司id", required = true) @RequestParam("id") Integer id) {
        List<CompanyDept> dept = companyService.getCoDeptById(id);
        return ObjectUtil.isEmpty(dept) ? new Response<>(ResponseMsgEnum.USER_ILLEGAL) : Response.success(dept);
    }

    @ApiOperation("添加部门")
    @PostMapping("/newDept")
    public Response newDept(@RequestBody @Validated @ApiParam(name = "新建部门对象", value = "json格式", required = true) NewDeptVo vo) {
        boolean b = companyDeptService.newCoDept(BeanUtil.copyProperties(vo, NewDeptDto.class));
        return b ? Response.success() : new Response<>(ResponseMsgEnum.USER_ILLEGAL);
    }

    @ApiOperation("编辑部门")
    @PostMapping("/updateDept")
    public Response updateDept(@RequestBody @Validated @ApiParam(name = "编辑部门对象", value = "json格式", required = true) UpdateDeptVo vo) {
        companyDeptService.updateDept(BeanUtil.copyProperties(vo, UpdateDeptDto.class));
        return Response.success();
    }

}
