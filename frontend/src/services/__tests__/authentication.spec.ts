import { afterEach, describe, expect, it } from 'vitest'
import {
  getToken,
  getTokenData,
  hasLKKarlsruheRole,
  hasSpecializedFieldDirectorRole,
  isLoggedIn,
  logout,
  Roles
} from '../authentication'

const TOKEN_STORAGE = 'access_token'

const createJwt = (payload: object) => {
  const encode = (value: object) => btoa(JSON.stringify(value)).replace(/=+$/, '')
  return `${encode({ alg: 'HS512' })}.${encode(payload)}.signature`
}

const storeToken = (role: Roles, expiresInSeconds: number) => {
  const jwt = createJwt({
    sub: 'user@feuerwehr.de',
    role,
    departmentId: 4,
    exp: Math.floor(Date.now() / 1000) + expiresInSeconds
  })
  // the frontend stores the raw token as JSON string
  localStorage.setItem(TOKEN_STORAGE, JSON.stringify(jwt))
  return jwt
}

describe('authentication', () => {
  afterEach(() => {
    localStorage.clear()
  })

  it('is logged in with a valid token', () => {
    const jwt = storeToken(Roles.USER, 60)

    expect(isLoggedIn()).toBe(true)
    expect(getToken()).toBe(jwt)
    expect(getTokenData()).toMatchObject({ sub: 'user@feuerwehr.de', role: Roles.USER, departmentId: 4 })
  })

  it('is logged out with an expired token', () => {
    storeToken(Roles.USER, -60)

    expect(isLoggedIn()).toBe(false)
    expect(getTokenData()).toBeUndefined()
  })

  it('is logged out after logout', () => {
    storeToken(Roles.USER, 60)

    logout()

    expect(isLoggedIn()).toBe(false)
  })

  it.each([
    [Roles.USER, false, false],
    [Roles.LK_KARLSRUHE, true, false],
    [Roles.SPECIALIZED_FIELD_DIRECTOR, true, true],
    [Roles.ADMIN, true, true]
  ])('role %s: LK Karlsruhe rights %s, specialized field director rights %s', (role, lkKarlsruhe, director) => {
    storeToken(role, 60)

    expect(hasLKKarlsruheRole()).toBe(lkKarlsruhe)
    expect(hasSpecializedFieldDirectorRole()).toBe(director)
  })
})
