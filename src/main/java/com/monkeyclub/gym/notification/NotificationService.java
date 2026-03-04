package com.monkeyclub.gym.notification;

import com.monkeyclub.gym.membership.Membership;
import com.monkeyclub.gym.membership.MembershipRepository;
import com.monkeyclub.gym.permission.PermissionAction;
import com.monkeyclub.gym.permission.RolePermissionService;
import com.monkeyclub.gym.permission.SystemModule;
import com.monkeyclub.gym.security.CurrentUserService;
import com.monkeyclub.gym.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class NotificationService {

    private final MembershipRepository membershipRepository;
    private final NotificationLogRepository notificationLogRepository;
    private final CurrentUserService currentUserService;
    private final RolePermissionService rolePermissionService;

    public NotificationService(MembershipRepository membershipRepository,
                               NotificationLogRepository notificationLogRepository,
                               CurrentUserService currentUserService,
                               RolePermissionService rolePermissionService) {
        this.membershipRepository = membershipRepository;
        this.notificationLogRepository = notificationLogRepository;
        this.currentUserService = currentUserService;
        this.rolePermissionService = rolePermissionService;
    }

    @Transactional
    public List<NotificationLogResponse> sendReminders(int daysBefore) {
        require(SystemModule.NOTIFICACIONES, PermissionAction.CREAR);

        LocalDate targetDate = LocalDate.now(ZoneOffset.UTC).plusDays(daysBefore);
        List<Membership> memberships = membershipRepository.findByEndDateBetweenOrderByEndDateAsc(targetDate, targetDate);

        List<NotificationLog> logs = memberships.stream()
                .map(membership -> {
                    NotificationLog log = new NotificationLog();
                    log.setClient(membership.getClient());
                    log.setDaysBeforeExpiry(daysBefore);
                    log.setChannel("WHATSAPP");
                    log.setMessage("Hola " + membership.getClient().getFirstName() + ", tu membresia vence el "
                            + membership.getEndDate() + ". Te esperamos para renovar.");
                    return log;
                })
                .toList();

        return notificationLogRepository.saveAll(logs)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NotificationLogResponse> logs(LocalDate from, LocalDate to) {
        require(SystemModule.NOTIFICACIONES, PermissionAction.VER);

        LocalDate effectiveFrom = from == null ? LocalDate.now(ZoneOffset.UTC).minusDays(30) : from;
        LocalDate effectiveTo = to == null ? LocalDate.now(ZoneOffset.UTC) : to;

        OffsetDateTime fromDateTime = effectiveFrom.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime toDateTime = effectiveTo.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC);

        return notificationLogRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(fromDateTime, toDateTime)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private User require(SystemModule module, PermissionAction action) {
        User user = currentUserService.getCurrentUser();
        rolePermissionService.requirePermission(user.getRole(), module, action);
        return user;
    }

    private NotificationLogResponse toResponse(NotificationLog log) {
        return new NotificationLogResponse(
                log.getId(),
                log.getClient().getId(),
                log.getClient().getFirstName() + " " + log.getClient().getLastName(),
                log.getDaysBeforeExpiry(),
                log.getMessage(),
                log.getChannel(),
                log.getCreatedAt());
    }
}
