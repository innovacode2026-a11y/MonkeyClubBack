package com.monkeyclub.gym.notification;

import com.monkeyclub.gym.client.Client;
import com.monkeyclub.gym.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "notification_logs")
public class NotificationLog extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id")
    private Client client;

    @Column(nullable = false)
    private Integer daysBeforeExpiry;

    @Column(nullable = false, length = 1000)
    private String message;

    @Column(nullable = false)
    private String channel;
}
