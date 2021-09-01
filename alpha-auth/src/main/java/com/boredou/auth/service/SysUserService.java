package com.boredou.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.boredou.common.module.entity.SysUser;

import java.util.List;

public interface SysUserService extends IService<SysUser> {

    /**
     * 根据用户名获取用户信息
     *
     * @param username 用户名称
     * @return {@link SysUser}
     */
    SysUser getSysUserByUsername(String username);

    /**
     * 根据用户名获取用户信息
     *
     * @param phone 用户手机
     * @return {@link SysUser}
     */
    SysUser getSysUserByPhone(String phone);

    /**
     * 根据用户id获取用户权限
     *
     * @param id 用户id
     * @return List
     */
    List<String> getSysUserPermission(String id);
}
