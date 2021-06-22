package com.boredou.user.exception;

import java.io.Serializable;

/**
 * @author yb
 * @since 2020-4-18 20:09
 */
public interface IException extends Serializable {

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
