package com.msams.agent.workflow.functions;

import com.msams.agent.workflow.AgentContext;
import com.msams.agent.workflow.OrderedMicroFunction;
import java.util.Arrays;
import org.springframework.stereotype.Component;

@Component
public class BuildTestingPlanFunction implements OrderedMicroFunction {

    @Override
    public int order() {
        return 40;
    }

    @Override
    public String code() {
        return "build-testing-plan";
    }

    @Override
    public void execute(AgentContext context) {
        if (context.isBlocked()) {
            context.getResponse().setRequiredTests(Arrays.asList(
                "Boundary rejection test for blocked requests.",
                "Audit emission test for rejected requests."
            ));
            return;
        }

        context.getResponse().setRequiredTests(Arrays.asList(
            "Unit tests for every MicroFunction.",
            "Component test for the full workflow order.",
            "Negative tests for blocked phrases and out-of-scope actions.",
            "Validation tests for missing requirements, flow, scope, and requestedBy.",
            "Audit test proving traceId and classification are emitted without secrets or PII."
        ));
    }
}
