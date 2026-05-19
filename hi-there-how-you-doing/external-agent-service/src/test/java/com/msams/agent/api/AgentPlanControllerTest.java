package com.msams.agent.api;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.msams.agent.ExternalAgentApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = ExternalAgentApplication.class)
@AutoConfigureMockMvc
class AgentPlanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void acceptsGovernedLoginPlanningRequest() throws Exception {
        String body = "{"
            + "\"requestedBy\":\"architect\","
            + "\"domain\":\"authentication\","
            + "\"requirements\":\"Build a login workflow using microfunctions\","
            + "\"flow\":\"validate input -> load user -> verify password -> audit\","
            + "\"scope\":\"Java 8 Spring Boot sample only\","
            + "\"classification\":\"INTERNAL\","
            + "\"specs\":[\"AVCI\", \"MicroFunction\", \"No direct model SDK\"]"
            + "}";

        mockMvc.perform(post("/api/v1/agent/plan")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.decision").value("ACCEPTED_WITH_GUARDRAILS"))
            .andExpect(jsonPath("$.summary", containsString("MicroFunctions")))
            .andExpect(jsonPath("$.avciEvidence.attributable", containsString("architect")));
    }

    @Test
    void rejectsDirectProviderSdkRequest() throws Exception {
        String body = "{"
            + "\"requestedBy\":\"developer\","
            + "\"domain\":\"ai\","
            + "\"requirements\":\"Use direct OpenAI provider SDK in the service\","
            + "\"flow\":\"controller -> provider sdk\","
            + "\"scope\":\"prototype\","
            + "\"classification\":\"INTERNAL\""
            + "}";

        mockMvc.perform(post("/api/v1/agent/plan")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.decision").value("REJECTED"))
            .andExpect(jsonPath("$.blockedActions[0]", containsString("LiteLLM")));
    }
}
