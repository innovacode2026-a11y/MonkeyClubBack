package com.monkeyclub.gym.membership;

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

    private final MembershipService membershipService;

    public MembershipController(MembershipService membershipService) {
        this.membershipService = membershipService;
    }

    @PostMapping("/sell")
    public MembershipResponse sell(@Valid @RequestBody SellMembershipRequest request) {
        return membershipService.sell(request);
    }

    @PostMapping("/renew")
    public MembershipResponse renew(@Valid @RequestBody SellMembershipRequest request) {
        return membershipService.renew(request);
    }

    @GetMapping("/client/{clientId}")
    public List<MembershipResponse> byClient(@PathVariable UUID clientId) {
        return membershipService.getByClient(clientId);
    }

    @GetMapping("/client/{clientId}/history")
    public List<MembershipHistoryResponse> history(@PathVariable UUID clientId) {
        return membershipService.getHistory(clientId);
    }

    @PostMapping("/refresh-status")
    public void refreshStatus() {
        membershipService.refreshExpiredStatusesManual();
    }
}
