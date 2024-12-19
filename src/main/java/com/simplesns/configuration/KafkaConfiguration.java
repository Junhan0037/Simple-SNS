package com.simplesns.configuration;

import com.simplesns.model.AlarmEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka 설정 클래스
 * Kafka 브로커와 연결하고, 메시지를 직렬화/역직렬화하며, 프로듀서를 구성
 */
@Configuration
public class KafkaConfiguration {

    // Kafka 설정 정보 (application.yml 에서 주입)
    @Value("${spring.kafka.producer.bootstrap-servers}")
    private String bootstrapServers;

    /**
     * KafkaTemplate 은 Kafka 메시지를 전송할 때 사용
     * AlarmEvent 객체를 Value 로 사용하는 Kafka 메시지를 전송하는 데 사용
     */
    @Bean
    public KafkaTemplate<String, AlarmEvent> alarmEventKafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    /**
     * ProducerFactory 는 Kafka Producer 인스턴스를 생성하는 데 사용
     * 이 팩토리를 통해 KafkaTemplate 에 필요한 프로듀서를 구성하고 제공
     */
    @Bean
    public ProducerFactory<String, AlarmEvent> producerFactory() {
        // Kafka Producer 설정을 저장할 Map 을 생성
        Map<String, Object> configs = new HashMap<>();

        // Kafka 브로커의 주소를 설정 (프로듀서가 메시지를 전송할 브로커를 식별하는 데 사용)
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        // Kafka 메시지의 Key 를 직렬화하기 위한 Serializer 클래스를 설정 (StringSerializer 는 Key 를 문자열 형태로 직렬화)
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        // Kafka 메시지의 Value 를 직렬화하기 위한 Serializer 클래스를 설정 (JsonSerializer 는 Value 를 JSON 형태로 직렬화하여 Kafka 메시지로 전송)
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        // 설정을 기반으로 DefaultKafkaProducerFactory 객체를 생성하여 반환
        return new DefaultKafkaProducerFactory<>(configs);
    }

}
