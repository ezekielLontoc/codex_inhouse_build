package com.msams.agent.workflow.functions;

import com.msams.agent.workflow.AgentContext;
import com.msams.agent.workflow.OrderedMicroFunction;
import java.util.Arrays;
import org.springframework.stereotype.Component;

@Component
public class BuildImplementationPlanFunction implements OrderedMicroFunction {

    @Override
    public int order() {
        return 30;
    }

    @Override
    public String code() {
        return "build-implementation-plan";
    }

    @Override
    public void execute(AgentContext context) {
        if (context.isBlocked()) {
            context.getResponse().setDecision("REJECTED");
            context.getResponse().setSummary("Request violates one or more non-negotiable MSAMS boundaries.");
            return;
        }

        context.getResponse().setDecision("ACCEPTED_WITH_GUARDRAILS");
        context.getResponse().setSummary(
            "Build a governed external-agent workflow for " + context.getRequest().getDomain() +
            " using MicroFunctions, AVCI evidence, audit logging, and deterministic boundaries."
        );
        context.getResponse().setImplementationSteps(Arrays.asList(
            "Create a versioned process definition for the requested flow.",
            "Split the flow into MicroFunctions with one responsibility per step.",
            "Validate input, scope, classification, and blocked actions before planning.",
            "Route any future model call through LiteLLM/Harness; do not call provider SDKs directly.",
            "Emit traceId, requestedBy, domain, classification, decision, and risk tags as audit evidence.",
            "Return a plan with required tests and AVCI completion evidence."
        ));
    }
}
