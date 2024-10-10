import { getData, postData, putData, deleteData } from '../helper/fetch'
import { withAuthenticationHeader } from './authentication'

export const getAttendees = () => getData<Attendees>('attendees', withAuthenticationHeader())

export const getAttendeesForDepartment = (departmentId: number) =>
  getData<Attendees>(`departments/${departmentId}/attendees`, withAuthenticationHeader())

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
  YOUTH_LEADER = 'YOUTH_LEADER',
  CHILD = 'CHILD',
  CHILD_LEADER = 'CHILD_LEADER',
  Z_KID = 'Z_KID'
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
  juleikaNumber: string
  juleikaExpireDate: string
  partOfDepartmentId: number
}

export interface Attendee extends NewAttendee {
  id: string
  status: AttendeeStatus | null
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

export interface ZKid extends ZKid {
  partOfDepartmentId: number
}

export interface Attendees {
  youths: Youth[]
  youthLeaders: YouthLeader[]
  children: Child[]
  childLeaders: ChildLeader[]
  zkids: ZKid[]
}

export const defaultAttendees: Attendees = {
  youths: [],
  youthLeaders: [],
  children: [],
  childLeaders: [],
  zKids: []
}
