package com.boredou.common.entity.base;

import java.io.Serializable;

public interface ResponseBase extends Serializable {

    /**
     * 消息
     *
     * @return String
     */
    String getMessage();

    /**
     * 错误状态码
     *
     * @return int
     */
    int getCode();
}
