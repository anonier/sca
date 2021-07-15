package com.boredou.user.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.boredou.common.annotation.DetailLog;
import com.boredou.common.config.SwaggerConfig;
import com.boredou.common.entity.AuthToken;
import com.boredou.common.entity.Response;
import com.boredou.common.enums.ResponseMsgEnum;
import com.boredou.user.model.dto.EditRoleDto;
import com.boredou.user.model.dto.NewRoleDto;
import com.boredou.user.model.dto.NewUserDto;
import com.boredou.user.model.entity.SysUser;
import com.boredou.user.model.result.SignInResult;
import com.boredou.user.model.vo.EditRoleVo;
import com.boredou.user.model.vo.NewRoleVo;
import com.boredou.user.model.vo.NewUserVo;
import com.boredou.user.model.vo.SignInVo;
import com.boredou.user.service.SysRoleService;
import com.boredou.user.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("user")
@Api(tags = SwaggerConfig.USER)
public class UserCtrl {

    @Resource
    private SysUserService sysUserService;
    @Resource
    private SysRoleService sysRoleService;

    @ApiOperation("登入")
    @PostMapping("signIn")
    public Response<SignInResult> signIn(@RequestBody @Validated @ApiParam(name = "登入对象", value = "json格式", required = true) SignInVo vo) {
        AuthToken authToken = sysUserService.login(vo.getUsername(), vo.getPassword());
        return Response.success(SignInResult.builder().accessToken(authToken.getJti()).jwtToken(authToken.getAccess_token()).build());
    }

    @DetailLog
    @ApiOperation("重置密码")
    @PostMapping("resetPasswd")
    public Response resetPasswd() {
        sysUserService.resetPasswd();
        return Response.success();
    }

    @ApiOperation("修改密码")
    @PostMapping("modifyPasswd")
    public Response<SignInResult> modifyPasswd(@ApiParam(name = "密码", value = "String", required = true) @RequestParam("password") String password) {
        sysUserService.modifyPasswd(password);
        return Response.success();
    }

    @ApiOperation("新建角色")
    @PostMapping("newRole")
    public Response newRole(@RequestBody @Validated @ApiParam(name = "添加角色对象", value = "json格式", required = true) NewRoleVo vo) {
        sysRoleService.newRole(BeanUtil.copyProperties(vo, NewRoleDto.class));
        return Response.success();
    }

    @ApiOperation("编辑角色权限")
    @PostMapping("editRole")
    public Response editRole(@RequestBody @Validated @ApiParam(name = "编辑角色对象", value = "json格式", required = true) EditRoleVo vo) {
        sysRoleService.editRole(BeanUtil.copyProperties(vo, EditRoleDto.class));
        return Response.success();
    }

    @DetailLog
    @ApiOperation("禁用角色")
    @PostMapping("banRole")
    public Response banRole(@ApiParam(name = "角色id", value = "String", required = true) @RequestParam("id") String id) {
        sysRoleService.banRole(id);
        return Response.success();
    }

    @PreAuthorize("hasPermission('member_management')")
    @ApiOperation("添加用户")
    @PostMapping("newUser")
    public Response newUser(@RequestBody @Validated @ApiParam(name = "添加用户对象", value = "json格式", required = true) NewUserVo vo) {
        sysUserService.newUser(BeanUtil.copyProperties(vo, NewUserDto.class));
        return Response.success();
    }

    @ApiOperation("禁用用户")
    @PostMapping("banUser")
    public Response banUser(@ApiParam(name = "用户id", value = "String", required = true) @RequestParam("id") String id) {
        sysUserService.banUser(id);
        return Response.success();
    }

    @ApiOperation("获取jwtToken")
    @GetMapping("userJwt")
    public Response userJwt() {
        AuthToken userToken = sysUserService.getUserToken(sysUserService.getTokenFormCookie());
        return ObjectUtil.isEmpty(userToken) ? Response.fail() : Response.success(userToken);
    }

    @ApiOperation("获取用户信息")
    @GetMapping("detail")
    public Response getUser(@ApiParam(name = "用户id", value = "Integer", required = true) @RequestParam("id") Integer id) {
        SysUser user = sysUserService.getUserById(id);
        return ObjectUtil.isEmpty(user) ? new Response<>(ResponseMsgEnum.USER_ILLEGAL) : Response.success(user);
    }

    @ApiOperation("获取权限信息")
    @GetMapping("authorization")
    public Object authorization() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities();
    }

    @ApiOperation("退出")
    @PostMapping("logout")
    public Response logout() {
        sysUserService.logout();
        return Response.success();
    }

    @ApiOperation("最近动态")
    @GetMapping("RecentDynamic")
    public Response RecentDynamic() {
        return Response.success(sysUserService.RecentDynamic());
    }

//    @PostMapping("/add")
//    @ApiOperation("通用添加数据源（推荐）")
//    public Set<String> add(@Validated @RequestBody DataSourceDTO dto) {
//        DataSourceProperty dataSourceProperty = new DataSourceProperty();
//        BeanUtils.copyProperties(dto, dataSourceProperty);
//        DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSource;
//        DataSource dataSource = dataSourceCreator.createDataSource(dataSourceProperty);
//        ds.addDataSource(dto.getPollName(), dataSource);
//        return ds.getCurrentDataSources().keySet();
//    }
//
//    @DeleteMapping
//    @ApiOperation("删除数据源")
//    public String remove(String name) {
//        DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSource;
//        ds.removeDataSource(name);
//        return "删除成功";
//    }

}
