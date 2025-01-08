import { deleteData, getData, postData, putData } from '../helper/fetch'
import { withAuthenticationHeader } from './authentication'

export const getAttendees = () => getData<Attendees>('attendees', withAuthenticationHeader())

export const getAttendeesForDepartment = (departmentId: number) =>
  getData<Attendees>(`departments/${departmentId}/attendees`, withAuthenticationHeader())

export const getAttendee = (id: number) => getData<Attendee>(`attendees/${id}`, withAuthenticationHeader())

export const createAttendee = (attendee: NewAttendee) =>
  postData<Attendee>('attendees', withAuthenticationHeader(), attendee)

export const updateAttendee = (attendee: Attendee) =>
  putData<Attendee>(`attendees/${attendee.id}`, withAuthenticationHeader(), attendee)

export const deleteAttendee = (id: string) => deleteData(`attendees/${id}`, withAuthenticationHeader())

// returns a role-based default attendee object for creating new attendees
export const getAttendeeDefault = (role: AttendeeRole, departmentId: number): Attendee => {
  return {
    id: 'newAttendee' + Date.now(),
    departmentId: departmentId,
    partOfDepartmentId: undefined,
    role,
    firstName: '',
    lastName: '',
    tShirtSize: '',
    helperDays: [],
    juleikaNumber: '',
    food: Food.MEAT,
    juleikaExpireDate: '',
    birthday: '',
    status: undefined,
    additionalInformation: '',
    code: ''
  }
}

export const getAttendeeTypeByRole = (role: AttendeeRole): keyof Attendees => {
  const map: Record<AttendeeRole, keyof Attendees> = {
    [AttendeeRole.YOUTH]: 'youths',
    [AttendeeRole.YOUTH_LEADER]: 'youthLeaders',
    [AttendeeRole.CHILD]: 'children',
    [AttendeeRole.CHILD_LEADER]: 'childLeaders',
    [AttendeeRole.Z_KID]: 'zKids',
    [AttendeeRole.HELPER]: 'helpers'
  }
  return map[role]
}

export enum AttendeeRole {
  YOUTH = 'YOUTH',
  YOUTH_LEADER = 'YOUTH_LEADER',
  CHILD = 'CHILD',
  CHILD_LEADER = 'CHILD_LEADER',
  Z_KID = 'Z_KID',
  HELPER = 'HELPER'
}

export enum Food {
  MEAT = 'MEAT',
  VEGETARIAN = 'VEGETARIAN',
  SPECIAL = 'SPECIAL',
  MUSLIM = 'MUSLIM',
  NONE = 'NONE'
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
  juleikaNumber: string
  juleikaExpireDate: string
  partOfDepartmentId: number | undefined
  helperDays: string[]
  code: string
}

export interface Attendee extends NewAttendee {
  id: string
  status: AttendeeStatus | null | undefined
}

export interface Youth extends Attendee {}

export interface YouthLeader extends Attendee {
  juleikaNumber: string
  juleikaExpireDate: string
}

export interface Child extends Attendee {}

export interface ChildLeader extends Attendee {
  juleikaNumber: string
  juleikaExpireDate: string
}

export interface ZKid extends Attendee {
  partOfDepartmentId: number
}

export interface Helper extends Attendee {
  helperDays: string[]
}

export interface Attendees {
  youths: Youth[]
  youthLeaders: YouthLeader[]
  children: Child[]
  childLeaders: ChildLeader[]
  zKids: ZKid[]
  helpers: Helper[]
}

export const defaultAttendees: Attendees = {
  youths: [],
  youthLeaders: [],
  children: [],
  childLeaders: [],
  zKids: [],
  helpers: []
}

export const getZeltagerIcon = (attendee: Attendee) => {
  if (attendee.status === AttendeeStatus.ENTERED) {
    return '⛺ '
  }
  if (attendee.status === AttendeeStatus.LEFT) {
    return '🏠 '
  }
  return ''
}

