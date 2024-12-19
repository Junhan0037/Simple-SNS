package com.simplesns.repository;

import com.simplesns.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

/**
 * Redis 를 이용한 사용자 캐시 저장소
 * 사용자 데이터를 Redis 에 저장하고 캐시를 관리
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class UserCacheRepository {

    // RedisTemplate 을 통해 Redis 와 상호작용
    private final RedisTemplate<String, User> userRedisTemplate;

    // 사용자 캐시 TTL (Time To Live): 3일
    private final static Duration USER_CACHE_TTL = Duration.ofDays(3);

    /**
     * 사용자 데이터를 Redis 에 저장
     */
    public void setUser(User user) {
        // 사용자 이름을 기반으로 Redis 키 생성
        String key = getKey(user.getUsername());
        log.info("Set User to Redis {}({})", key, user);

        // Redis 에 사용자 데이터를 저장하고 TTL 설정
        userRedisTemplate.opsForValue().set(key, user, USER_CACHE_TTL);
    }

    /**
     * Redis 에서 사용자 데이터를 조회
     */
    public Optional<User> getUser(String username) {
        // Redis 에서 사용자 데이터 조회
        User data = userRedisTemplate.opsForValue().get(getKey(username));
        log.info("Get User from Redis {}", data);

        // Null 값도 안전하게 처리
        return Optional.ofNullable(data);
    }

    /**
     * 사용자 이름을 기반으로 Redis 키를 생성
     */
    private String getKey(String userName) {
        return "UID:" + userName;
    }

}
