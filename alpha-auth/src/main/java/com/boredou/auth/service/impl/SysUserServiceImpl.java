package com.boredou.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boredou.auth.service.SysUserService;
import com.boredou.common.module.entity.SysUser;
import com.boredou.common.mapper.SysUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
@RefreshScope
@Transactional(rollbackFor = Exception.class)
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Resource
    SysUserMapper sysUserMapper;

    @Override
    public SysUser getSysUserByUsername(String username) {
        return this.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
    }

    @Override
    public SysUser getSysUserByPhone(String phone) {
        return this.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getPhone, phone));
    }

    @Override
    public List<String> getSysUserPermission(String id) {
        return sysUserMapper.getSysUserPermission(id);
    }
}
