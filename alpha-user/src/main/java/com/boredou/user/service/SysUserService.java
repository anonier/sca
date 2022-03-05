package com.boredou.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.boredou.common.module.entity.SysUser;
import com.boredou.user.model.dto.AuthorityDto;
import com.boredou.user.model.dto.UserDto;
import com.boredou.user.model.result.LoginResult;

import java.util.List;
import java.util.Map;

public interface SysUserService extends IService<SysUser> {

    /**
     * 根据账号获取用户信息
     *
     * @param username 用户账号
     * @return {@link SysUser}
     */
    SysUser getUserByName(String username);

    /**
     * 根据用户id更新用户信息
     *
     * @param sysUser {@link SysUser}
     */
    void update(SysUser sysUser);

    /**
     * 根据账号获取用户权限
     *
     * @param username 用户账号
     * @return {@link List<AuthorityDto>}
     */
    List<AuthorityDto> getPermissions(String username);

    /**
     * 登入
     *
     * @param username 用户名称
     * @param password 用户密码
     * @return {@link LoginResult}
     */
    LoginResult login(String type, String username, String password, String code);

    /**
     * 通过手机账号绑定钉钉
     *
     * @param username 用户名称
     */
    void bindDingTalkByPhone(String username);

    /**
     * 通过扫码绑定钉钉
     *
     * @param code 二维码Code
     */
    void bindDingTalkByQrcode(String code);

    /**
     * 发送钉钉验证码
     *
     * @param username 账号
     * @return 验证码
     */
    int sendDingTalkCode(String username);

    /**
     * 修改密码
     *
     * @param password 密码
     */
    void modifyPasswd(String password);

    /**
     * 重置密码
     *
     * @param username 用户名
     */
    void resetPasswd(String username);

    /**
     * 退出登入
     */
    void logout();

    /**
     * 新建用户
     *
     * @param dto {@link UserDto}
     */
    void newUser(UserDto dto);

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
     * 编辑用户
     *
     * @param dto {@link UserDto}
     */
    void editUser(UserDto dto);

    /**
     * 禁用用户
     *
     * @param id 用户id
     */
    void banUser(String id);

    /**
     * 权限管理界面
     *
     * @param id 公司id
     * @return {@link Map}
     */
    List authorityManage(String id);

    /**
     * 用户管理界面
     *
     * @param id 公司id
     * @return {@link List<SysUser>}
     */
    List<SysUser> memberManage(String id);
}
