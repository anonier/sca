package com.boredou.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.boredou.common.entity.AuthToken;
import com.boredou.user.model.dto.NewRoleDto;
import com.boredou.user.model.dto.NewUserDto;
import com.boredou.user.model.dto.SysUserDto;
import com.boredou.user.model.entity.SysUser;

public interface UserService extends IService<SysUser> {

    /**
     * 根据id获取用户信息
     *
     * @param id
     * @return {@link SysUser}
     */
    SysUser getUserById(int id);

    /**
     * 登入
     *
     * @param username 用户名称
     * @param password 用户密码
     * @return {@link AuthToken}
     */
    AuthToken login(String username, String password);

    /**
     * 新建用户
     *
     * @param dto {@link NewUserDto}
     * @return {@link AuthToken}
     */
    void newUser(NewUserDto dto);

    /**
     * 新建用户
     *
     * @param dto {@link SysUserDto}
     * @return Sting
     */
    String newSysUser(SysUserDto dto);

    /**
     * 从redis获取token
     *
     * @param token token
     * @return {@link AuthToken}
     */
    AuthToken getUserToken(String token);

    /**
     * 删除token
     *
     * @param accessToken accessToken
     */
    void delToken(String accessToken);

    /**
     * 从cookie中获取token
     *
     * @return String
     */
    String getTokenFormCookie();


    /**
     * 清除cookie
     *
     * @param token token
     */
    void clearCookie(String token);

    /**
     * 保存cookie
     *
     * @param token token
     */
    void saveCookie(String token);

    /**
     * 新建角色
     *
     * @param dto {@link NewRoleDto}
     */
    void newRole(NewRoleDto dto);
}
