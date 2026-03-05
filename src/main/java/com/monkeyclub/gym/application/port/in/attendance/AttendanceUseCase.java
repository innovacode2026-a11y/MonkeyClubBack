package com.monkeyclub.gym.application.port.in.attendance;

import com.monkeyclub.gym.features.attendance.AttendanceResponse;
import com.monkeyclub.gym.features.attendance.CheckInRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface AttendanceUseCase {

    AttendanceResponse checkIn(CheckInRequest request);

    List<AttendanceResponse> history(LocalDate from, LocalDate to, UUID clientId);
}
