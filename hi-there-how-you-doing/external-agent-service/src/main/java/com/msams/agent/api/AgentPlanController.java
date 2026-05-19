package com.msams.agent.api;

import com.msams.agent.model.AgentPlanRequest;
import com.msams.agent.model.AgentPlanResponse;
import com.msams.agent.service.AgentPlanningService;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/agent")
public class AgentPlanController {

    private final AgentPlanningService planningService;

    public AgentPlanController(AgentPlanningService planningService) {
        this.planningService = planningService;
    }

    @PostMapping("/plan")
    public ResponseEntity<AgentPlanResponse> createPlan(@Valid @RequestBody AgentPlanRequest request) {
        return ResponseEntity.ok(planningService.createPlan(request));
    }
}
