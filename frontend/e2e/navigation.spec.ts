import { expect, test } from '@playwright/test'
import { loginAs, mockApi, type Role } from './support/api'

const adminLinks = ['Feuerwehren', 'Planung', 'Anwesend', 'Einstellungen']

const visibleLinks: Record<Role, string[]> = {
  USER: [],
  LK_KARLSRUHE: ['Planung', 'Anwesend'],
  SPECIALIZED_FIELD_DIRECTOR: adminLinks,
  ADMIN: adminLinks
}

for (const [role, links] of Object.entries(visibleLinks) as [Role, string[]][]) {
  test(`${role} sees only the allowed admin navigation`, async ({ page }) => {
    await mockApi(page)
    await loginAs(page, role)

    await page.goto('/teilnehmer')
    const navigation = page.getByRole('navigation')
    await expect(navigation.getByRole('link', { name: 'Anmeldeunterlagen' })).toBeVisible()

    for (const link of adminLinks) {
      await expect(navigation.getByRole('link', { name: link, exact: true })).toHaveCount(links.includes(link) ? 1 : 0)
    }
  })
}
