package com.simplesns.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * SSE(Single-Side Event) Emitter 저장소
 * 사용자별 SseEmitter 객체를 관리하여 실시간 알림 기능을 제공
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class EmitterRepository {

    // 사용자별 SseEmitter 를 저장하는 Map
    private final Map<String, SseEmitter> emitterMap = new HashMap<>();

    /**
     * 사용자 ID와 SseEmitter 객체를 저장
     */
    public SseEmitter save(Integer userId, SseEmitter emitter) {
        String key = getKey(userId);
        log.info("Set Emitter to Redis {}({})", key, emitter);
        emitterMap.put(key, emitter);
        return emitter;
    }

    /**
     * 사용자 ID에 해당하는 SseEmitter 객체를 삭제
     */
    public void delete(Integer userId) {
        emitterMap.remove(getKey(userId));
    }

    /**
     * 사용자 ID로 SseEmitter 객체를 조회
     */
    public Optional<SseEmitter> get(Integer userId) {
        SseEmitter emitter = emitterMap.get(getKey(userId));
        log.info("Get Emitter from Redis {}", emitter);
        return Optional.ofNullable(emitter);
    }

    /**
     * 사용자 ID를 기반으로 Emitter 키를 생성
     */
    private String getKey(Integer userId) {
        return "emitter:UID:" + userId;
    }

}
