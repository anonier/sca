package com.boredou.common.entity;

import com.boredou.common.enums.ResponseBase;
import com.boredou.common.enums.ResponseMsgEnum;
import com.boredou.common.enums.exception.Exceptions;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.Optional;

/**
 * response 返回 ，默认成功
 *
 * @param <T>
 */
@ApiModel(value = "返回结果")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 默认为空消息
     */
    private final static String DEFAULT_NULL_MESSAGE = "暂无数据";
    /**
     * 默认成功消息
     */
    private final static String DEFAULT_SUCCESS_MESSAGE = "操作成功";
    /**
     * 返回信息定义 , 默认成功
     */
    @ApiModelProperty(value = "200:成功", position = 1)
    private int code = ResponseMsgEnum.SUCCESS.getCode();
    @ApiModelProperty(value = "描述信息", position = 2)
    private String msg = ResponseMsgEnum.SUCCESS.getMessage();
    @ApiModelProperty(value = "结果", position = 3)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public Response(ResponseBase res) {
        this(res.getCode(), res.getMessage(), null);
    }

    public Response(int code, String msg) {
        this(code, msg, null);
    }

    private Response(ResponseBase exceptions, String msg) {
        this(exceptions, null, msg);
    }

    private Response(ResponseBase exceptions, T data, String msg) {
        this(exceptions.getCode(), data, msg);
    }

    private Response(int code, T data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
    }


    /**
     * 成功
     */
    public Response(T data) {
        this.data = data;
    }

    public Response(ResponseBase res, T data) {
        this(res.getCode(), res.getMessage(), data);
    }

    /**
     * 调用异常
     */
    @SuppressWarnings("rawtypes")
    public static Response exception() {
        return new Response(ResponseMsgEnum.DEFAULT_ERROR);
    }

    /**
     * 返回R
     *
     * @param data 数据
     * @param <T>  T 泛型标记
     * @return R
     */
    public static <T> Response<T> data(T data) {
        return data(data, DEFAULT_SUCCESS_MESSAGE);
    }

    /**
     * 返回R
     *
     * @param data 数据
     * @param msg  消息
     * @param <T>  T 泛型标记
     * @return R
     */
    public static <T> Response<T> data(T data, String msg) {
        return data(HttpServletResponse.SC_OK, data, msg);
    }

    /**
     * 返回R
     *
     * @param code 状态码
     * @param data 数据
     * @param msg  消息
     * @param <T>  T 泛型标记
     * @return R
     */
    public static <T> Response<T> data(int code, T data, String msg) {
        return new Response<>(code, data, data == null ? DEFAULT_NULL_MESSAGE : msg);
    }

    public static <T> Response<T> success() {
        return new Response<>(Exceptions.SUCCESS);
    }

    /**
     * 成功
     *
     * @return ResponseVo
     */
    public static <T> Response<T> success(T data) {
        Response<T> res = new Response<>();
        res.setData(data);
        return res;
    }

    /**
     * 返回R
     *
     * @param msg 消息
     * @param <T> T 泛型标记
     * @return R
     */
    public static <T> Response<T> success(String msg) {
        return new Response<>(Exceptions.SUCCESS, msg);
    }

    /**
     * 判断返回是否为成功
     *
     * @param result Result
     * @return 是否成功
     */
    public static boolean isSuccess(@Nullable Response<?> result) {
        return Optional.ofNullable(result)
                .map(x -> ObjectUtils.nullSafeEquals(Exceptions.SUCCESS.code, x.code))
                .orElse(Boolean.FALSE);
    }

    /**
     * 调用失败
     */
    @SuppressWarnings("rawtypes")
    public static Response fail() {
        return new Response(ResponseMsgEnum.FAILED);
    }

    /**
     * 返回R
     *
     * @param msg 消息
     * @param <T> T 泛型标记
     * @return R
     */
    public static <T> Response<T> fail(String msg) {
        return new Response<>(Exceptions.FAILURE, msg);
    }


    /**
     * 返回R
     *
     * @param <T>  T 泛型标记
     * @param code 状态码
     * @param msg  消息
     * @return R
     */
    @SuppressWarnings("rawtypes")
    public static <T> Response fail(int code, String msg) {
        return new Response(code, (Object) null, msg);
    }

    /**
     * 返回R
     *
     * @param exceptions 业务代码
     * @param <T>        T 泛型标记
     * @return R
     */
    public static <T> Response<T> fail(Exceptions exceptions) {
        return new Response<>(exceptions);
    }

    /**
     * 返回R
     *
     * @param exceptions 业务代码
     * @param msg        消息
     * @param <T>        T 泛型标记
     * @return R
     */
    public static <T> Response<T> fail(Exceptions exceptions, String msg) {
        return new Response<>(exceptions, msg);
    }
}
