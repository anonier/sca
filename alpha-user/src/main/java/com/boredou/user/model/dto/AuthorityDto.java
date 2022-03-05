package com.boredou.user.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthorityDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String path;
    private String component;
    private String name;
    private boolean hidden;
    private AuthorityMetaDto meta;
    private List<AuthorityDto> children;
}
