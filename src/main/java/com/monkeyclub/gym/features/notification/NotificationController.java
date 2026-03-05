package com.monkeyclub.gym.features.notification;

import com.monkeyclub.gym.application.port.in.notification.NotificationUseCase;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationUseCase notificationUseCase;

    public NotificationController(NotificationUseCase notificationUseCase) {
        this.notificationUseCase = notificationUseCase;
    }

    @PostMapping("/reminders")
    public List<NotificationLogResponse> reminders(@RequestParam(defaultValue = "3") int daysBefore) {
        return notificationUseCase.sendReminders(daysBefore);
    }

    @GetMapping("/logs")
    public List<NotificationLogResponse> logs(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return notificationUseCase.logs(from, to);
    }
}
