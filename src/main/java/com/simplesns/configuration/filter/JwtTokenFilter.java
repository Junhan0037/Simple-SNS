package com.simplesns.configuration.filter;

import com.simplesns.model.User;
import com.simplesns.service.UserService;
import com.simplesns.utils.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * JWT 토큰 필터 클래스
 * 요청당 한 번 실행되며, 요청의 JWT 토큰을 검증하여 사용자 인증을 처리
 */
@Slf4j
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final UserService userService;
    private final String secretKey;

    // 쿼리 파라미터로 토큰을 전달받는 URL 목록
    private final static List<String> TOKEN_IN_PARAM_URLS = List.of("/api/v1/users/alarm/subscribe");

    /**
     * 필터 내부 로직을 정의한 메서드
     * 요청의 Authorization 헤더 또는 쿼리 파라미터에서 JWT 토큰을 추출하고 검증
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        // Authorization 헤더에서 JWT 토큰 추출
        String header = request.getHeader("Authorization");
        String token;

        try {
            // 특정 URL 에서 토큰이 쿼리 파라미터로 전달되는 경우 처리
            if (TOKEN_IN_PARAM_URLS.contains(request.getRequestURI())) {
                log.info("Request with {} check the query param", request.getRequestURI());
                token = request.getQueryString().split("=")[1].trim();
            // Authorization 헤더가 없거나 Bearer 로 시작하지 않는 경우
            } else if (header == null || !header.startsWith("Bearer ")) {
                log.error("Authorization Header does not start with Bearer {}", request.getRequestURI());
                chain.doFilter(request, response);
                return;
            // Bearer 토큰이 포함된 경우 토큰을 추출
            } else {
                token = header.split(" ")[1].trim();
            }

            // 토큰에서 사용자 이름 추출
            String userName = JwtTokenUtils.getUserName(token, secretKey);

            // 사용자 이름으로 사용자 정보 로드
            User userDetails = userService.loadUserByUserName(userName);

            // 토큰 유효성 검증
            if (!JwtTokenUtils.validate(token, userDetails.getUsername(), secretKey)) {
                chain.doFilter(request, response);
                return;
            }

            // Spring Security 인증 객체 생성
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            // 요청 세부 정보를 설정
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // SecurityContext 에 인증 정보 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (RuntimeException e) {
            // 예외 발생 시 요청 필터 체인 실행 (인증 실패)
            chain.doFilter(request, response);
            return;
        }

        // 필터 체인 계속 실행
        chain.doFilter(request, response);
    }

}
