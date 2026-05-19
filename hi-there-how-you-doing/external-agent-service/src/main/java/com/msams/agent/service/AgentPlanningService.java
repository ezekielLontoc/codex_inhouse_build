package com.msams.agent.service;

import com.msams.agent.model.AgentPlanRequest;
import com.msams.agent.model.AgentPlanResponse;
import com.msams.agent.workflow.AgentContext;
import com.msams.agent.workflow.OrderedMicroFunction;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class AgentPlanningService {

    private final List<OrderedMicroFunction> workflow;

    public AgentPlanningService(List<OrderedMicroFunction> functions) {
        this.workflow = new ArrayList<OrderedMicroFunction>(functions);
        this.workflow.sort(Comparator.comparingInt(OrderedMicroFunction::order));
    }

    public AgentPlanResponse createPlan(AgentPlanRequest request) {
        AgentContext context = new AgentContext(UUID.randomUUID().toString(), request);
        for (OrderedMicroFunction function : workflow) {
            function.execute(context);
        }
        return context.getResponse();
    }
}
