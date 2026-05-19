package com.msams.agent.workflow;

public interface MicroFunction {

    String code();

    void execute(AgentContext context);
}
