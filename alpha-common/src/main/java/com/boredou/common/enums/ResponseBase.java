package com.boredou.common.enums;

import java.io.Serializable;

/**
 * @author yb
 * @since 2021-6-28
 */
public interface ResponseBase extends Serializable {

    /**
     * 消息
     *
     * @return String
     */
    String getMessage();

    /**
     * 状态码
     *
     * @return int
     */
    int getCode();

}
