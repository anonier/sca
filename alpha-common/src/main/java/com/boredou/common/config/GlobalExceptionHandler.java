package com.boredou.common.config;

import com.boredou.common.module.entity.Response;
import com.boredou.common.enums.BizException;
import com.boredou.common.enums.exception.Exceptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.Optional;
import java.util.Set;

/**
 * 全局异常处理
 *
 * @author yb
 * @since 2021-6-28
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 未知异常的处理方法
     *
     * @param e 异常信息
     * @return ResponseVo
     */
    @ExceptionHandler(BizException.class)
    public Response bizExceptionHandler(BizException e) {
        log.error("发生异常！:", e);
        return Response.fail(e.getResponseBaseCode().getCode(), e.getMessage());
    }

    /**
     * 接口权限异常
     *
     * @param e SpelEvaluationException
     * @return Response
     */
    @ExceptionHandler(SpelEvaluationException.class)
    public Response spelEvaluationExceptionHandler(SpelEvaluationException e) {
        log.error("权限异常！:", e);
        return Response.fail(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
    }

    /**
     * 空指针异常
     *
     * @param e NullPointerException
     * @return Response
     */
    @ExceptionHandler(value = NullPointerException.class)
    public Response nullPointerExceptionHandler(NullPointerException e) {
        log.error("发生空指针异常！:", e);
        return Response.fail(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
    }

    /**
     * 入参异常捕获
     *
     * @param e Exception
     * @return Response
     */
    @ExceptionHandler({Exception.class})
    public Response exceptionHandler(Exception e) {
        log.error("异常！原因是:", e);
        return Response.fail(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public Response methodNotFound(NoHandlerFoundException e) {
        log.error("未找到接口:{} {}", e.getRequestURL(), e.getHttpMethod());
        return Response.fail(Exceptions.NOT_FOUND);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Response handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.error("缺少请求参数->paramType[{}], paramName[{}], error[{}]", e.getParameterType(), e.getParameterName(), e.getMessage());
        return Response.fail(Exceptions.PARAM_MISS);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Response handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("参数解析失败->{}", e.getMessage());
        return Response.fail(Exceptions.PARAM_MISS);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Response handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = valueValid(e);
        log.error("异常！原因是:", e);
        return Response.fail(Exceptions.PARAM_VALID_ERROR, message);
    }

    @ExceptionHandler(BindException.class)
    public Response handleBindException(BindException e) {
        String message = valueValid(e);
        log.error("异常！原因是:", e);
        return Response.fail(Exceptions.PARAM_BIND_ERROR, message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Response handleServiceException(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        ConstraintViolation<?> violation = violations.iterator().next();
        String message = violation.getMessage();
        log.error("参数验证失败->{}", message);
        return Response.fail(Exceptions.PARAM_VALID_ERROR, message);
    }

    @ExceptionHandler(ValidationException.class)
    public Response handleValidationException(ValidationException e) {
        log.error("参数验证失败->{}", e.getMessage());
        return Response.fail(Exceptions.PARAM_VALID_ERROR, e.getMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Response handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("不支持当前请求方法->{}", e.getMessage());
        return Response.fail(Exceptions.METHOD_NOT_SUPPORTED);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public Response handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        log.error("不支持当前媒体类型->{}", e.toString());
        return Response.fail(Exceptions.MEDIA_TYPE_NOT_SUPPORTED);
    }

    private String valueValid(BindException e) {
        BindingResult bingResult = e.getBindingResult();
        FieldError error = bingResult.getFieldError();
        String message = e.getMessage();
        if (Optional.ofNullable(error).isPresent()) {
            String field = error.getField();
            String code = error.getDefaultMessage();
            message = String.format("%s:%s", field, code);
        }
        log.error("参数绑定失败->{}", message);
        return message;
    }

}
