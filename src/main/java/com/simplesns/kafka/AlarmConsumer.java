package com.simplesns.kafka;

import com.simplesns.model.AlarmEvent;
import com.simplesns.service.AlarmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

/**
 * Kafka 메시지를 수신하고 처리하는 역할
 *
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AlarmConsumer {

    private final AlarmService alarmService;

    /**
     * Kafka 토픽에서 메시지를 소비하는 메서드
     */
    @KafkaListener(topics = "${spring.kafka.topic.notification}")
    public void consumeNotification(AlarmEvent event, Acknowledgment ack) {
        log.info("Consume the event {}", event);

        alarmService.send(event.getType(), event.getArgs(), event.getReceiverUserId());

        // 메시지를 성공적으로 처리했음을 Kafka 브로커에 알림
        ack.acknowledge();
    }

}
