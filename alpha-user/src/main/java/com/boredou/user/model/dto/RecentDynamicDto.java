package com.boredou.user.model.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class RecentDynamicDto extends Page implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;

    private Date startDate;

    private Date finishDate;

    private String module;
}
