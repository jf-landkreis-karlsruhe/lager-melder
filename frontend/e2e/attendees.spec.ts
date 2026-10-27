import { expect, test } from '@playwright/test'
import { department, loginAs, mockApi, youth, youthLeader } from './support/api'

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

  test('shows only the number of a youth leader without valid Juleika', async ({ page }) => {
    await mockApi(page, {
      [`GET departments/${department.id}/attendees`]: {
        youths: [youth],
        youthLeaders: [{ ...youthLeader, juleikaNumber: '99999', juleikaExpireDate: null }],
        children: [],
        childLeaders: [],
        zKids: [],
        helpers: []
      }
    })

    await page.goto('/teilnehmer')

    await expect(page.getByTitle('Keine gültige Juleika')).toHaveText('99999')
    await expect(page.getByTitle('Keine gültige Juleika')).toHaveCSS('color', 'rgb(251, 140, 0)')
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

  test('creates a youth leader after checking the Juleika', async ({ page }) => {
    let createdBody: Record<string, unknown> | undefined
    await loginAs(page, 'USER')
    const { unmocked } = await mockApi(page, {
      'POST attendees': async (route) => {
        createdBody = route.request().postDataJSON()
        await route.fulfill({
          json: { ...youthLeader, ...createdBody, id: 'leader-2', code: 'LEA12345', status: null }
        })
      }
    })
    await page.goto('/teilnehmer')

    await page.getByRole('button', { name: 'Jugendleiter hinzufügen' }).click()
    const dialog = page.getByRole('dialog')
    await dialog.getByLabel('Vorname').fill('Lea')
    await dialog.getByLabel('Nachname').fill('Leiter')
    await dialog.getByLabel('Geburtsdatum').fill('2000-01-01')
    await dialog.getByRole('combobox').first().click()
    await page.getByRole('listbox').getByText('L', { exact: true }).click()
    const save = dialog.getByRole('button', { name: 'Speichern' })

    await dialog.getByLabel('Juleika-Nummer').fill('12340')
    await dialog.getByLabel('Juleika-Nummer').blur()

    await expect(dialog.getByText('Juleika gültig')).toBeVisible()
    await expect(dialog.getByLabel('Juleika-Ablaufdatum')).toHaveValue('2099-12-31')
    await save.click()

    await expect(page.getByText('Lea Leiter')).toBeVisible()
    expect(createdBody).toMatchObject({
      lastName: 'Leiter',
      role: 'YOUTH_LEADER',
      juleikaNumber: '12340',
      juleikaExpireDate: '2099-12-31'
    })
    expect(unmocked).toEqual([])
  })

  test('creates a youth leader whose Juleika is invalid', async ({ page }) => {
    let createdBody: Record<string, unknown> | undefined
    await loginAs(page, 'USER')
    const { unmocked } = await mockApi(page, {
      'POST juleika/validate': { valid: false },
      'POST attendees': async (route) => {
        createdBody = route.request().postDataJSON()
        await route.fulfill({
          json: { ...youthLeader, ...createdBody, id: 'leader-2', code: 'LEA12345', status: null }
        })
      }
    })
    await page.goto('/teilnehmer')

    await page.getByRole('button', { name: 'Jugendleiter hinzufügen' }).click()
    const dialog = page.getByRole('dialog')
    await dialog.getByLabel('Vorname').fill('Lea')
    await dialog.getByLabel('Nachname').fill('Leiter')
    await dialog.getByLabel('Geburtsdatum').fill('2000-01-01')
    await dialog.getByRole('combobox').first().click()
    await page.getByRole('listbox').getByText('L', { exact: true }).click()
    await dialog.getByLabel('Juleika-Nummer').fill('99999')
    await dialog.getByLabel('Juleika-Nummer').blur()

    await expect(dialog.getByText('Juleika ungültig oder nicht gefunden.')).toBeVisible()
    await dialog.getByRole('button', { name: 'Speichern' }).click()

    await expect(page.getByText('Lea Leiter')).toBeVisible()
    expect(createdBody).toMatchObject({ lastName: 'Leiter', role: 'YOUTH_LEADER', juleikaNumber: '99999' })
    expect(createdBody?.juleikaExpireDate).toBeUndefined()
    expect(unmocked).toEqual([])
  })
})
