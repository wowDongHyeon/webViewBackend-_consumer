package com.example.webapi.service;

import com.example.webapi.entity.Attendance;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final ObjectMapper objectMapper;
    private final AttendanceService attendanceService;

    @Value("${spring.kafka.consumer.topic}")
    private String topic;

    @KafkaListener(topics = "${spring.kafka.consumer.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(String message) {
        try {
            log.info("Received message: {}", message);
            Attendance attendance = objectMapper.readValue(message, Attendance.class);
            // 여기서 필요한 비즈니스 로직을 처리할 수 있습니다.
            // 예: 데이터베이스에 저장, 다른 서비스 호출 등
            log.info("Processed attendance: {}", attendance);
        } catch (Exception e) {
            log.error("Error processing message: {}", message, e);
        }
    }
} 