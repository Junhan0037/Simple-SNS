package com.simplesns.repository;

import com.simplesns.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserCacheRepository {

    private final RedisTemplate<String, User> userRedisTemplate;

    private final static Duration USER_CACHE_TTL = Duration.ofDays(3);

    public void setUser(User user) {
        String key = getKey(user.getUsername());
        log.info("Set User to Redis {}({})", key, user);
        userRedisTemplate.opsForValue().set(key, user, USER_CACHE_TTL);
    }

    public Optional<User> getUser(String username) {
        User data = userRedisTemplate.opsForValue().get(getKey(username));
        log.info("Get User from Redis {}", data);
        return Optional.ofNullable(data);
    }

    private String getKey(String userName) {
        return "UID:" + userName;
    }

}