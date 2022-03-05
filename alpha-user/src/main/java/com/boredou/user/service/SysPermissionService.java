package com.boredou.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.boredou.user.model.entity.SysPermission;

import java.util.List;

public interface SysPermissionService extends IService<SysPermission> {

    /**
     * 根据角色id批量添加权限
     *
     * @param id  角色id
     * @param ids 菜单权限id
     */
    void newBatchPermission(String id, String[] ids);

    /**
     * 根据角色id获取权限信息
     *
     * @param id 角色id
     */
    List<SysPermission> getPermission(String id);
}