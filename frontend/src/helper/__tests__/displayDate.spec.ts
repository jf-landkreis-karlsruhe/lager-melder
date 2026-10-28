import { describe, expect, it } from 'vitest'
import { dateTimeLocalized, endOfDay, startOfDay } from '../displayDate'

describe('startOfDay', () => {
  it('parses a day as local midnight, not as UTC', () => {
    const date = startOfDay('2026-07-05')

    expect([date.getFullYear(), date.getMonth(), date.getDate(), date.getHours(), date.getMinutes()]).toEqual([
      2026, 6, 5, 0, 0
    ])
  })
})

describe('endOfDay', () => {
  it('is the last minute of the day', () => {
    expect(dateTimeLocalized(endOfDay('2026-07-05'))).toBe('Sonntag, 05.07.2026, 23:59')
  })

  it('is the last minute of the day in winter time', () => {
    expect(dateTimeLocalized(endOfDay('2026-01-15'))).toBe('Donnerstag, 15.01.2026, 23:59')
  })

  it('is the last minute of the day when the clocks change', () => {
    expect(dateTimeLocalized(endOfDay('2026-03-29'))).toBe('Sonntag, 29.03.2026, 23:59')
    expect(dateTimeLocalized(endOfDay('2026-10-25'))).toBe('Sonntag, 25.10.2026, 23:59')
  })
})
