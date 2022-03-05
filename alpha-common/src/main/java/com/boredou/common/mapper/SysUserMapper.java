package com.boredou.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boredou.common.module.entity.SysUser;
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
    @Select("SELECT CONCAT_WS(':',m.`code`,m.url,m.is_menu) FROM sys_menu m INNER JOIN sys_permission p INNER JOIN sys_user_role r INNER JOIN sys_role o ON m.id = p.menu_id AND p.role_id = r.role_id = o.id WHERE r.user_id = #{id}")
    List<String> getSysUserPermission(@Param("id") String id);

    /**
     * 根绝公司id获取用户列表
     *
     * @param id 公司id
     * @return {@link List<SysUser>}
     */
    @Select("SELECT u.*,o.description FROM sys_user u INNER JOIN user_role r INNER JOIN sys_role o ON u.id = r.user_id AND r.role_id = o.id WHERE u.company = #{id}")
    List<SysUser> getUsers(@Param("id") String id);
}
