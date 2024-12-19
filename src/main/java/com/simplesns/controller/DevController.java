package com.simplesns.controller;

import com.simplesns.kafka.AlarmProducer;
import com.simplesns.model.AlarmArgs;
import com.simplesns.model.AlarmEvent;
import com.simplesns.model.AlarmType;
import com.simplesns.model.entity.UserEntity;
import com.simplesns.repository.UserEntityRepository;
import com.simplesns.service.AlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api-dev/v1")
@RequiredArgsConstructor
public class DevController {

    private final AlarmService alarmService;
    private final UserEntityRepository userEntityRepository;
    private final AlarmProducer alarmProducer;

    @GetMapping("/notification")
    public void notification() {
        UserEntity userEntity = userEntityRepository.findById(5).orElseThrow();
        alarmService.send(AlarmType.NEW_LIKE_ON_POST, new AlarmArgs(0, 0), userEntity.getId());
    }

    @GetMapping("/send")
    public void send() {
        alarmProducer.send(new AlarmEvent(AlarmType.NEW_LIKE_ON_POST, new AlarmArgs(0, 0), 5));
    }

}
