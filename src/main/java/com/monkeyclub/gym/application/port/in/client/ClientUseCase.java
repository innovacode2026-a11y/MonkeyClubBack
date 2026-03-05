package com.monkeyclub.gym.application.port.in.client;

import com.monkeyclub.gym.features.client.ClientAuditResponse;
import com.monkeyclub.gym.features.client.ClientResponse;
import com.monkeyclub.gym.features.client.CreateClientRequest;
import com.monkeyclub.gym.features.client.UpdateClientRequest;

import java.util.List;
import java.util.UUID;

public interface ClientUseCase {

    ClientResponse create(CreateClientRequest request);

    ClientResponse update(UUID clientId, UpdateClientRequest request);

    List<ClientResponse> list(String query);

    List<ClientAuditResponse> history(UUID clientId);
}
