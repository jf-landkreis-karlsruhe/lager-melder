import { defineConfig, devices } from '@playwright/test'

const PORT = 9100

/**
 * E2E tests run against the Vite dev server with a mocked backend (see e2e/support/api.ts).
 * No backend or database is needed.
 */
export default defineConfig({
  testDir: './e2e',
  fullyParallel: true,
  forbidOnly: !!process.env.CI,
  retries: process.env.CI ? 1 : 0,
  reporter: process.env.CI ? [['list'], ['html', { open: 'never' }]] : 'list',
  use: {
    baseURL: `http://localhost:${PORT}`,
    locale: 'de-DE',
    timezoneId: 'Europe/Berlin',
    trace: 'retain-on-failure'
  },
  expect: {
    toHaveScreenshot: { maxDiffPixelRatio: 0.01 }
  },
  // screenshots differ between operating systems, keep one baseline per platform
  snapshotPathTemplate: '{testDir}/__screenshots__/{platform}/{testFilePath}/{arg}{ext}',
  projects: [
    {
      name: 'chromium',
      use: { ...devices['Desktop Chrome'], viewport: { width: 1280, height: 900 } }
    }
  ],
  webServer: {
    // without VITE_BACKEND_URL the app calls /api on its own origin, which the tests intercept
    command: `node_modules/.bin/vite --port ${PORT} --strictPort`,
    url: `http://localhost:${PORT}`,
    reuseExistingServer: !process.env.CI
  }
})
