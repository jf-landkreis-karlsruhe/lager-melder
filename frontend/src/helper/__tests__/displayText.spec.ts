import { describe, expect, it } from 'vitest'
import { dateAsText, foodText, helperDaysText } from '../displayText'
import { Food } from '@/services/attendee'

describe('dateAsText', () => {
  it('formats ISO dates as German date', () => {
    expect(dateAsText('2010-03-04')).toBe('04.03.2010')
  })

  it('returns empty text for empty input', () => {
    expect(dateAsText('')).toBe('')
  })
})

describe('foodText', () => {
  it('translates every food option', () => {
    expect(Object.values(Food).map(foodText)).toEqual(
      expect.arrayContaining(['Fleisch', 'Nichts', 'Sonderessen', 'Vegetarisch', 'Muslimisch'])
    )
  })
})

describe('helperDaysText', () => {
  const eventDays = [{ id: 'd1', name: 'Freitag' }] as Parameters<typeof helperDaysText>[1]

  it('returns the name of the event day', () => {
    expect(helperDaysText('d1', eventDays)).toBe('Freitag')
  })

  it('falls back for unknown days', () => {
    expect(helperDaysText('unknown', eventDays)).toBe('unbekannt')
  })
})
