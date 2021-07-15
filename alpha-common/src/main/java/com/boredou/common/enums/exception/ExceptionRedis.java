package com.boredou.common.enums.exception;

import com.boredou.common.enums.ResponseBase;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.servlet.http.HttpServletResponse;

/**
 * @author yb
 * @since 2021-6-28
 */
@Getter
@AllArgsConstructor
public enum ExceptionRedis implements ResponseBase {

    SAVE_FAIL(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "redis保存失败"),

    DEL_FAIL(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "redis删除失败");

    /**
     * code编码
     */
    final public int code;
    /**
     * 中文信息描述
     */
    final public String message;
}
