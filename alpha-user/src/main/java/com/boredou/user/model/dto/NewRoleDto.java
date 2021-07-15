package com.boredou.user.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class NewRoleDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String roleName;
    private String ids;

}
