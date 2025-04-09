package com.example.webapi.repository;

import com.example.webapi.entity.TempAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TempAttendanceRepository extends JpaRepository<TempAttendance, Long> {
    Optional<TempAttendance> findByTestSeq(Long testSeq);
} 