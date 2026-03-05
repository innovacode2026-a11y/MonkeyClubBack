package com.monkeyclub.gym.application.port.in.plan;

import com.monkeyclub.gym.features.plan.PlanRequest;
import com.monkeyclub.gym.features.plan.PlanResponse;

import java.util.List;
import java.util.UUID;

public interface PlanUseCase {

    PlanResponse create(PlanRequest request);

    PlanResponse update(UUID planId, PlanRequest request);

    List<PlanResponse> list();

    void delete(UUID planId);
}
