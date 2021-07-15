package com.boredou.common.enums;

import com.boredou.common.enums.exception.Exceptions;
import lombok.Getter;

/**
 * @author yb
 * @since 2021-6-28
 */
public class BizException extends RuntimeException {
    private static final long serialVersionUID = 2359767895161832954L;

    @Getter
    private final ResponseBase responseBaseCode;

    public BizException(String message) {
        super(message);
        this.responseBaseCode = Exceptions.FAILURE;
    }

    public BizException(ResponseBase responseBaseCode) {
        super(responseBaseCode.getMessage());
        this.responseBaseCode = responseBaseCode;
    }

    public BizException(ResponseBase responseBaseCode, Throwable cause) {
        super(cause);
        this.responseBaseCode = responseBaseCode;
    }

}
