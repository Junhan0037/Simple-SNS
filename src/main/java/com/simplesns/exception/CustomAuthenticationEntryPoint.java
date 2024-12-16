package com.simplesns.exception;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint { // // Spring Security 에서 인증 실패 시 호출되는 EntryPoint 를 정의

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException { // 인증 실패 시 호출되는 메서드로, 클라이언트에 에러 응답을 보냄
        response.setContentType("application/json"); // 응답의 콘텐츠 타입을 JSON 으로 설정
        response.setStatus(ErrorCode.INVALID_TOKEN.getStatus().value()); // 응답 상태 코드를 에러 코드에서 정의된 상태 코드로 설정
        response.getWriter().print(ErrorCode.INVALID_TOKEN.getMessage()); // 응답 본문에 에러 메시지를 작성
    }

}
