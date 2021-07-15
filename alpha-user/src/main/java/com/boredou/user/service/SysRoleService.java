package com.boredou.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.boredou.user.model.dto.EditRoleDto;
import com.boredou.user.model.dto.NewRoleDto;
import com.boredou.user.model.entity.SysRole;

public interface SysRoleService extends IService<SysRole> {

    /**
     * 新建角色
     *
     * @param dto {@link NewRoleDto}
     */
    void newRole(NewRoleDto dto);

    /**
     * 编辑角色
     *
     * @param dto {@link EditRoleDto}
     */
    void editRole(EditRoleDto dto);

    /**
     * 禁用角色
     */
    void banRole(String id);

}
