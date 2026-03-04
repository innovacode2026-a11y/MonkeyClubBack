package com.monkeyclub.gym.attendance;

import com.monkeyclub.gym.client.Client;
import com.monkeyclub.gym.client.ClientRepository;
import com.monkeyclub.gym.client.ClientStatus;
import com.monkeyclub.gym.common.BusinessException;
import com.monkeyclub.gym.membership.Membership;
import com.monkeyclub.gym.membership.MembershipRepository;
import com.monkeyclub.gym.membership.MembershipStatus;
import com.monkeyclub.gym.permission.PermissionAction;
import com.monkeyclub.gym.permission.RolePermissionService;
import com.monkeyclub.gym.permission.SystemModule;
import com.monkeyclub.gym.security.CurrentUserService;
import com.monkeyclub.gym.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Service
public class AttendanceService {

    private final AttendanceRecordRepository attendanceRecordRepository;
    private final ClientRepository clientRepository;
    private final MembershipRepository membershipRepository;
    private final CurrentUserService currentUserService;
    private final RolePermissionService rolePermissionService;

    public AttendanceService(AttendanceRecordRepository attendanceRecordRepository,
                             ClientRepository clientRepository,
                             MembershipRepository membershipRepository,
                             CurrentUserService currentUserService,
                             RolePermissionService rolePermissionService) {
        this.attendanceRecordRepository = attendanceRecordRepository;
        this.clientRepository = clientRepository;
        this.membershipRepository = membershipRepository;
        this.currentUserService = currentUserService;
        this.rolePermissionService = rolePermissionService;
    }

    @Transactional
    public AttendanceResponse checkIn(CheckInRequest request) {
        User actor = require(SystemModule.CONTROL_INGRESO, PermissionAction.CREAR);
        Client client = resolveClient(request);

        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        Membership membership = membershipRepository.findTopByClientIdOrderByEndDateDesc(client.getId()).orElse(null);

        boolean activeMembership = membership != null
                && membership.getStatus() == MembershipStatus.ACTIVA
                && membership.getEndDate() != null
                && !membership.getEndDate().isBefore(today);

        boolean accessGranted;
        String message;

        if (client.getStatus() == ClientStatus.SUSPENDIDO) {
            accessGranted = false;
            message = "Cliente suspendido";
        } else if (activeMembership) {
            accessGranted = true;
            message = "Acceso permitido";
            client.setStatus(ClientStatus.ACTIVO);
        } else {
            accessGranted = false;
            message = "Membresia vencida o inexistente";
            client.setStatus(ClientStatus.VENCIDO);
        }

        clientRepository.save(client);

        AttendanceRecord record = new AttendanceRecord();
        record.setClient(client);
        record.setRegisteredBy(actor);
        record.setMethod(request.method());
        record.setAccessGranted(accessGranted);
        record.setMessage(message);
        record.setCheckInAt(OffsetDateTime.now(ZoneOffset.UTC));

        return toResponse(attendanceRecordRepository.save(record));
    }

    @Transactional(readOnly = true)
    public List<AttendanceResponse> history(LocalDate from, LocalDate to, UUID clientId) {
        require(SystemModule.CONTROL_INGRESO, PermissionAction.VER);

        LocalDate effectiveFrom = from == null ? LocalDate.now(ZoneOffset.UTC).minusDays(30) : from;
        LocalDate effectiveTo = to == null ? LocalDate.now(ZoneOffset.UTC) : to;

        OffsetDateTime fromDateTime = effectiveFrom.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime toDateTime = effectiveTo.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC);

        if (clientId != null) {
            return attendanceRecordRepository.findByClientIdAndCheckInAtBetweenOrderByCheckInAtDesc(clientId, fromDateTime, toDateTime)
                    .stream().map(this::toResponse).toList();
        }

        return attendanceRecordRepository.findByCheckInAtBetweenOrderByCheckInAtDesc(fromDateTime, toDateTime)
                .stream().map(this::toResponse).toList();
    }

    private Client resolveClient(CheckInRequest request) {
        if (request.clientId() != null) {
            return clientRepository.findById(request.clientId())
                    .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Cliente no encontrado"));
        }

        if (request.codeOrDocument() == null || request.codeOrDocument().isBlank()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Debe enviar clientId o codeOrDocument");
        }

        return clientRepository.findByDocument(request.codeOrDocument().trim())
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Cliente no encontrado por codigo/documento"));
    }

    private User require(SystemModule module, PermissionAction action) {
        User user = currentUserService.getCurrentUser();
        rolePermissionService.requirePermission(user.getRole(), module, action);
        return user;
    }

    private AttendanceResponse toResponse(AttendanceRecord record) {
        Client client = record.getClient();
        return new AttendanceResponse(
                record.getId(),
                client.getId(),
                client.getFirstName() + " " + client.getLastName(),
                record.getMethod(),
                record.isAccessGranted(),
                record.getMessage(),
                record.getRegisteredBy().getUsername(),
                record.getCheckInAt());
    }
}
