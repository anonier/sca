package com.boredou.user.model.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class AuthorityDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String path;
    private String component;
    private String redirect;
    private String name;
    private AuthorityMetaDto meta;
    private List<AuthorityChildDto> children;
}
