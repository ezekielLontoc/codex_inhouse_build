package com.msams.agent.workflow.functions;

import com.msams.agent.workflow.AgentContext;
import com.msams.agent.workflow.OrderedMicroFunction;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class ClassifyRequestFunction implements OrderedMicroFunction {

    @Override
    public int order() {
        return 20;
    }

    @Override
    public String code() {
        return "classify-request";
    }

    @Override
    public void execute(AgentContext context) {
        String requirements = context.getRequest().getRequirements().toLowerCase(Locale.ENGLISH);
        if (requirements.contains("login") || requirements.contains("authentication")) {
            context.getDetectedRisks().add("AUTHENTICATION_FLOW");
        }
        if (requirements.contains("loan") || requirements.contains("payment")) {
            context.getDetectedRisks().add("REGULATED_FINANCIAL_DOMAIN");
        }
        if (requirements.contains("ai") || requirements.contains("agent")) {
            context.getDetectedRisks().add("AI_GOVERNANCE_REQUIRED");
        }
    }
}
