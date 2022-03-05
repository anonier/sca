package com.boredou.user.model.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boredou.common.module.entity.SysUser;
import com.boredou.user.model.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {

    /**
     * 根据角色id和公司id获取对应用户列表
     *
     * @param roleId    角色id
     * @param companyId 公司id
     * @return {@link List<SysUser>}
     */
    @Select("SELECT * FROM sys_user u INNER JOIN user_role r ON u.id = r.user_id WHERE r.role_id = #{roleId} AND u.company = #{companyId}")
    List<SysUser> getUsers(@Param("roleId") String roleId, @Param("companyId") String companyId);
}
