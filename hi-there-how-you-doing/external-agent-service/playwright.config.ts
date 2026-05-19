import { defineConfig } from '@playwright/test';

const baseURL = process.env.AGENT_BASE_URL || 'http://127.0.0.1:8088';

export default defineConfig({
  testDir: './tests/e2e',
  timeout: 30_000,
  expect: {
    timeout: 5_000
  },
  fullyParallel: true,
  retries: process.env.CI ? 2 : 0,
  reporter: [
    ['list'],
    ['html', { outputFolder: 'playwright-report', open: 'never' }],
    ['json', { outputFile: 'target/playwright-results.json' }]
  ],
  use: {
    baseURL,
    extraHTTPHeaders: {
      'Accept': 'application/json'
    }
  },
  webServer: {
    command: 'mvn spring-boot:run',
    url: `${baseURL}/actuator/health`,
    reuseExistingServer: true,
    timeout: 120_000
  }
});
