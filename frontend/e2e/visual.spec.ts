import { expect, test } from '@playwright/test'
import { loginAs, mockApi } from './support/api'

/**
 * Visual regression tests. Baselines live in e2e/__screenshots__/<platform>/ and are created in the Playwright
 * Docker image (npm run test:e2e:update-screenshots) so they are identical on every machine and in CI.
 */
test.describe('visual', () => {
  test('login page', async ({ page }) => {
    await mockApi(page)

    await page.goto('/login')

    await expect(page.getByRole('button', { name: 'Einloggen' })).toBeVisible()
    await expect(page).toHaveScreenshot('login.png', { fullPage: true })
  })

  test('attendee registration', async ({ page }) => {
    await loginAs(page, 'USER')
    await mockApi(page)

    await page.goto('/teilnehmer')

    await expect(page.getByText('Anna Schmidt')).toBeVisible()
    await expect(page).toHaveScreenshot('attendees.png', {
      fullPage: true,
      // the countdown to the registration end changes every minute
      mask: [page.getByRole('alert').filter({ hasText: 'Anmeldeschluss' })]
    })
  })
})
