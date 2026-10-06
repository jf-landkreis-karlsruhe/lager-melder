import { describe, expect, it } from 'vitest'
import { isValidJuleikaExpireDate, isValidJuleikaNumber } from '../juleika'

describe('isValidJuleikaExpireDate', () => {
  const eventEnd = new Date('2026-07-10')

  it('is invalid without expire date', () => {
    expect(isValidJuleikaExpireDate(null, eventEnd)).toBe(false)
    expect(isValidJuleikaExpireDate('', eventEnd)).toBe(false)
  })

  it('is valid without event end', () => {
    expect(isValidJuleikaExpireDate('2020-01-01', null)).toBe(true)
  })

  it('must not expire before the end of the event', () => {
    expect(isValidJuleikaExpireDate('2026-07-10', eventEnd)).toBe(true)
    expect(isValidJuleikaExpireDate('2027-01-01', eventEnd)).toBe(true)
    expect(isValidJuleikaExpireDate('2026-07-09', eventEnd)).toBe(false)
  })
})

describe('isValidJuleikaNumber', () => {
  it('requires a non empty number', () => {
    expect(isValidJuleikaNumber('')).toBe(false)
    expect(isValidJuleikaNumber('12345')).toBe(true)
  })
})
