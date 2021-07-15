package com.boredou.common.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 统一resopnse定义
 *
 * @since 2021-6-28
 */
@AllArgsConstructor
@Getter
public enum ResponseMsgEnum {
    /**
     * 操作成功
     */
    SUCCESS(0, "成功"),
    FAILED(1, "失败"),
    CCOS_OPERATION_ERROR_1(2, "非法请求!"),
    USER_ILLEGAL(3, "非法用户"),
    EMPTY_DATA(4, "非法用户"),
    DEFAULT_ERROR(9999, "服务调用异常");

    /**
     * code编码
     */
    private final int code;
    /**
     * 中文信息描述
     */
    private final String message;
}
