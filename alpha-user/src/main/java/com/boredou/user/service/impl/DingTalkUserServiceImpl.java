package com.boredou.user.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boredou.user.model.entity.DingTalkUser;
import com.boredou.user.model.mapper.DingTalkUserMapper;
import com.boredou.user.service.DingTalkUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RefreshScope
@DSTransactional
public class DingTalkUserServiceImpl extends ServiceImpl<DingTalkUserMapper, DingTalkUser> implements DingTalkUserService {

    @Override
    @DS("write")
    public void update(DingTalkUser dingTalkUser) {
        this.updateById(dingTalkUser);
    }

    @Override
    @DS("write")
    public void saveUser(DingTalkUser dingTalkUser) {
        this.save(dingTalkUser);
    }
}
