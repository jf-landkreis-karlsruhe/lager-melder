import type { Attendee } from '@/services/attendee'

export interface AttendeeWithSelected extends Attendee {
  selected: boolean
}

export interface AttendeeGroup<T> {
  headline: string
  attendees: T
}
