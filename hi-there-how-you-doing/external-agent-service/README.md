# MSAMS External Agent Service Sample

This is a Java 8 / Spring Boot 2.7 sample of an external planning agent service.
It accepts requirements, flow, scope, and specs, then returns a governed plan with
AVCI evidence.

The sample follows the `bedrock` boundaries:

- no direct model-provider SDK calls
- MicroFunction workflow design
- deterministic boundary validation
- AVCI evidence on every response
- audit logs with `traceId`
- no secrets or PII in logs

## Run

```bash
mvn test
mvn spring-boot:run
```

Service URL:

```text
http://localhost:8088
```

## Scenario

An architect wants an agent to plan a login module.

Input:

```json
{
  "requestedBy": "architect",
  "domain": "authentication",
  "requirements": "Build a login workflow using microfunctions",
  "flow": "validate input -> load user -> verify password -> create session -> audit",
  "scope": "Java 8 Spring Boot sample only",
  "classification": "INTERNAL",
  "specs": ["AVCI", "MicroFunction", "No direct model SDK"]
}
```

Command:

```bash
curl -X POST http://localhost:8088/api/v1/agent/plan \
  -H "Content-Type: application/json" \
  -d "{\"requestedBy\":\"architect\",\"domain\":\"authentication\",\"requirements\":\"Build a login workflow using microfunctions\",\"flow\":\"validate input -> load user -> verify password -> create session -> audit\",\"scope\":\"Java 8 Spring Boot sample only\",\"classification\":\"INTERNAL\",\"specs\":[\"AVCI\",\"MicroFunction\",\"No direct model SDK\"]}"
```

Expected result:

- decision: `ACCEPTED_WITH_GUARDRAILS`
- implementation steps are returned
- required tests are returned
- AVCI evidence is returned

Blocked scenario:

```json
{
  "requestedBy": "developer",
  "domain": "ai",
  "requirements": "Use direct OpenAI provider SDK in the service",
  "flow": "controller -> provider sdk",
  "scope": "prototype",
  "classification": "INTERNAL"
}
```

Expected result:

- decision: `REJECTED`
- reason: direct model-provider calls must be routed through LiteLLM/Harness

## Playwright Heavy-Duty API Tests

Install the Playwright test runner:

```bash
npm install
```

Run the API test suite:

```bash
npm run test:e2e
```

The Playwright suite covers:

- health endpoint readiness
- accepted governed planning request
- rejected direct provider SDK request
- rejected audit/guardrail bypass request
- validation failure for missing required fields
- default classification behaviour
- concurrent burst of 20 requests with unique trace IDs
- stable response shape for downstream consumers

The test runner reuses an existing service on `http://127.0.0.1:8088`.
If the service is not already running, Playwright starts it with:

```bash
mvn spring-boot:run
```

To test a different endpoint:

```bash
AGENT_BASE_URL=http://host:port npm run test:e2e
```

## How It Maps To MicroFunction Design

The request is processed through ordered MicroFunctions:

```text
validate-boundary
classify-request
build-implementation-plan
build-testing-plan
build-avci-evidence
audit-decision
```

In a production MSAMS implementation, these function codes would come from a
versioned process definition stored in PostgreSQL and activated through the
MicroFunction framework.
