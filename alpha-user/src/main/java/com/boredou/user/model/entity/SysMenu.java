package com.boredou.user.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
public class SysMenu implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private String id;
    /**
     * 组件
     */
    private String component;
    /**
     * 父菜单ID
     */
    private String pId;
    /**
     * 名称
     */
    private String name;
    /**
     * 标题
     */
    private String title;
    /**
     * 请求地址
     */
    private String url;
    /**
     * 是否是菜单
     */
    private String isMenu;
    /**
     * 菜单层级
     */
    private String level;
    /**
     * 菜单排序
     */
    private String sort;
    /**
     * 图标
     */
    private String icon;
    /**
     * 状态
     */
    private String status;

    private Date createTime;
    private Date updateTime;
}
