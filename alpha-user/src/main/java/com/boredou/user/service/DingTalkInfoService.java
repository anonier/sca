package com.boredou.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.boredou.user.model.entity.DingTalkInfo;

public interface DingTalkInfoService extends IService<DingTalkInfo> {

    /**
     * 保存DingTalkInfo
     *
     * @param dingTalkInfo {@link DingTalkInfo}
     */
    void saveInfo(DingTalkInfo dingTalkInfo);
}
