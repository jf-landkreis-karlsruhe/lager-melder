import { expect, test } from '@playwright/test'
import { loginAs, mockApi, youth, youthLeader } from './support/api'

test.describe('attendee registration', () => {
  test.beforeEach(async ({ page }) => {
    await loginAs(page, 'USER')
  })

  test('lists youths and youth leaders of the department', async ({ page }) => {
    const { unmocked } = await mockApi(page)

    await page.goto('/teilnehmer')

    await expect(page.getByText(`${youth.firstName} ${youth.lastName}`)).toBeVisible()
    await expect(page.getByText(`${youthLeader.firstName} ${youthLeader.lastName}`)).toBeVisible()
    expect(unmocked).toEqual([])
  })
})

test.describe('add attendee', () => {
  test('creates a youth with the entered data', async ({ page }) => {
    let createdBody: Record<string, unknown> | undefined
    await loginAs(page, 'USER')
    await mockApi(page, {
      'POST attendees': async (route) => {
        createdBody = route.request().postDataJSON()
        await route.fulfill({ json: { ...youth, ...createdBody, id: 'youth-2', code: 'NEUE1234', status: null } })
      }
    })
    await page.goto('/teilnehmer')

    await page.getByRole('button', { name: 'Jugendliche hinzufügen' }).click()
    const dialog = page.getByRole('dialog')
    await dialog.getByLabel('Vorname').fill('Mia')
    await dialog.getByLabel('Nachname').fill('Wagner')
    await dialog.getByLabel('Geburtsdatum').fill('2013-05-01')
    await dialog.getByRole('combobox').first().click()
    await page.getByRole('listbox').getByText('140', { exact: true }).click()
    await dialog.getByRole('button', { name: 'Speichern' }).click()

    await expect(page.getByText('Mia Wagner')).toBeVisible()
    expect(createdBody).toMatchObject({
      firstName: 'Mia',
      lastName: 'Wagner',
      birthday: '2013-05-01',
      tShirtSize: '140',
      food: 'MEAT',
      role: 'YOUTH',
      departmentId: 2
    })
  })
})
