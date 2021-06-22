package com.boredou.user.exception;

import com.boredou.user.exception.enums.Exceptions;
import lombok.Getter;

public class BusiException extends RuntimeException {
    private static final long serialVersionUID = 2359767895161832954L;

    @Getter
    private final IException resultCode;

    public BusiException(String message) {
        super(message);
        this.resultCode = Exceptions.FAILURE;
    }

    public BusiException(IException resultCode) {
        super(resultCode.getMessage());
        this.resultCode = resultCode;
    }

    public BusiException(IException resultCode, Throwable cause) {
        super(cause);
        this.resultCode = resultCode;
    }
}
