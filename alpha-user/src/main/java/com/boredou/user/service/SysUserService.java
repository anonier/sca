package com.boredou.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.boredou.common.entity.AuthToken;
import com.boredou.user.model.dto.NewUserDto;
import com.boredou.user.model.entity.SysLog;
import com.boredou.user.model.entity.SysUser;

import java.util.List;

public interface SysUserService extends IService<SysUser> {

    /**
     * 根据id获取用户信息
     *
     * @param id 用户id
     * @return {@link SysUser}
     */
    SysUser getUserById(int id);

    /**
     * 根据账号获取用户信息
     *
     * @param username 用户账号
     * @return {@link SysUser}
     */
    SysUser getUserByName(String username);

    /**
     * 登入
     *
     * @param username 用户名称
     * @param password 用户密码
     * @return {@link AuthToken}
     */
    AuthToken login(String username, String password);

    /**
     * 修改密码
     *
     * @param password 密码
     */
    void modifyPasswd(String password);

    /**
     * 重置密码
     */
    void resetPasswd();

    /**
     * 退出登入
     */
    void logout();

    /**
     * 新建用户
     *
     * @param dto {@link NewUserDto}
     */
    void newUser(NewUserDto dto);

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
     * 禁用用户
     *
     * @param id 用户id
     */
    void banUser(String id);

    /**
     * 最近动态
     *
     * @return {@link List<SysLog>}
     */
    List<SysLog> RecentDynamic();
}
