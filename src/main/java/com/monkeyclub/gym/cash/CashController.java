package com.monkeyclub.gym.cash;

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

    private final CashService cashService;

    public CashController(CashService cashService) {
        this.cashService = cashService;
    }

    @PostMapping("/open")
    public CashSessionResponse open(@Valid @RequestBody OpenCashRequest request) {
        return cashService.openCash(request);
    }

    @PostMapping("/{sessionId}/close")
    public CashSessionResponse close(@PathVariable UUID sessionId,
                                     @Valid @RequestBody CloseCashRequest request) {
        return cashService.closeCash(sessionId, request);
    }

    @GetMapping("/current")
    public CashSessionResponse current() {
        return cashService.currentCash();
    }

    @GetMapping("/history")
    public List<CashSessionResponse> history() {
        return cashService.history();
    }
}
