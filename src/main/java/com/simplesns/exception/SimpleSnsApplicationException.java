package com.simplesns.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SimpleSnsApplicationException extends RuntimeException { // 사용자 정의 예외 클래스 정의 (RuntimeException 을 상속받음)

    private ErrorCode errorCode;
    private String message;

    public SimpleSnsApplicationException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.message = null;
    }

    @Override
    public String getLocalizedMessage() { // 런타임 예외가 사용자에게 출력될 때 호출
        if (message == null) {
            return errorCode.getMessage();
        } else {
            return String.format("%s (%s)", errorCode.getMessage(), message);
        }
    }

}
