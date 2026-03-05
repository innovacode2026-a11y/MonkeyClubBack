package com.monkeyclub.gym.features.attendance;

import com.monkeyclub.gym.application.port.in.attendance.AttendanceUseCase;

import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceUseCase attendanceUseCase;

    public AttendanceController(AttendanceUseCase attendanceUseCase) {
        this.attendanceUseCase = attendanceUseCase;
    }

    @PostMapping("/check-in")
    public AttendanceResponse checkIn(@Valid @RequestBody CheckInRequest request) {
        return attendanceUseCase.checkIn(request);
    }

    @GetMapping("/history")
    public List<AttendanceResponse> history(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) UUID clientId) {
        return attendanceUseCase.history(from, to, clientId);
    }
}
