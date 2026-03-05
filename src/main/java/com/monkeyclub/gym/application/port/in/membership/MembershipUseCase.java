package com.monkeyclub.gym.application.port.in.membership;

import com.monkeyclub.gym.features.membership.MembershipHistoryResponse;
import com.monkeyclub.gym.features.membership.MembershipResponse;
import com.monkeyclub.gym.features.membership.SellMembershipRequest;

import java.util.List;
import java.util.UUID;

public interface MembershipUseCase {

    MembershipResponse sell(SellMembershipRequest request);

    MembershipResponse renew(SellMembershipRequest request);

    List<MembershipResponse> getByClient(UUID clientId);

    List<MembershipHistoryResponse> getHistory(UUID clientId);

    void refreshExpiredStatusesManual();
}
