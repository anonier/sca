package com.boredou.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boredou.auth.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 根据用户id获取权限信息
     *
     * @param id 用户id
     * @return List<String>
     */
    @Select("SELECT CONCAT_WS(':',m.`code`,m.url) FROM sys_menu m INNER JOIN sys_permission p INNER JOIN sys_user_role r INNER JOIN sys_role o ON m.id = p.menu_id and p.role_id = r.role_id = o.id WHERE r.user_id = #{id} ")
    List<String> getSysUserPermission(@Param("id") String id);
}
