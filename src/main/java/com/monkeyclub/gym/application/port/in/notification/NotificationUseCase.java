package com.monkeyclub.gym.application.port.in.notification;

import com.monkeyclub.gym.features.notification.NotificationLogResponse;

import java.time.LocalDate;
import java.util.List;

public interface NotificationUseCase {

    List<NotificationLogResponse> sendReminders(int daysBefore);

    List<NotificationLogResponse> logs(LocalDate from, LocalDate to);
}
