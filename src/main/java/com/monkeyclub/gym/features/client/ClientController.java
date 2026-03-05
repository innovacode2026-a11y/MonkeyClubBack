package com.monkeyclub.gym.features.client;

import com.monkeyclub.gym.application.port.in.client.ClientUseCase;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientUseCase clientUseCase;

    public ClientController(ClientUseCase clientUseCase) {
        this.clientUseCase = clientUseCase;
    }

    @PostMapping
    public ClientResponse create(@Valid @RequestBody CreateClientRequest request) {
        return clientUseCase.create(request);
    }

    @PutMapping("/{clientId}")
    public ClientResponse update(@PathVariable UUID clientId,
                                 @Valid @RequestBody UpdateClientRequest request) {
        return clientUseCase.update(clientId, request);
    }

    @GetMapping
    public List<ClientResponse> list(@RequestParam(required = false) String q) {
        return clientUseCase.list(q);
    }

    @GetMapping("/{clientId}/audits")
    public List<ClientAuditResponse> history(@PathVariable UUID clientId) {
        return clientUseCase.history(clientId);
    }
}
