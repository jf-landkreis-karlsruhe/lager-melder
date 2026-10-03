import { expect, test } from '@playwright/test'
import { createJwt, mockApi } from './support/api'

test.describe('login', () => {
  test('redirects to the login page when not logged in', async ({ page }) => {
    await mockApi(page)

    await page.goto('/teilnehmer')

    await expect(page).toHaveURL(/\/login$/)
    await expect(page.getByRole('button', { name: 'Einloggen' })).toBeVisible()
  })

  test('logs in and shows the attendees of the own department', async ({ page }) => {
    let loginBody: unknown
    await mockApi(page, {
      'POST login': async (route) => {
        loginBody = route.request().postDataJSON()
        await route.fulfill({ json: { Authorization: `Bearer ${createJwt('USER')}` } })
      }
    })

    await page.goto('/login')
    await page.getByLabel('Benutzername').fill('jugendwart@ettlingen.de')
    await page.getByLabel('Passwort').fill('geheim')
    await page.getByRole('button', { name: 'Einloggen' }).click()

    await expect(page).toHaveURL(/\/teilnehmer$/)
    await expect(page.getByText('Anna Schmidt')).toBeVisible()
    expect(loginBody).toEqual({ username: 'jugendwart@ettlingen.de', password: 'geheim' })
  })

  test('shows an error for wrong credentials', async ({ page }) => {
    await mockApi(page, {
      'POST login': (route) => route.fulfill({ status: 401, json: { path: '/api/login', status: 401 } })
    })

    await page.goto('/login')
    await page.getByLabel('Benutzername').fill('jugendwart@ettlingen.de')
    await page.getByLabel('Passwort').fill('falsch')
    await page.getByRole('button', { name: 'Einloggen' }).click()

    await expect(page.getByText('Benutzername oder Passwort sind falsch')).toBeVisible()
    await expect(page).toHaveURL(/\/login$/)
  })
})
