package com.boredou.user.model.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DingTalkUserInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String boss;
    private String unionid;
    private List<DingTalkRoleList> role_list;
    private String exclusive_account;
    private String mobile;
    private String active;
    private String admin;
    private String avatar;
    private String hide_mobile;
    private String userid;
    private String senior;
    private List<DingTalkDeptOrderList> dept_order_list;
    private String real_authed;
    private String name;
    private List<String> dept_id_list;
    private String state_code;
    private String email;
    private List<DingTalkLeaderInDept> leader_in_dept;
}
