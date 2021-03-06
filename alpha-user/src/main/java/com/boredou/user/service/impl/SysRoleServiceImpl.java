package com.boredou.user.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boredou.common.enums.BizException;
import com.boredou.common.module.entity.SysUser;
import com.boredou.user.model.dto.EditRoleDto;
import com.boredou.user.model.dto.NewRoleDto;
import com.boredou.user.model.entity.SysPermission;
import com.boredou.user.model.entity.SysRole;
import com.boredou.user.model.mapper.SysRoleMapper;
import com.boredou.user.service.SysPermissionService;
import com.boredou.user.service.SysRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
@RefreshScope
@DSTransactional
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Resource
    private SysPermissionService permissionService;

    @Override
    @DS("write")
    public void newRole(NewRoleDto dto) {
        String[] ids = dto.getIds().split(",");
        SysRole role = SysRole.builder().roleName(dto.getRoleName()).status("1")
                .description(dto.getDescription())
                .company(dto.getCompany()).build();
        try {
            this.save(role);
            permissionService.newBatchPermission(role.getId(), ids);
        } catch (Exception e) {
            throw new BizException("添加角色和权限失败");
        }
    }

    @Override
    @DS("write")
    public void editRole(EditRoleDto dto) {
        String[] ids = dto.getIds().split(",");
        permissionService.remove(new LambdaQueryWrapper<SysPermission>().eq(SysPermission::getRole_id, dto.getId()));
        permissionService.newBatchPermission(dto.getId(), ids);
    }

    @Override
    @DS("write")
    public void banRole(String id) {
        SysRole role = this.getById(id);
        role.setStatus("0");
        this.updateById(role);
    }

    @Override
    public List<SysUser> getUsers(String roleId, String companyId) {
        return this.baseMapper.getUsers(roleId, companyId);
    }
}
