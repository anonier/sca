package com.boredou.user.exception.enums;

import com.boredou.user.exception.IException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.servlet.http.HttpServletResponse;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;

/**
 * @author yb
 * @since 2020-4-18 20:10
 */
@AllArgsConstructor
@Getter
public enum Exceptions implements IException {
    /**
     * 操作成功
     */
    SUCCESS(HttpServletResponse.SC_OK, "操作成功"),

    /**
     * 业务异常
     */
    FAILURE(SC_BAD_REQUEST, "业务异常"),

    UN_LOGIN(SC_BAD_REQUEST, "未登录或登录已过期"),

    /**
     * 请求未授权
     */
    UN_AUTHORIZED(HttpServletResponse.SC_UNAUTHORIZED, "请求未授权"),

    /**
     * 请求第三方接口异常
     */
    THIRD_REQUEST_FAIL(SC_BAD_REQUEST, "请求第三方接口异常"),

    /**
     * 404 没找到请求
     */
    NOT_FOUND(HttpServletResponse.SC_NOT_FOUND, "404没找到请求"),

    /**
     * 消息不能读取
     */
    MSG_NOT_READABLE(SC_BAD_REQUEST, "消息不能读取"),

    /**
     * 不支持当前请求方法
     */
    METHOD_NOT_SUPPORTED(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "不支持当前请求方法"),

    /**
     * 不支持当前媒体类型
     */
    MEDIA_TYPE_NOT_SUPPORTED(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, "不支持当前媒体类型"),

    /**
     * 请求被拒绝
     */
    REQ_REJECT(HttpServletResponse.SC_FORBIDDEN, "请求被拒绝"),

    /**
     * 服务器异常
     */
    INTERNAL_SERVER_ERROR(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "服务器异常"),

    /**
     * 缺少必要的请求参数
     */
    PARAM_MISS(SC_BAD_REQUEST, "缺少必要的请求参数"),

    /**
     * 请求参数类型错误
     */
    PARAM_TYPE_ERROR(SC_BAD_REQUEST, "请求参数类型错误"),

    /**
     * 请求参数绑定错误
     */
    PARAM_BIND_ERROR(SC_BAD_REQUEST, "请求参数绑定错误"),

    /**
     * 参数校验失败
     */
    PARAM_VALID_ERROR(SC_BAD_REQUEST, "参数校验失败"),

    /**
     * 文件后缀不能为空
     */
    FILE_EXTENSION_IS_NULL(SC_BAD_REQUEST, "文件后缀不能为空"),

    /**
     * open office启动失败
     */
    OPEN_OFFICE_START_FAIL(SC_BAD_REQUEST, "open office启动失败");

    /**
     * code编码
     */
    final public int code;
    /**
     * 中文信息描述
     */
    final public String message;
}
