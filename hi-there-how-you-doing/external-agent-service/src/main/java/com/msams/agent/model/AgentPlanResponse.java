package com.msams.agent.model;

import java.util.ArrayList;
import java.util.List;

public class AgentPlanResponse {

    private String traceId;
    private String decision;
    private String summary;
    private List<String> implementationSteps = new ArrayList<String>();
    private List<String> requiredTests = new ArrayList<String>();
    private List<String> blockedActions = new ArrayList<String>();
    private AvciEvidence avciEvidence;

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<String> getImplementationSteps() {
        return implementationSteps;
    }

    public void setImplementationSteps(List<String> implementationSteps) {
        this.implementationSteps = implementationSteps;
    }

    public List<String> getRequiredTests() {
        return requiredTests;
    }

    public void setRequiredTests(List<String> requiredTests) {
        this.requiredTests = requiredTests;
    }

    public List<String> getBlockedActions() {
        return blockedActions;
    }

    public void setBlockedActions(List<String> blockedActions) {
        this.blockedActions = blockedActions;
    }

    public AvciEvidence getAvciEvidence() {
        return avciEvidence;
    }

    public void setAvciEvidence(AvciEvidence avciEvidence) {
        this.avciEvidence = avciEvidence;
    }
}
