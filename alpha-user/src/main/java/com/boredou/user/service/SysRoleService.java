package com.boredou.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.boredou.common.module.entity.SysUser;
import com.boredou.user.model.dto.EditRoleDto;
import com.boredou.user.model.dto.NewRoleDto;
import com.boredou.user.model.entity.SysRole;

import java.util.List;

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

    /**
     * 根据角色id获取对应用户列表
     *
     * @param roleId    角色id
     * @param companyId 公司id
     * @return {@link List<SysUser>}
     */
    List<SysUser> getUsers(String roleId, String companyId);
}
