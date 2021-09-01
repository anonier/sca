package com.boredou.user.model.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * 钉钉用户
 */
@Data
@Builder
public class DingTalkUser {
    private static final long serialVersionUID = 4204215988136317808L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 角色ID
     */
    private Integer roleId;

    /**
     * 登录名（匿名）
     */
    private String username;

    /**
     * 登录密码
     */
    private String password;

    /**
     * 邮件
     */
    private String email;

    /**
     * 状态
     */
    private String status;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 微信状态
     */
    private Integer wechatState;

    /**
     * 钉钉状态
     */
    private String dingTalkState;

    private Date createTime;
    private Date updateTime;
}
