package com.monkeyclub.gym.application.port.in.cash;

import com.monkeyclub.gym.features.cash.CashSessionResponse;
import com.monkeyclub.gym.features.cash.CloseCashRequest;
import com.monkeyclub.gym.features.cash.OpenCashRequest;

import java.util.List;
import java.util.UUID;

public interface CashUseCase {

    CashSessionResponse openCash(OpenCashRequest request);

    CashSessionResponse closeCash(UUID sessionId, CloseCashRequest request);

    CashSessionResponse currentCash();

    List<CashSessionResponse> history();
}
