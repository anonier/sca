package com.boredou.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.boredou.user.model.entity.SysMenu;

import java.util.List;

public interface SysMenuService extends IService<SysMenu> {

    /**
     * 根据用户id查询所有菜单和权限
     *
     * @param userId 用户id
     * @return {@link List<SysMenu>}
     */
    List<SysMenu> getMenus(String userId);
}
