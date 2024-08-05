import { getData, postData, putData, deleteData } from '../helper/fetch'
import { withAuthenticationHeader, getTokenData } from './authentication'
import type { TShirtSize } from '@/services/tShirtSizes'

export const getAttendees = () => getData<Attendee[]>('attendees', withAuthenticationHeader())

export const getAttendeesForDepartment = (departmentId: number) =>
  getData<Attendee[]>(`departments/${departmentId}/attendees`, withAuthenticationHeader())

export const getAttendee = (id: number) =>
  getData<Attendee>(`attendees/${id}`, withAuthenticationHeader())

export const createAttendee = (attendee: NewAttendee) =>
  postData<Attendee>('attendees', withAuthenticationHeader(), attendee)

export const updateAttendee = (attendee: Attendee) =>
  putData<Attendee>(`attendees/${attendee.id}`, withAuthenticationHeader(), attendee)

export const deleteAttendee = (id: string) =>
  deleteData(`attendees/${id}`, withAuthenticationHeader())

export enum AttendeeRole {
  YOUTH = 'YOUTH',
  YOUTH_LEADER = 'YOUTH_LEADER'
}

export enum Food {
  MEAT = 'MEAT',
  NONE = 'NONE',
  SPECIAL = 'SPECIAL',
  VEGETARIAN = 'VEGETARIAN',
  MUSLIM = 'MUSLIM'
}

export enum AttendeeStatus {
  ENTERED = 'ENTERED',
  LEFT = 'LEFT'
}

export interface NewAttendee {
  firstName: string
  lastName: string
  departmentId: number
  birthday: string
  food: Food
  tShirtSize: string
  additionalInformation: string
  role: AttendeeRole
}

export interface Attendee extends NewAttendee {
  id: string
  status: AttendeeStatus | null
}
