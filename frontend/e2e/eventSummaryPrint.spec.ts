import { expect, test } from '@playwright/test'
import { department, loginAs, mockApi } from './support/api'

const red = { id: 'red', name: 'rot', color: '#de2424' }
const blue = { id: 'blue', name: 'blau', color: '#4a8fdc' }

const distribution = (name: string) => ({ name, youths: 3, youthLeaders: 1, children: 0, childLeaders: 0, helpers: 0 })

const departments = [
  { ...department, id: 1, name: 'Malsch', evacuationGroup: red, tentMarkings: [{ id: 't1', name: 'R1' }] },
  { ...department, id: 2, name: 'Ettlingen', evacuationGroup: blue, tentMarkings: [{ id: 't2', name: 'B1' }] },
  { ...department, id: 3, name: 'Bruchsal', evacuationGroup: null, tentMarkings: [] }
]

test.beforeEach(async ({ page }) => {
  await mockApi(page, {
    'GET departments': departments,
    'GET evacuation-groups': [red, blue],
    'GET events/global/summary': {
      total: distribution('Gesamt'),
      departments: departments.map((dep) => distribution(dep.name))
    }
  })
  await loginAs(page, 'LK_KARLSRUHE')
})

test('print view shows one section per evacuation group and leaves out departments without group', async ({ page }) => {
  await page.goto('/anwesend-print')

  const sections = page.locator('section.evacuation-group')
  await expect(sections).toHaveCount(2)
  await expect(sections.nth(0).getByRole('heading', { name: 'Evakuierungsgruppe: blau' })).toBeVisible()
  await expect(sections.nth(0)).toContainText('Ettlingen')
  await expect(sections.nth(0)).toContainText('Zelte – Evakuierungsgruppe blau')
  await expect(sections.nth(1).getByRole('heading', { name: 'Evakuierungsgruppe: rot' })).toBeVisible()
  await expect(sections.nth(1)).toContainText('Malsch')
  await expect(page.getByRole('heading', { name: 'Bruchsal' })).toHaveCount(0)
})

test('every evacuation group but the first starts on a new page when printing', async ({ page }) => {
  await page.goto('/anwesend-print')
  await page.emulateMedia({ media: 'print' })

  const sections = page.locator('section.evacuation-group')
  await expect(sections).toHaveCount(2)
  await expect(sections.nth(0)).not.toHaveCSS('break-before', 'page')
  await expect(sections.nth(1)).toHaveCSS('break-before', 'page')
})

test('tents have a border when printing', async ({ page }) => {
  await page.goto('/anwesend-print')
  await page.emulateMedia({ media: 'print' })

  await expect(page.locator('.tent-chip', { hasText: 'R1' })).toHaveCSS('border-top-width', '1px')
})

test('the layout does not cut off content when printing', async ({ page }) => {
  await page.goto('/anwesend-print')
  await page.emulateMedia({ media: 'print' })
  await expect(page.locator('section.evacuation-group')).toHaveCount(2)

  // A clipping container cuts off the last departments in the printout
  await expect(page.locator('.grid-container')).toHaveCSS('overflow', 'visible')
})

test('the layout does not add an empty page at the end when printing', async ({ page }) => {
  await page.goto('/anwesend-print')
  await page.emulateMedia({ media: 'print' })
  await expect(page.locator('section.evacuation-group')).toHaveCount(2)

  await expect(page.locator('body')).toHaveCSS('min-height', '0px')
  await expect(page.locator('.v-application__wrap')).toHaveCSS('min-height', 'auto')
})

test('attendee overview still shows departments without evacuation group', async ({ page }) => {
  await page.goto('/anwesend')

  await expect(page.getByText('Bruchsal')).toBeVisible()
})
