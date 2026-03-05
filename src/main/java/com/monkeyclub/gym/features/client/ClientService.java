package com.monkeyclub.gym.features.client;

import com.monkeyclub.gym.application.port.in.client.ClientUseCase;

import com.monkeyclub.gym.common.BusinessException;
import com.monkeyclub.gym.features.permission.PermissionAction;
import com.monkeyclub.gym.features.permission.RolePermissionService;
import com.monkeyclub.gym.features.permission.SystemModule;
import com.monkeyclub.gym.security.CurrentUserService;
import com.monkeyclub.gym.features.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ClientService implements ClientUseCase {

    private final ClientRepository clientRepository;
    private final ClientAuditRepository clientAuditRepository;
    private final CurrentUserService currentUserService;
    private final RolePermissionService rolePermissionService;

    public ClientService(ClientRepository clientRepository,
                         ClientAuditRepository clientAuditRepository,
                         CurrentUserService currentUserService,
                         RolePermissionService rolePermissionService) {
        this.clientRepository = clientRepository;
        this.clientAuditRepository = clientAuditRepository;
        this.currentUserService = currentUserService;
        this.rolePermissionService = rolePermissionService;
    }

    public ClientResponse create(CreateClientRequest request) {
        User actor = require(SystemModule.CLIENTES, PermissionAction.CREAR);

        if (clientRepository.existsByDocument(request.document().trim())) {
            throw new BusinessException(HttpStatus.CONFLICT, "Ya existe un cliente con ese documento");
        }

        Client client = new Client();
        client.setFirstName(request.firstName().trim());
        client.setLastName(request.lastName().trim());
        client.setDocument(request.document().trim());
        client.setPhone(request.phone().trim());
        client.setEmail(normalizeNullable(request.email()));
        client.setInternalNotes(normalizeNullable(request.internalNotes()));
        client.setStatus(ClientStatus.ACTIVO);

        Client saved = clientRepository.save(client);
        saveAudit(saved, actor, "CREACION", "Se registro cliente " + saved.getFirstName() + " " + saved.getLastName());
        return toResponse(saved);
    }

    public ClientResponse update(UUID clientId, UpdateClientRequest request) {
        User actor = require(SystemModule.CLIENTES, PermissionAction.EDITAR);

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Cliente no encontrado"));

        if (clientRepository.existsByDocumentAndIdNot(request.document().trim(), clientId)) {
            throw new BusinessException(HttpStatus.CONFLICT, "Ya existe otro cliente con ese documento");
        }

        String previous = snapshot(client);

        client.setFirstName(request.firstName().trim());
        client.setLastName(request.lastName().trim());
        client.setDocument(request.document().trim());
        client.setPhone(request.phone().trim());
        client.setEmail(normalizeNullable(request.email()));
        client.setInternalNotes(normalizeNullable(request.internalNotes()));
        if (request.status() != null) {
            client.setStatus(request.status());
        }

        Client saved = clientRepository.save(client);
        saveAudit(saved, actor, "EDICION", "Antes: " + previous + " | Despues: " + snapshot(saved));
        return toResponse(saved);
    }

    public List<ClientResponse> list(String query) {
        require(SystemModule.CLIENTES, PermissionAction.VER);

        if (query == null || query.isBlank()) {
            return clientRepository.findAll().stream().map(this::toResponse).toList();
        }
        return clientRepository.search(query.trim()).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<ClientAuditResponse> history(UUID clientId) {
        require(SystemModule.CLIENTES, PermissionAction.VER);
        return clientAuditRepository.findByClientIdOrderByCreatedAtDesc(clientId)
                .stream()
                .map(a -> new ClientAuditResponse(
                        a.getId(),
                        a.getAction(),
                        a.getDetail(),
                        a.getChangedBy() == null ? "sistema" : a.getChangedBy().getUsername(),
                        a.getCreatedAt()))
                .toList();
    }

    private User require(SystemModule module, PermissionAction action) {
        User user = currentUserService.getCurrentUser();
        rolePermissionService.requirePermission(user.getRole(), module, action);
        return user;
    }

    private void saveAudit(Client client, User changedBy, String action, String detail) {
        ClientAudit audit = new ClientAudit();
        audit.setClient(client);
        audit.setChangedBy(changedBy);
        audit.setAction(action);
        audit.setDetail(detail);
        clientAuditRepository.save(audit);
    }

    private ClientResponse toResponse(Client client) {
        return new ClientResponse(
                client.getId(),
                client.getFirstName(),
                client.getLastName(),
                client.getFirstName() + " " + client.getLastName(),
                client.getDocument(),
                client.getPhone(),
                client.getEmail(),
                client.getStatus(),
                client.getInternalNotes(),
                client.getCreatedAt(),
                client.getUpdatedAt());
    }

    private String snapshot(Client client) {
        return "{" +
                "nombre='" + client.getFirstName() + " " + client.getLastName() + "'," +
                "documento='" + client.getDocument() + "'," +
                "telefono='" + client.getPhone() + "'," +
                "estado='" + client.getStatus() + "'" +
                "}";
    }

    private String normalizeNullable(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}

