package com.monkeyclub.gym.features.attendance;

import com.monkeyclub.gym.features.client.Client;
import com.monkeyclub.gym.common.BaseEntity;
import com.monkeyclub.gym.features.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "attendance_records")
public class AttendanceRecord extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "registered_by")
    private User registeredBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendanceMethod method;

    @Column(nullable = false)
    private boolean accessGranted;

    @Column(nullable = false, length = 500)
    private String message;

    @Column(nullable = false)
    private OffsetDateTime checkInAt;
}
