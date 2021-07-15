package com.boredou.common.enums.enums;

import com.boredou.common.enums.ResponseBase;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yb
 * @since 2021-6-28
 */
@Getter
@AllArgsConstructor
public enum ResponseBaseUser implements ResponseBase {

    USER_DEFALTE_PASSWD(0, "boredou-1234");

    /**
     * code编码
     */
    final public int code;

    /**
     * 中文信息描述
     */
    final public String message;

}
