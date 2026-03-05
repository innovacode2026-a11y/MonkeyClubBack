package com.monkeyclub.gym.features.membership;

import com.monkeyclub.gym.application.port.in.membership.MembershipUseCase;

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
@RequestMapping("/api/memberships")
public class MembershipController {

    private final MembershipUseCase membershipUseCase;

    public MembershipController(MembershipUseCase membershipUseCase) {
        this.membershipUseCase = membershipUseCase;
    }

    @PostMapping("/sell")
    public MembershipResponse sell(@Valid @RequestBody SellMembershipRequest request) {
        return membershipUseCase.sell(request);
    }

    @PostMapping("/renew")
    public MembershipResponse renew(@Valid @RequestBody SellMembershipRequest request) {
        return membershipUseCase.renew(request);
    }

    @GetMapping("/client/{clientId}")
    public List<MembershipResponse> byClient(@PathVariable UUID clientId) {
        return membershipUseCase.getByClient(clientId);
    }

    @GetMapping("/client/{clientId}/history")
    public List<MembershipHistoryResponse> history(@PathVariable UUID clientId) {
        return membershipUseCase.getHistory(clientId);
    }

    @PostMapping("/refresh-status")
    public void refreshStatus() {
        membershipUseCase.refreshExpiredStatusesManual();
    }
}
