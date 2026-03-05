package com.monkeyclub.gym.features.cash;

import com.monkeyclub.gym.application.port.in.cash.CashUseCase;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cash")
public class CashController {

    private final CashUseCase cashUseCase;

    public CashController(CashUseCase cashUseCase) {
        this.cashUseCase = cashUseCase;
    }

    @PostMapping("/open")
    public CashSessionResponse open(@Valid @RequestBody OpenCashRequest request) {
        return cashUseCase.openCash(request);
    }

    @PostMapping("/{sessionId}/close")
    public CashSessionResponse close(@PathVariable UUID sessionId,
                                     @Valid @RequestBody CloseCashRequest request) {
        return cashUseCase.closeCash(sessionId, request);
    }

    @GetMapping("/current")
    public CashSessionResponse current() {
        return cashUseCase.currentCash();
    }

    @GetMapping("/history")
    public List<CashSessionResponse> history() {
        return cashUseCase.history();
    }
}
