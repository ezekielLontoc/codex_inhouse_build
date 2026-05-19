package com.msams.agent.workflow.functions;

import com.msams.agent.workflow.AgentContext;
import com.msams.agent.workflow.OrderedMicroFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AuditDecisionFunction implements OrderedMicroFunction {

    private static final Logger log = LoggerFactory.getLogger(AuditDecisionFunction.class);

    @Override
    public int order() {
        return 60;
    }

    @Override
    public String code() {
        return "audit-decision";
    }

    @Override
    public void execute(AgentContext context) {
        log.info(
            "AGENT_AUDIT traceId={} requestedBy={} domain={} decision={} risks={} blockedCount={}",
            context.getTraceId(),
            context.getRequest().getRequestedBy(),
            context.getRequest().getDomain(),
            context.getResponse().getDecision(),
            context.getDetectedRisks(),
            context.getResponse().getBlockedActions().size()
        );
    }
}
