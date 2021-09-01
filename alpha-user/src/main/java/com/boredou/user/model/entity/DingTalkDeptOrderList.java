package com.boredou.user.model.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class DingTalkDeptOrderList implements Serializable {
    private static final long serialVersionUID = 1L;

    private String dept_id;
    private String order;
}
