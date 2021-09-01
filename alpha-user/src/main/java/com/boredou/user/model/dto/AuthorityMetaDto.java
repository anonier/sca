package com.boredou.user.model.dto;

import com.boredou.user.model.entity.SysMenu;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class AuthorityMetaDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String title;
    private String icon;
    private List<SysMenu> authorities;
}
