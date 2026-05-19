import { expect, test, type APIRequestContext } from '@playwright/test';

type AgentPlanResponse = {
  traceId: string;
  decision: string;
  summary: string;
  implementationSteps: string[];
  requiredTests: string[];
  blockedActions: string[];
  avciEvidence: {
    attributable: string;
    verifiable: string;
    classifiable: string;
    improvable: string;
  };
};

const uuidPattern = /^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i;

function acceptedLoginPayload() {
  return {
    requestedBy: 'architect',
    domain: 'authentication',
    requirements: 'Build a login workflow using microfunctions',
    flow: 'validate input -> load user -> verify password -> create session -> audit',
    scope: 'Java 8 Spring Boot sample only',
    classification: 'INTERNAL',
    specs: ['AVCI', 'MicroFunction', 'No direct model SDK']
  };
}

async function postPlan(request: APIRequestContext, payload: object) {
  const response = await request.post('/api/v1/agent/plan', {
    data: payload,
    headers: { 'Content-Type': 'application/json' }
  });
  return response;
}

test.describe('MSAMS external agent API - heavy duty sample', () => {
  test('health endpoint is available before API scenarios run', async ({ request }) => {
    const response = await request.get('/actuator/health');
    expect(response.ok()).toBeTruthy();
    expect(await response.json()).toEqual({ status: 'UP' });
  });

  test('accepts a governed login planning request and returns AVCI evidence', async ({ request }) => {
    const response = await postPlan(request, acceptedLoginPayload());
    expect(response.status()).toBe(200);

    const body = (await response.json()) as AgentPlanResponse;
    expect(body.traceId).toMatch(uuidPattern);
    expect(body.decision).toBe('ACCEPTED_WITH_GUARDRAILS');
    expect(body.summary).toContain('MicroFunctions');
    expect(body.implementationSteps).toHaveLength(6);
    expect(body.requiredTests).toHaveLength(5);
    expect(body.blockedActions).toEqual([]);
    expect(body.avciEvidence.attributable).toContain('requestedBy=architect');
    expect(body.avciEvidence.classifiable).toContain('AUTHENTICATION_FLOW');
    expect(body.avciEvidence.improvable).toContain('no autonomous production change');
  });

  test('rejects direct model-provider SDK usage', async ({ request }) => {
    const response = await postPlan(request, {
      requestedBy: 'developer',
      domain: 'ai',
      requirements: 'Use direct OpenAI provider SDK in the service',
      flow: 'controller -> provider sdk',
      scope: 'prototype',
      classification: 'INTERNAL'
    });

    expect(response.status()).toBe(200);
    const body = (await response.json()) as AgentPlanResponse;
    expect(body.decision).toBe('REJECTED');
    expect(body.blockedActions.join(' ')).toContain('LiteLLM/Harness');
    expect(body.avciEvidence.classifiable).toContain('AI_GOVERNANCE_REQUIRED');
  });

  test('rejects requests that attempt to disable audit or guardrails', async ({ request }) => {
    const response = await postPlan(request, {
      requestedBy: 'operator',
      domain: 'security',
      requirements: 'Disable audit and bypass guardrail for faster delivery',
      flow: 'skip controls -> deploy',
      scope: 'unsafe shortcut',
      classification: 'INTERNAL'
    });

    expect(response.status()).toBe(200);
    const body = (await response.json()) as AgentPlanResponse;
    expect(body.decision).toBe('REJECTED');
    expect(body.blockedActions.length).toBeGreaterThanOrEqual(1);
    expect(body.blockedActions[0]).toContain('outside the MSAMS governance boundary');
  });

  test('defaults classification evidence to INTERNAL when caller omits classification', async ({ request }) => {
    const payload = acceptedLoginPayload() as Record<string, unknown>;
    delete payload.classification;

    const response = await postPlan(request, payload);
    expect(response.status()).toBe(200);

    const body = (await response.json()) as AgentPlanResponse;
    expect(body.decision).toBe('ACCEPTED_WITH_GUARDRAILS');
    expect(body.avciEvidence.classifiable).toContain('classification=INTERNAL');
  });

  test('enforces request validation for missing required fields', async ({ request }) => {
    const response = await postPlan(request, {
      requestedBy: 'architect',
      domain: 'authentication',
      requirements: '',
      flow: 'validate input -> audit',
      scope: 'sample'
    });

    expect(response.status()).toBe(400);
    const text = await response.text();
    expect(text).toContain('Bad Request');
  });

  test('handles a concurrent burst without losing traceability', async ({ request }) => {
    const calls = Array.from({ length: 20 }, (_, i) => {
      const payload = acceptedLoginPayload();
      payload.requestedBy = `architect-${i}`;
      return postPlan(request, payload);
    });

    const started = Date.now();
    const responses = await Promise.all(calls);
    const elapsedMs = Date.now() - started;

    expect(elapsedMs).toBeLessThan(10_000);
    const bodies = await Promise.all(responses.map(async response => {
      expect(response.status()).toBe(200);
      return response.json() as Promise<AgentPlanResponse>;
    }));

    const traceIds = new Set(bodies.map(body => body.traceId));
    expect(traceIds.size).toBe(20);
    for (const body of bodies) {
      expect(body.traceId).toMatch(uuidPattern);
      expect(body.decision).toBe('ACCEPTED_WITH_GUARDRAILS');
      expect(body.avciEvidence.attributable).toContain('requestedBy=architect-');
    }
  });

  test('keeps response shape stable for downstream consumers', async ({ request }) => {
    const response = await postPlan(request, acceptedLoginPayload());
    const body = (await response.json()) as Record<string, unknown>;

    expect(Object.keys(body).sort()).toEqual([
      'avciEvidence',
      'blockedActions',
      'decision',
      'implementationSteps',
      'requiredTests',
      'summary',
      'traceId'
    ]);

    const avci = body.avciEvidence as Record<string, unknown>;
    expect(Object.keys(avci).sort()).toEqual([
      'attributable',
      'classifiable',
      'improvable',
      'verifiable'
    ]);
  });
});
