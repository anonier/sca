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
public enum ExceptionCookie implements ResponseBase {

    SAVE_FAIL(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "cookie保存失败"),

    CLEAR_FAIL(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "cookie清除失败");

    /**
     * code编码
     */
    final public int code;
    /**
     * 中文信息描述
     */
    final public String message;
}
