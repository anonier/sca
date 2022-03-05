package com.boredou.user.service.impl;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boredou.user.model.entity.SysMenu;
import com.boredou.user.model.mapper.SysMenuMapper;
import com.boredou.user.service.SysMenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RefreshScope
@DSTransactional
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Override
    public List<SysMenu> getMenus(String userId) {
        return this.baseMapper.getMenus(userId);
    }
}
