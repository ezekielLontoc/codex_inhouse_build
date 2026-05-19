package com.msams.agent.workflow.functions;

import com.msams.agent.workflow.AgentContext;
import com.msams.agent.workflow.OrderedMicroFunction;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class ValidateBoundaryFunction implements OrderedMicroFunction {

    @Override
    public int order() {
        return 10;
    }

    @Override
    public String code() {
        return "validate-boundary";
    }

    @Override
    public void execute(AgentContext context) {
        String combined = (
            context.getRequest().getRequirements() + " " +
            context.getRequest().getFlow() + " " +
            context.getRequest().getScope()
        ).toLowerCase(Locale.ENGLISH);

        if (containsAny(combined, "production password", "raw pii", "bypass guardrail", "disable audit")) {
            context.block("Rejected: request asks for an action outside the MSAMS governance boundary.");
        }

        if (containsAny(combined, "direct openai", "direct anthropic", "provider sdk")) {
            context.block("Rejected: direct model-provider calls are not allowed; route through LiteLLM/Harness.");
        }
    }

    private boolean containsAny(String input, String... needles) {
        for (String needle : needles) {
            if (input.contains(needle)) {
                return true;
            }
        }
        return false;
    }
}
