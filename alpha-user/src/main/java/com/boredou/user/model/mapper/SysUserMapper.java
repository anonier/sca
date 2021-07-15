package com.boredou.user.model.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boredou.user.model.entity.SysUser;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
}
