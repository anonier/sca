package com.boredou.common.entity;

import com.boredou.common.entity.base.ResponseBase;
import com.boredou.common.enums.ResponseMsgEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * response 返回 ，默认成功
 *
 * @param <T>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseVO<T> implements Serializable {

    private static final long serialVersionUID = -7985397479524277148L;

    /**
     * 返回信息定义 , 默认成功
     */
    private int code = ResponseMsgEnum.SUCCESS.getCode();
    private String msg = ResponseMsgEnum.SUCCESS.getMessage();

    /**
     * 其他相关返回的数据信息 ,data==null 不返回前端
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public ResponseVO(ResponseBase res) {
        this(res.getCode(), res.getMessage(), null);
    }

    public ResponseVO(int code, String msg) {
        this(code, msg, null);
    }

    /**
     * 成功
     */
    public ResponseVO(T data) {
        this.data = data;
    }

    public ResponseVO(ResponseBase res, T data) {
        this(res.getCode(), res.getMessage(), data);
    }

    /**
     * 成功
     *
     * @param <T>
     * @param data
     * @return
     */
    public static <T> ResponseVO<T> success(T data) {
        ResponseVO<T> res = new ResponseVO<T>();
        res.setData(data);
        return res;
    }

    @SuppressWarnings("rawtypes")
    public static ResponseVO success() {
        ResponseVO res = new ResponseVO(ResponseMsgEnum.SUCCESS);
        return res;
    }

    /**
     * 调用失败
     */
    @SuppressWarnings("rawtypes")
    public static ResponseVO fail() {
        ResponseVO res = new ResponseVO(ResponseMsgEnum.FAILED);
        return res;
    }

    @SuppressWarnings("rawtypes")
    public static ResponseVO fail(ResponseBase fail) {
        ResponseVO res = new ResponseVO(fail);
        return res;
    }

    @SuppressWarnings("rawtypes")
    public static ResponseVO fail(int code, String msg) {
        ResponseVO res = new ResponseVO(code, msg);
        return res;
    }

    /**
     * 调用异常
     */
    @SuppressWarnings("rawtypes")
    public static ResponseVO exception() {
        ResponseVO res = new ResponseVO(ResponseMsgEnum.DEFAULT_ERROR);
        return res;
    }

    /**
     * 自定义数据
     *
     * @param code 状态码
     * @param data 数据
     * @param msg  消息
     * @param <T>  T 泛型标记
     * @return R
     */
    public static <T> ResponseVO<T> data(int code, String msg, T data) {
        return new ResponseVO<>(code, msg, data);
    }

}
