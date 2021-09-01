package com.boredou.user.model.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class DingTalkRoleList implements Serializable {
    private static final long serialVersionUID = 1L;

    private String group_name;
    private String name;
    private String id;
}
