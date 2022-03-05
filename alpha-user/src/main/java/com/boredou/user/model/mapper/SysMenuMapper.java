package com.boredou.user.model.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boredou.user.model.entity.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    @Select("SELECT DISTINCT m.* FROM sys_menu m INNER JOIN sys_permission p INNER JOIN user_role r ON m.id=p.menu_id AND p.role_id=r.role_id AND r.user_id = #{userId}")
    List<SysMenu> getMenus(@Param("userId") String userId);
}
