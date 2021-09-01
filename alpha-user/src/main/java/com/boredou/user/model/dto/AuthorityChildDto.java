package com.boredou.user.model.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class AuthorityChildDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String path;
    private String name;
    private String component;
    private AuthorityMetaDto meta;
}
