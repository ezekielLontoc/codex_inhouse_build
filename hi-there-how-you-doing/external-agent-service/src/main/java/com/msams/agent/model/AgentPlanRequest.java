package com.msams.agent.model;

import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class AgentPlanRequest {

    @NotBlank
    @Size(max = 120)
    private String requestedBy;

    @NotBlank
    @Size(max = 120)
    private String domain;

    @NotBlank
    @Size(max = 4000)
    private String requirements;

    @NotBlank
    @Size(max = 4000)
    private String flow;

    @NotBlank
    @Size(max = 2000)
    private String scope;

    @Size(max = 20)
    private List<@Size(max = 500) String> specs = new ArrayList<String>();

    @Size(max = 60)
    private String classification;

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public String getFlow() {
        return flow;
    }

    public void setFlow(String flow) {
        this.flow = flow;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public List<String> getSpecs() {
        return specs;
    }

    public void setSpecs(List<String> specs) {
        this.specs = specs;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }
}
