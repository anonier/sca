package com.boredou.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.boredou.common.module.entity.SysUser;
import com.boredou.user.model.dto.DingTalkBindDto;
import com.boredou.user.model.entity.DingTalkInfo;
import com.boredou.user.model.entity.DingTalkUser;
import com.boredou.user.service.DingTalkInfoService;
import com.boredou.user.service.DingTalkService;
import com.boredou.user.service.DingTalkUserService;
import com.boredou.user.util.DingTalkUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
@RefreshScope
@DSTransactional
public class DingTalkServiceImpl implements DingTalkService {

    @Resource
    private DingTalkUserService dingTalkUserService;
    @Resource
    private DingTalkInfoService dingTalkInfoService;
    @Resource
    private DingTalkUtil dingTalkUtil;


    @Override
    public DingTalkUser getDingTalkByUserName(String userName) {
        return dingTalkUserService.getOne(new LambdaQueryWrapper<DingTalkUser>().eq(DingTalkUser::getUsername, userName));
    }

    @Override
    public DingTalkInfo getDingTalkInfo(int userId) {
        return dingTalkInfoService.getOne(new LambdaQueryWrapper<DingTalkInfo>().eq(DingTalkInfo::getDUserId, userId));
    }

    @Override
    public void saveDingTalkMessage(SysUser user, DingTalkBindDto dto) {
        DingTalkUser dingTalkUser;
        String userId = ObjectUtil.isEmpty(dto) ? dingTalkUtil.getUserId(user.getPhone()) : dto.getResult().getUserid();
        dto = ObjectUtil.isEmpty(dto) ? dingTalkUtil.getUserInfo(userId) : dto;
        dingTalkUser = DingTalkUser.builder().username(dto.getResult().getName())
                .dingTalkState("1")
                .email(dto.getResult().getEmail())
                .status("1")
                .phone(dto.getResult().getMobile())
                .build();
        DingTalkUser ddUser = dingTalkUserService.getById(new LambdaQueryWrapper<DingTalkUser>().eq(DingTalkUser::getId, dingTalkInfoService.getOne(new LambdaQueryWrapper<DingTalkInfo>().eq(DingTalkInfo::getUserId, dto.getResult().getUserid())).getDUserId()));
        if (ObjectUtil.isEmpty(ddUser)) {
            dingTalkUserService.saveUser(dingTalkUser);
        } else {
            BeanUtil.copyProperties(dingTalkUser, ddUser);
            dingTalkUserService.update(ddUser);
        }
        DingTalkInfo dingTalkInfo = dingTalkInfoService.getOne(new LambdaQueryWrapper<DingTalkInfo>().
                eq(DingTalkInfo::getUserId, dto.getResult().getUserid()).
                eq(DingTalkInfo::getUnionId, dto.getResult().getUnionid()));
        if (ObjectUtil.isEmpty(dingTalkInfo)) {
            dingTalkInfoService.saveInfo(DingTalkInfo.builder()
                    .userId(userId)
                    .dUserId(dingTalkUser.getId())
                    .unionId(dto.getResult().getUnionid()).build());
        }
    }
}
