package com.example.webapi.controller;

import com.example.webapi.dto.AttendanceCountRequest;
import com.example.webapi.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/count")
    public ResponseEntity<Long> getAttendanceCount(@RequestBody AttendanceCountRequest request) {
        Long count = attendanceService.getAttendanceCount(request.getLectureName(), request.getClassTime());
        return ResponseEntity.ok(count);
    }
} 