package com.msams.agent.workflow;

import com.msams.agent.model.AgentPlanRequest;
import com.msams.agent.model.AgentPlanResponse;
import java.util.ArrayList;
import java.util.List;

public class AgentContext {

    private final String traceId;
    private final AgentPlanRequest request;
    private final AgentPlanResponse response = new AgentPlanResponse();
    private final List<String> detectedRisks = new ArrayList<String>();
    private boolean blocked;

    public AgentContext(String traceId, AgentPlanRequest request) {
        this.traceId = traceId;
        this.request = request;
        this.response.setTraceId(traceId);
    }

    public String getTraceId() {
        return traceId;
    }

    public AgentPlanRequest getRequest() {
        return request;
    }

    public AgentPlanResponse getResponse() {
        return response;
    }

    public List<String> getDetectedRisks() {
        return detectedRisks;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void block(String reason) {
        this.blocked = true;
        this.response.getBlockedActions().add(reason);
    }
}
