package com.boredou.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.boredou.user.model.entity.DingTalkUser;

public interface DingTalkUserService extends IService<DingTalkUser> {

    /**
     * 根据id更新钉钉用户信息
     *
     * @param dingTalkUser {@link DingTalkUser}
     */
    void update(DingTalkUser dingTalkUser);

    /**
     * 保存钉钉用户信息
     *
     * @param dingTalkUser {@link DingTalkUser}
     */
    void saveUser(DingTalkUser dingTalkUser);
}
