package com.monkeyclub.gym.plan;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/plans")
public class PlanController {

    private final PlanService planService;

    public PlanController(PlanService planService) {
        this.planService = planService;
    }

    @PostMapping
    public PlanResponse create(@Valid @RequestBody PlanRequest request) {
        return planService.create(request);
    }

    @PutMapping("/{planId}")
    public PlanResponse update(@PathVariable UUID planId,
                               @Valid @RequestBody PlanRequest request) {
        return planService.update(planId, request);
    }

    @GetMapping
    public List<PlanResponse> list() {
        return planService.list();
    }

    @DeleteMapping("/{planId}")
    public void delete(@PathVariable UUID planId) {
        planService.delete(planId);
    }
}
