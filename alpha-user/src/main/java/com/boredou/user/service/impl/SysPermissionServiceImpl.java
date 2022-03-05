package com.boredou.user.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boredou.user.model.entity.SysPermission;
import com.boredou.user.model.mapper.SysPermissionMapper;
import com.boredou.user.service.SysPermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RefreshScope
@DSTransactional
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission> implements SysPermissionService {

    @Override
    @DS("write")
    public void newBatchPermission(String id, String[] ids) {
        List<SysPermission> permissions = new ArrayList<>();
        for (String i : ids) {
            SysPermission permission = SysPermission.builder().role_id(id).menu_id(i).build();
            permissions.add(permission);
        }
        this.saveBatch(permissions);
    }

    @Override
    @DS("read")
    public List<SysPermission> getPermission(String id) {
        return this.list(new LambdaQueryWrapper<SysPermission>().eq(SysPermission::getRole_id, id));
    }
}
