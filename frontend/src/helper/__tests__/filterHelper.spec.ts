import { describe, expect, it } from 'vitest'
import { filterByDepartmentAndSearch, filterEnteredAttendees } from '../filterHelper'
import { type Attendee, AttendeeRole, AttendeeStatus, Food } from '@/services/attendee'

const attendee = (overrides: Partial<Attendee>): Attendee => ({
  id: '1',
  firstName: 'Max',
  lastName: 'Mustermann',
  departmentId: 1,
  birthday: '2010-01-01',
  food: Food.MEAT,
  tShirtSize: 'M',
  additionalInformation: '',
  role: AttendeeRole.YOUTH,
  code: 'abcdefgh',
  juleikaNumber: '',
  juleikaExpireDate: '',
  partOfDepartmentId: undefined,
  helperDays: [],
  status: undefined,
  ...overrides
})

describe('filterByDepartmentAndSearch', () => {
  const attendees = [
    attendee({ id: '1', firstName: 'Anna', departmentId: 1 }),
    attendee({ id: '2', firstName: 'Ben', departmentId: 1, additionalInformation: 'Allergie' }),
    attendee({ id: '3', firstName: 'Anna', departmentId: 2 })
  ]

  it('returns all attendees of the department without search', () => {
    expect(filterByDepartmentAndSearch(attendees, 1, '').map((a) => a.id)).toEqual(['1', '2'])
  })

  it('searches first name, last name and additional information', () => {
    expect(filterByDepartmentAndSearch(attendees, 1, 'Anna').map((a) => a.id)).toEqual(['1'])
    expect(filterByDepartmentAndSearch(attendees, 1, 'Allergie').map((a) => a.id)).toEqual(['2'])
    expect(filterByDepartmentAndSearch(attendees, 1, 'Muster').map((a) => a.id)).toEqual(['1', '2'])
  })

  it('search is case sensitive', () => {
    expect(filterByDepartmentAndSearch(attendees, 1, 'anna')).toEqual([])
  })
})

describe('filterEnteredAttendees', () => {
  it('only keeps entered attendees', () => {
    expect(filterEnteredAttendees(attendee({ status: AttendeeStatus.ENTERED }))).toBe(true)
    expect(filterEnteredAttendees(attendee({ status: AttendeeStatus.LEFT }))).toBe(false)
    expect(filterEnteredAttendees(attendee({ status: null }))).toBe(false)
  })
})
