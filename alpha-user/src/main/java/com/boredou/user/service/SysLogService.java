package com.boredou.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.boredou.user.model.dto.RecentDynamicDto;
import com.boredou.user.model.entity.SysLog;

import java.util.List;

public interface SysLogService extends IService<SysLog> {

    /**
     * 根据用户账号获取log信息
     *
     * @param recentDynamicDto {@link RecentDynamicDto}
     * @return {@link List<SysLog>}
     */
    IPage<SysLog> getLogs(RecentDynamicDto recentDynamicDto);

    /**
     * 保存log
     *
     * @param sysLog {@link SysLog}
     */
    void saveLog(SysLog sysLog);
}
