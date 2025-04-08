package com.example.webapi.service;

import com.example.webapi.entity.Attendance;
import com.example.webapi.repository.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;

    @Transactional(readOnly = true)
    public Long getAttendanceCount(String lectureName, String classTime) {
        return attendanceRepository.countAttendanceByLectureAndTime(lectureName, classTime);
    }
} 