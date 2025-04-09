package com.example.webapi.service;

import com.example.webapi.entity.Attendance;
import com.example.webapi.repository.AttendanceRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final ObjectMapper objectMapper;
    private final AttendanceRepository attendanceRepository;

    @Value("${spring.kafka.consumer.topic}")
    private String topic;

    @KafkaListener(topics = "${spring.kafka.consumer.topic}", groupId = "${spring.kafka.consumer.group-id}")
    @Transactional
    public void consume(String message, Acknowledgment ack) {
        log.info("Received message from topic {}: {}", topic, message);
        
        try {
            // Parse the message to Attendance object
            Attendance attendance = objectMapper.readValue(message, Attendance.class);
            log.info("Deserialized attendance object: {}", attendance);
            
            // Check for duplicate test_seq
            // Optional<Attendance> existingAttendance = attendanceRepository.findByTestSeq(attendance.getTestSeq());
            // if (existingAttendance.isPresent()) {
            //     log.info("Duplicate attendance record found for test_seq: {}", attendance.getTestSeq());
            //     ack.acknowledge(); // 중복은 성공으로 처리
            //     return;
            // }
            
            // Set check time to current time
            attendance.setCheckTime(LocalDateTime.now());
            
            // If status is not set, default to "미정"
            if (attendance.getStatus() == null) {
                attendance.setStatus("미정");
            }
            
            // Save the attendance record
            Attendance savedAttendance = attendanceRepository.save(attendance);
            log.info("Successfully saved attendance record with ID: {}", savedAttendance.getAttendanceId());
            
            // 수동 커밋
            ack.acknowledge();
            
        } catch (Exception e) {
            log.error("Error processing message: {}", message, e);
            // 예외 발생 시 커밋하지 않음 - 재처리 가능
        }
    }
} 