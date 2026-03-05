package com.monkeyclub.gym.features.attendance;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, UUID> {

    List<AttendanceRecord> findByCheckInAtBetweenOrderByCheckInAtDesc(OffsetDateTime from, OffsetDateTime to);

    List<AttendanceRecord> findByClientIdAndCheckInAtBetweenOrderByCheckInAtDesc(UUID clientId, OffsetDateTime from, OffsetDateTime to);
}
