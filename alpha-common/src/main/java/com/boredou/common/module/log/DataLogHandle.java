package com.boredou.common.module.log;


import cn.hutool.core.collection.CollUtil;
import com.boredou.common.annotation.DetailLog;
import org.springframework.stereotype.Component;

/**
 * 数据日志处理
 *
 * @author yb
 * @since 2021/7/15
 */
@Component
public class DataLogHandle extends BaseDataLog {

    @Override
    public void setting() {
        // 设置排除某张表、某些字段
        this.addExcludeTableName("sys_log");
        this.addExcludeFieldName("createTime")
                .addExcludeFieldName("updateTime");
    }

    @Override
    public boolean isIgnore(DetailLog dataLog) {
        // 根据注解判断是否忽略某次操作
        return false;
    }

    @Override
    public void change(DetailLog dataLog, LogData data) {
        if (CollUtil.isEmpty(data.getDataChanges())) {
            return;
        }
        // 存库
//        System.err.println("存库成功：" + data.getLogStr());
    }

}
