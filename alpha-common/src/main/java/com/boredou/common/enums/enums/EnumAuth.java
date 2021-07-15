package com.boredou.common.enums.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yb
 * @since 2021-6-28
 */
@Getter
@AllArgsConstructor
public enum EnumAuth {

    PASSWD("password"),

    USERNAME("username"),

    ACCESS_TOKEN("access_token"),

    REFRESH_TOKEN("refresh_token"),

    JTI("jti"),

    GRANT_TYPE("grant_type");

    /**
     * 字段
     */
    private final String desc;

}
