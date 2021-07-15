package com.boredou.common.enums.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yb
 * @since 2021-6-28
 */
@Getter
@AllArgsConstructor
public enum EnumRedis {

    USER_TOKEN("user_token:");

    /**
     * 字段
     */
    private final String desc;

}
