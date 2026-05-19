package com.msams.agent.workflow.functions;

import com.msams.agent.model.AvciEvidence;
import com.msams.agent.workflow.AgentContext;
import com.msams.agent.workflow.OrderedMicroFunction;
import org.springframework.stereotype.Component;

@Component
public class BuildAvciEvidenceFunction implements OrderedMicroFunction {

    @Override
    public int order() {
        return 50;
    }

    @Override
    public String code() {
        return "build-avci-evidence";
    }

    @Override
    public void execute(AgentContext context) {
        AvciEvidence evidence = new AvciEvidence();
        evidence.setAttributable(
            "traceId=" + context.getTraceId() +
            ", requestedBy=" + context.getRequest().getRequestedBy() +
            ", domain=" + context.getRequest().getDomain()
        );
        evidence.setVerifiable(
            "Response includes required unit, component, validation, boundary, and audit tests."
        );
        evidence.setClassifiable(
            "classification=" + safeClassification(context) +
            ", risks=" + context.getDetectedRisks()
        );
        evidence.setImprovable(
            "Rejected actions and failed tests become governed backlog items; no autonomous production change."
        );
        context.getResponse().setAvciEvidence(evidence);
    }

    private String safeClassification(AgentContext context) {
        String classification = context.getRequest().getClassification();
        return classification == null || classification.trim().isEmpty()
            ? "INTERNAL"
            : classification;
    }
}
