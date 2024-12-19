package com.simplesns.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

/**
 * JWT 토큰 생성 및 검증 유틸리티 클래스
 * 토큰 생성, 파싱, 유효검 검증 등을 처리
 */
public class JwtTokenUtils {

    /**
     * 토큰이 유효한지 검증
     * 1. 토큰에서 추출한 사용자 이름과 제공된 사용자 이름이 동일한지 확인
     * 2. 토큰이 만료되지 않았는지 확인
     */
    public static Boolean validate(String token, String userName, String key) {
        String userNameByToken = getUserName(token, key);
        return userNameByToken.equals(userName) && !isTokenExpired(token, key);
    }

    /**
     * 토큰에서 모든 클레임 추출
     */
    public static Claims extractAllClaims(String token, String key) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey(key)) // 서명 검증을 위한 키 설정
                .build()
                .parseClaimsJws(token) // 토큰 파싱 및 검증
                .getBody(); // 클레임 반환
    }

    /**
     * 토큰에서 사용자 이름을 추출
     */
    public static String getUserName(String token, String key) {
        return extractAllClaims(token, key).get("username", String.class);
    }

    /**
     * 토큰의 만료 여부를 확인
     */
    public static Boolean isTokenExpired(String token, String key) {
        Date expiration = extractAllClaims(token, key).getExpiration();

        // 만료 시간이 현재 시간 이전인지 확인
        return expiration.before(new Date());
    }

    /**
     * 사용자 이름과 만료 시간을 기반으로 JWT 액세스 토큰을 생성
     */
    public static String generateAccessToken(String userName, String key, long expiredTimeMs) {
        return doGenerateToken(userName, expiredTimeMs, key);
    }

    /**
     * 비밀키를 기반으로 HMAC-SHA 키 객체를 생성합니다.
     */
    private static Key getSigningKey(String secretKey) {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * JWT 토큰을 생성
     */
    private static String doGenerateToken(String username, long expireTime, String key) {
        Claims claims = Jwts.claims(); // 클레임 생성
        claims.put("username", username); // 사용자 이름 추가

        return Jwts.builder()
                .setClaims(claims) // 클레임 설정
                .setIssuedAt(new Date(System.currentTimeMillis())) // 발급 시간 설정
                .setExpiration(new Date(System.currentTimeMillis() + expireTime)) // 만료 시간 설정
                .signWith(getSigningKey(key), SignatureAlgorithm.HS256) // 서명 및 알고리즘 생성
                .compact(); // JWT 토큰 생성
    }

}
