package com.boredou.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
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
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Slf4j
@Service
@RefreshScope
@Transactional
public class DingTalkServiceImpl implements DingTalkService {

    @Resource
    DingTalkUserService dingTalkUserService;
    @Resource
    DingTalkInfoService dingTalkInfoService;
    @Resource
    DingTalkUtil dingTalkUtil;


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
        String userId;
        DingTalkUser dingTalkUser;
        if (ObjectUtil.isEmpty(dto)) {
            userId = dingTalkUtil.getUserId(user.getPhone());
            dto = dingTalkUtil.getUserInfo(userId);
        } else {
            userId = dto.getResult().getUserid();
        }
        dingTalkUser = DingTalkUser.builder().username(dto.getResult().getName())
                .dingTalkState("1")
                .email(dto.getResult().getEmail())
                .status("1")
                .phone(dto.getResult().getMobile())
                .build();
        DingTalkUser ddUser = dingTalkUserService.getById(new LambdaQueryWrapper<DingTalkUser>().eq(DingTalkUser::getId, dingTalkInfoService.getOne(new LambdaQueryWrapper<DingTalkInfo>().eq(DingTalkInfo::getUserId, dto.getResult().getUserid())).getDUserId()));
        if (ObjectUtil.isEmpty(ddUser)) {
            dingTalkUserService.save(dingTalkUser);
        } else {
            BeanUtil.copyProperties(dingTalkUser, ddUser);
            dingTalkUserService.updateById(ddUser);
        }
        DingTalkInfo dingTalkInfo = dingTalkInfoService.getOne(new LambdaQueryWrapper<DingTalkInfo>().
                eq(DingTalkInfo::getUserId, dto.getResult().getUserid()).
                eq(DingTalkInfo::getUnionId, dto.getResult().getUnionid()));
        if (ObjectUtil.isEmpty(dingTalkInfo)) {
            dingTalkInfoService.save(DingTalkInfo.builder()
                    .userId(userId)
                    .dUserId(dingTalkUser.getId())
                    .unionId(dto.getResult().getUnionid()).build());
        }
    }
}
