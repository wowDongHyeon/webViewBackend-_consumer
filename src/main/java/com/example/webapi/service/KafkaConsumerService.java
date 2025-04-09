package com.example.webapi.service;

import com.example.webapi.entity.Attendance;
import com.example.webapi.repository.AttendanceRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
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
    public void consume(String message) {
        log.info("Received message from topic {}: {}", topic, message);
        
        try {
            // Parse the message to Attendance object
            Attendance attendance = objectMapper.readValue(message, Attendance.class);
            log.info("Deserialized attendance object: {}", attendance);
            
            // Check for duplicate test_seq
            Optional<Attendance> existingAttendance = attendanceRepository.findByTestSeq(attendance.getTestSeq());
            if (existingAttendance.isPresent()) {
                log.info("Duplicate attendance record found for test_seq: {}", attendance.getTestSeq());
                return;
            }
            
            // Set check time to current time
            attendance.setCheckTime(LocalDateTime.now());
            
            // If status is not set, default to "미정"
            if (attendance.getStatus() == null) {
                attendance.setStatus("미정");
            }
            
            // Save the attendance record
            Attendance savedAttendance = attendanceRepository.save(attendance);
            log.info("Successfully saved attendance record with ID: {}", savedAttendance.getAttendanceId());
            
        } catch (Exception e) {
            log.error("Error processing message: {}", message, e);
            // Don't throw the exception to prevent Kafka from retrying
            // This way, we can continue processing other messages
        }
    }
} 