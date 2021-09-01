package com.boredou.user.controller;

import cn.hutool.core.bean.BeanUtil;
import com.boredou.common.annotation.DetailLog;
import com.boredou.common.annotation.SysLog;
import com.boredou.common.config.SwaggerConfig;
import com.boredou.common.module.entity.Response;
import com.boredou.user.model.dto.EditRoleDto;
import com.boredou.user.model.dto.NewRoleDto;
import com.boredou.user.model.dto.RecentDynamicDto;
import com.boredou.user.model.dto.UserDto;
import com.boredou.user.model.result.LoginResult;
import com.boredou.user.model.result.SignInResult;
import com.boredou.user.model.vo.*;
import com.boredou.user.service.SysLogService;
import com.boredou.user.service.SysRoleService;
import com.boredou.user.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 用户管理接口
 *
 * @author yb
 * @since 2021/5/27
 */
@RestController
@RequestMapping("user")
@Api(tags = SwaggerConfig.USER)
public class UserCtrl {

    @Resource
    private SysUserService sysUserService;
    @Resource
    private SysRoleService sysRoleService;
    @Resource
    private SysLogService sysLogService;

    @SysLog
    @ApiOperation("登入")
    @PostMapping("signIn")
    public Response signIn(@RequestBody @Validated @ApiParam(name = "SignInVo", value = "登入对象", required = true) SignInVo vo) {
        LoginResult loginResult = sysUserService.login(vo.getType(), vo.getUsername(), vo.getPassword(), vo.getCode());
        return Response.success(SignInResult.builder().accessToken(loginResult.getJti()).jwtToken(loginResult.getAccess_token()).build());
    }

    @SysLog
    @ApiOperation("手机号绑定钉钉")
    @PostMapping("bindDingTalkByPhone")
    public Response bindDingTalkByPhone() {
        sysUserService.bindDingTalkByPhone(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        return Response.success();
    }

    @SysLog
    @ApiOperation("扫码绑定钉钉")
    @PostMapping("bindDingTalkByQrcode")
    public Response bindDingTalkByQrcode(@ApiParam(name = "code", value = "二维码Code") @RequestParam("code") String code) {
        sysUserService.bindDingTalkByQrcode(code);
        return Response.success();
    }

    @SysLog
    @ApiOperation("发送钉钉验证码")
    @GetMapping("sendDingTalkCode")
    public Response sendDingTalkCode(@ApiParam(name = "username", value = "用户名", required = true) @RequestParam("username") String username) {
        return Response.success(sysUserService.sendDingTalkCode(username));
    }

    @DetailLog
    @ApiOperation("重置密码")
    @PostMapping("resetPasswd")
    public Response resetPasswd(@ApiParam(name = "username", value = "用户名") @RequestParam(value = "username", required = false) String username) {
        if (StringUtils.isBlank(username)) {
            sysUserService.resetPasswd("");
        } else {
            sysUserService.resetPasswd(username);
        }
        return Response.success();
    }

    @SysLog
    @ApiOperation("修改密码")
    @PostMapping("modifyPasswd")
    public Response modifyPasswd(@ApiParam(name = "password", value = "密码", required = true) @RequestParam("password") String password) {
        sysUserService.modifyPasswd(password);
        return Response.success();
    }

    @SysLog
    @ApiOperation("新建角色")
    @PostMapping("newRole")
    public Response newRole(@RequestBody @Validated @ApiParam(name = "NewRoleVo", value = "添加角色对象", required = true) NewRoleVo vo) {
        sysRoleService.newRole(BeanUtil.copyProperties(vo, NewRoleDto.class));
        return Response.success();
    }

    @SysLog
    @ApiOperation("编辑角色权限")
    @PostMapping("editRole")
    public Response editRole(@RequestBody @Validated @ApiParam(name = "EditRoleVo", value = "编辑角色对象", required = true) EditRoleVo vo) {
        sysRoleService.editRole(BeanUtil.copyProperties(vo, EditRoleDto.class));
        return Response.success();
    }

    @SysLog
    @ApiOperation("禁用角色")
    @PostMapping("banRole")
    public Response banRole(@ApiParam(name = "id", value = "角色id", required = true) @RequestParam("id") String id) {
        sysRoleService.banRole(id);
        return Response.success();
    }

    @SysLog
    @ApiOperation("添加用户")
    @PostMapping("newUser")
    public Response newUser(@RequestBody @Validated @ApiParam(name = "NewUserVo", value = "添加用户对象", required = true) UserVo vo) {
        sysUserService.newUser(BeanUtil.copyProperties(vo, UserDto.class));
        return Response.success();
    }

    @SysLog
    @ApiOperation("编辑/禁用用户")
    @PostMapping("editUser")
    public Response editUser(@RequestBody @Validated @ApiParam(name = "NewUserVo", value = "编辑或禁用用户对象", required = true) UserVo vo) {
        sysUserService.editUser(BeanUtil.copyProperties(vo, UserDto.class));
        return Response.success();
    }

    @ApiOperation("获取权限信息")
    @GetMapping("authorization")
    public Response authorization() {
        return Response.success(sysUserService.getPermissions(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString()));
    }

    @ApiOperation("退出")
    @PostMapping("logout")
    public Response logout() {
        sysUserService.logout();
        return Response.success();
    }

    @ApiOperation("最近动态")
    @GetMapping("recentDynamic")
    public Response RecentDynamic(@Validated @ApiParam(name = "RecentDynamicVo", value = "最近动态参数", required = true) RecentDynamicVo vo) {
        RecentDynamicDto recentDynamicDto = BeanUtil.copyProperties(vo, RecentDynamicDto.class);
        return Response.success(sysLogService.getLogsByName(recentDynamicDto.setUsername(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString())));
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
