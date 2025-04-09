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

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final ObjectMapper objectMapper;
    private final AttendanceRepository attendanceRepository;

    @Value("${spring.kafka.consumer.topic}")
    private String topic;

    @KafkaListener(topics = "${spring.kafka.consumer.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(String message, Acknowledgment ack) {
        String messageId = null;
        try {
            // Parse the message to Attendance object
            Attendance attendance = objectMapper.readValue(message, Attendance.class);
            messageId = attendance.getUuid();
            log.info("Processing message with UUID: {}", messageId);
            
            // Process the message
            processMessage(attendance);
            
            // Acknowledge the message
            ack.acknowledge();
            log.info("Successfully processed message with UUID: {}", messageId);
            
        } catch (Exception e) {
            log.error("Error processing message {}: {}", messageId, message, e);
            // Don't acknowledge - message will be reprocessed
        }
    }

    @Transactional
    protected void processMessage(Attendance attendance) {
        // Check if attendance already exists
        if (attendanceRepository.existsById(attendance.getUuid())) {
            log.info("Attendance record already exists for UUID: {}", attendance.getUuid());
            return;
        }
        
        // Set check time to current time if not set
        if (attendance.getCheckTime() == null) {
            attendance.setCheckTime(LocalDateTime.now());
        }
        
        // If status is not set, default to "미정"
        if (attendance.getStatus() == null) {
            attendance.setStatus("미정");
        }
        
        // Save the attendance record
        attendanceRepository.save(attendance);
        log.info("Saved new attendance record with UUID: {}", attendance.getUuid());
    }
} 