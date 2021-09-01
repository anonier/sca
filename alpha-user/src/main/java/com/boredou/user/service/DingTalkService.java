package com.boredou.user.service;

import com.boredou.common.module.entity.SysUser;
import com.boredou.user.model.dto.DingTalkBindDto;
import com.boredou.user.model.entity.DingTalkInfo;
import com.boredou.user.model.entity.DingTalkUser;

public interface DingTalkService {

    /**
     * 根据账号获取钉钉信息
     *
     * @return {@link DingTalkUser}
     */
    DingTalkUser getDingTalkByUserName(String userName);

    /**
     * 描述:根据用户id获取钉钉的详细信息
     */
    DingTalkInfo getDingTalkInfo(int userId);

    /**
     * 保存钉钉信息
     *
     * @param sysUser         {@link SysUser}
     * @param dingTalkBindDto {@link DingTalkBindDto}
     */
    void saveDingTalkMessage(SysUser sysUser, DingTalkBindDto dingTalkBindDto);
}
