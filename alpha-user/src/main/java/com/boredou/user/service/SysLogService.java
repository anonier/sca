package com.boredou.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.boredou.user.model.entity.SysLog;

import java.util.List;

public interface SysLogService extends IService<SysLog> {

    /**
     * 根据用户账号获取log信息
     *
     * @param name 用户账号
     * @return {@link List<SysLog>}
     */
    List<SysLog> getLogsByName(String name);
}
