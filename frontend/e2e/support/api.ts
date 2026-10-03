import type { Page, Route } from '@playwright/test'

export type Role = 'USER' | 'LK_KARLSRUHE' | 'SPECIALIZED_FIELD_DIRECTOR' | 'ADMIN'

export const department = {
  id: 2,
  name: 'Ettlingen',
  leaderName: 'Dana Leiterin',
  leaderEMail: 'jugendwart@ettlingen.de',
  phoneNumber: '0721 123456',
  shortName: 'ETT',
  phoneNumberKommandant: '',
  nameKommandant: '',
  features: ['YOUTH_GROUPS'],
  headDepartmentName: '',
  paused: false,
  tentMarkings: [],
  evacuationGroup: null
}

export const youth = {
  id: 'youth-1',
  firstName: 'Anna',
  lastName: 'Schmidt',
  departmentId: department.id,
  birthday: '2012-04-03',
  food: 'MEAT',
  tShirtSize: '152',
  additionalInformation: '',
  role: 'YOUTH',
  code: 'ANNA1234',
  status: null,
  juleikaNumber: null,
  juleikaExpireDate: null,
  partOfDepartmentId: null,
  helperDays: null
}

export const youthLeader = {
  ...youth,
  id: 'leader-1',
  firstName: 'Lukas',
  lastName: 'Becker',
  birthday: '1995-08-12',
  tShirtSize: 'L',
  role: 'YOUTH_LEADER',
  code: 'LUKAS123',
  juleikaNumber: '1234567',
  juleikaExpireDate: '2030-01-01'
}

/** Creates an unsigned JWT; the frontend only decodes the payload. */
export const createJwt = (role: Role, departmentId = department.id) => {
  const encode = (value: object) => Buffer.from(JSON.stringify(value)).toString('base64url')
  const payload = { sub: 'jugendwart@ettlingen.de', role, departmentId, exp: Math.floor(Date.now() / 1000) + 3600 }
  return `${encode({ alg: 'HS512', typ: 'JWT' })}.${encode(payload)}.signature`
}

/** Stores a token in localStorage before the app starts, like after a successful login. */
export const loginAs = async (page: Page, role: Role) => {
  const jwt = createJwt(role)
  await page.addInitScript((token) => localStorage.setItem('access_token', JSON.stringify(token)), jwt)
}

type Handler = (route: Route) => Promise<void> | void
export type ApiOverrides = Record<string, Handler | object>

const inDays = (days: number) => new Date(Date.now() + days * 24 * 60 * 60 * 1000).toISOString()

const defaultResponses = (): Record<string, object> => ({
  'GET settings/registration-end': {
    registrationEnd: inDays(30),
    attendeesCanBeEdited: true,
    childGroupsRegistrationEnd: inDays(30),
    childGroupsCanBeEdited: true,
    helpersRegistrationEnd: inDays(30),
    helpersCanBeEdited: true,
    eventEnd: '2027-07-10'
  },
  [`GET departments/${department.id}`]: department,
  [`GET departments/${department.id}/attendees`]: {
    youths: [youth],
    youthLeaders: [youthLeader],
    children: [],
    childLeaders: [],
    zKids: [],
    helpers: []
  },
  [`GET departments/${department.id}/tents`]: {
    id: 1,
    departmentId: department.id,
    sg200: 1,
    sg20: 0,
    sg30: 0,
    sg40: 0,
    sg50: 0
  },
  'GET departments/for-selecting': [{ id: department.id, name: department.name }],
  'GET tShirtSizes': ['140', '152', '164', 'S', 'M', 'L', 'XL'],
  'GET event-days': [{ id: 'day-1', name: 'Freitag', dayOfEvent: 1 }]
})

/**
 * Intercepts all calls to /api. Requests are matched by "METHOD path" (path without /api/ and query).
 * Unmocked requests answer 404 and are collected in the returned list, so tests can assert on them.
 */
export const mockApi = async (page: Page, overrides: ApiOverrides = {}) => {
  const responses: ApiOverrides = { ...defaultResponses(), ...overrides }
  const unmocked: string[] = []

  await page.route('**/api/**', async (route) => {
    const url = new URL(route.request().url())
    const path = url.pathname.replace(/^\/api\//, '')
    const key = `${route.request().method()} ${path}`
    const response = responses[key]

    if (typeof response === 'function') {
      return (response as Handler)(route)
    }
    if (response !== undefined) {
      return route.fulfill({ json: response })
    }
    unmocked.push(key)
    return route.fulfill({
      status: 404,
      json: { key: 'NOT_FOUND_ERROR', messages: [{ message: `not mocked: ${key}` }] }
    })
  })

  return { unmocked }
}
