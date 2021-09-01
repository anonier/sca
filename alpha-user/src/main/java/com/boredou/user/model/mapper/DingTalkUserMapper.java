package com.boredou.user.model.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boredou.user.model.entity.DingTalkUser;
import com.boredou.user.model.entity.SysLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DingTalkUserMapper extends BaseMapper<DingTalkUser> {
}
