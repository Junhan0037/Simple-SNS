package com.simplesns.configuration;

import com.simplesns.model.User;
import io.lettuce.core.RedisURI;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 설정 클래스
 * Redis 연결 설정 및 사용자 정의 RedisTemplate 구성
 */
@Configuration
@EnableRedisRepositories
@RequiredArgsConstructor
public class RedisConfiguration {

    // Redis 설정 정보 (application.yml 에서 주입)
    private final RedisProperties redisProperties;

    /**
     * 사용자 데이터를 처리하기 위한 RedisTemplate Bean 생성
     */
    @Bean
    public RedisTemplate<String, User> userRedisTemplate() {
        // RedisTemplate 객체 생성
        RedisTemplate<String, User> redisTemplate = new RedisTemplate<>();

        // Redis 연결 설정
        redisTemplate.setConnectionFactory(redisConnectionFactory());

        // 키 직렬화 설정 (String 타입)
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        // 값 직렬화 설정 (JSON 형태로 User 클래스)
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(User.class));

        return redisTemplate;
    }

    /**
     * Redis 서버 연결 관리
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        // Redis URI 를 기반으로 연결 구성 생성
        RedisURI redisURI = RedisURI.create(redisProperties.getUrl());
        org.springframework.data.redis.connection.RedisConfiguration configuration = LettuceConnectionFactory.createRedisConfiguration(redisURI);

        // LettuceConnectionFactory 로 Redis 연결 팩토리 생성
        LettuceConnectionFactory factory = new LettuceConnectionFactory(configuration);

        // 설정 완료 후 초기화
        factory.afterPropertiesSet();

        return factory;
    }

}
