package com.boredou.user.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.boredou.common.entity.AuthToken;
import com.boredou.common.entity.ResponseVO;
import com.boredou.common.enums.ResponseMsgEnum;
import com.boredou.user.exception.BusiException;
import com.boredou.user.model.dto.NewRoleDto;
import com.boredou.user.model.dto.NewUserDto;
import com.boredou.user.model.dto.SysUserDto;
import com.boredou.user.model.entity.SysUser;
import com.boredou.user.model.vo.BossLoginVo;
import com.boredou.user.model.vo.NewRoleVo;
import com.boredou.user.model.vo.NewUserVo;
import com.boredou.user.model.vo.SysUserVo;
import com.boredou.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("user")
@Api(tags = "用户模块", description = "用户相关模块接口")
public class UserCtrl {

    @Resource
    private UserService userService;

    @PostMapping("bossLogin")
    public ResponseVO pwdLogin(@Validated BossLoginVo vo) {
        AuthToken authToken = userService.login(vo.getUsername(), vo.getPassword());
        userService.saveCookie(authToken.getJwt_token());
        return ResponseVO.success(authToken.getAccess_token());
    }

    @PostMapping("newRole")
    public ResponseVO newRole(@RequestBody @Validated NewRoleVo vo) {
        try {
            userService.newRole(BeanUtil.copyProperties(vo, NewRoleDto.class));
            return ResponseVO.success();
        } catch (BusiException e) {
            return ResponseVO.fail(e.getResultCode().getCode(), e.getMessage());
        }
    }

    //    @PreAuthorize("hasPermission('member_management')")
    @PostMapping("newUser")
    public ResponseVO newUser(@RequestBody @Validated NewUserVo vo) {
        try {
            userService.newUser(BeanUtil.copyProperties(vo, NewUserDto.class));
            return ResponseVO.success();
        } catch (BusiException e) {
            return ResponseVO.fail(e.getResultCode().getCode(), e.getMessage());
        }
    }

    @GetMapping("userJwt")
    public ResponseVO userJwt() {
        AuthToken userToken = userService.getUserToken(userService.getTokenFormCookie());
        return ObjectUtil.isEmpty(userToken) ? ResponseVO.fail() : ResponseVO.success(userToken);
    }

    @RequestMapping("/{id}")
    public ResponseVO<SysUser> oneUser(@PathVariable("id") int id) {
        SysUser user = userService.getUserById(id);
        return user == null ? new ResponseVO<>(ResponseMsgEnum.USER_ILLEGAL) : ResponseVO.success(user);
    }

    @GetMapping("readOnly")
    public ResponseVO getUser(@ApiParam(value = "用户ID", required = true) @RequestParam("id") int id) {
        SysUser user = userService.getUserById(id);
        return ObjectUtil.isEmpty(user) ? new ResponseVO<>(ResponseMsgEnum.USER_ILLEGAL) : ResponseVO.success(user);
    }

    @PostMapping("writeOnly")
    public ResponseVO newSysUser(@ApiParam(value = "SysUserVo", required = true) @RequestBody @Validated SysUserVo sysUserVo) {
        SysUserDto dto = BeanUtil.copyProperties(sysUserVo, SysUserDto.class);
        return ResponseVO.success(userService.newSysUser(dto));
    }

    @GetMapping("authorization")
    public Object authorization() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities();
    }

    @PostMapping("logout")
    public ResponseVO logout() {
        //取出cookie中的用户身份令牌
        String uid = userService.getTokenFormCookie();
        //删除redis中的token
        try {
            userService.delToken(uid);
        } catch (Exception e) {
            return new ResponseVO<>(ResponseMsgEnum.FAILED);
        }
        //清除cookie
        userService.clearCookie(uid);
        return ResponseVO.success();
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
