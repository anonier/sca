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
public enum ExceptionLogin implements ResponseBase {

    GET_QRCODE_TOKEN_FAIL(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "扫码获取token失败"),

    GET_CODE_TOKEN_FAIL(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "验证码获取token失败");

    /**
     * code编码
     */
    final public int code;
    /**
     * 中文信息描述
     */
    final public String message;
}
