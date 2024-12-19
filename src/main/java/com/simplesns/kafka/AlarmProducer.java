package com.simplesns.kafka;

import com.simplesns.model.AlarmEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Kafka 를 사용하여 알람 이벤트를 프로듀싱(송신)하는 클래스
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AlarmProducer {

    private final KafkaTemplate<Integer, AlarmEvent> alarmEventKafkaTemplate;

    @Value("${spring.kafka.topic.notification}")
    private String topic;

    public void send(AlarmEvent event) {
        // 지정된 토픽에 이벤트를 전송
        alarmEventKafkaTemplate.send(topic, event.getReceiverUserId(), event);
        log.info("send fin");
    }

}
