package com.simplesns.service;

import com.simplesns.exception.ErrorCode;
import com.simplesns.exception.SimpleSnsApplicationException;
import com.simplesns.model.AlarmArgs;
import com.simplesns.model.AlarmNoti;
import com.simplesns.model.AlarmType;
import com.simplesns.model.entity.AlarmEntity;
import com.simplesns.model.entity.UserEntity;
import com.simplesns.repository.AlarmEntityRepository;
import com.simplesns.repository.EmitterRepository;
import com.simplesns.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

/**
 * 알림을 생성하고 전송
 * SSE 연결을 관리
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AlarmService {

    // SSE 알림의 이름 정의
    private final static String ALARM_NAME = "alarm";
    // SSE 연결의 기본 타임아웃 설정 (60분)
    private final static Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    private final AlarmEntityRepository alarmEntityRepository;
    private final EmitterRepository emitterRepository;
    private final UserEntityRepository userEntityRepository;

    /**
     * 특정 사용자에게 알림을 전송하는 메서드
     */
    public void send(AlarmType type, AlarmArgs args, Integer receiverId) {
        // 수신자의 사용자 정보 조회
        UserEntity userEntity = userEntityRepository.findById(receiverId).orElseThrow(() -> new SimpleSnsApplicationException(ErrorCode.USER_NOT_FOUND));

        // 알림 엔티티
        AlarmEntity alarmEntity = AlarmEntity.of(type, args, userEntity);
        alarmEntityRepository.save(alarmEntity);

        // 수신자 ID에 해당하는 SSE Emitter 가 있는지 확인 후 알림을 전송
        emitterRepository.get(receiverId).ifPresentOrElse(it -> {
            try {
                it.send(SseEmitter.event()
                        .id(alarmEntity.getId().toString())
                        .name(ALARM_NAME)
                        .data(new AlarmNoti()));
            } catch (IOException exception) {
                emitterRepository.delete(receiverId);
            }
        }, () -> log.info("No emitter founded"));
    }

    /**
     * SSE 연결을 초기화하고 클라이언트와의 연결을 관리
     */
    public SseEmitter connectNotification(Integer userId) {
        // 새로운 SseEmitter 객체를 생성하며 기본 타임아웃 값을 설정
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitterRepository.save(userId, emitter);

        // Emitter 연결 종료 시 Emitter 를 삭제하도록 설정
        emitter.onCompletion(() -> emitterRepository.delete(userId));
        // Emitter 타임아웃 발생 시 Emitter 를 삭제하도록 설정
        emitter.onTimeout(() -> emitterRepository.delete(userId));

        // 연결 완료 메시지를 클라이언트에게 전송
        try {
            log.info("send");
            emitter.send(SseEmitter.event()
                    .id("id")
                    .name(ALARM_NAME)
                    .data("connect completed"));
        } catch (IOException exception) {
            throw new SimpleSnsApplicationException(ErrorCode.NOTIFICATION_CONNECT_ERROR);
        }

        return emitter;
    }

}
