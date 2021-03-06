package com.boredou.user.model.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class EditRoleDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String ids;
}
