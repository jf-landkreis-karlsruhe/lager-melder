import { deleteData, getData, postData, putData } from '../helper/fetch'
import { withAuthenticationHeader } from './authentication'

export interface NewEvent {
  name: string
}

export enum EventType {
  GLOBAL_ENTER = 'GlobalEnter',
  GLOBAL_LEAVE = 'GlobalLeave',
  LOCATION = 'Location'
}

export interface Event {
  name: string
  id: string
  code: string
  type: EventType
}

export interface AttendeeResponse {
  attendeeFirstName: string
  attendeeLastName: string
  eventName: string
  time: string
}

export const getEvents = (): Promise<Event[]> => getData<Event[]>('events', withAuthenticationHeader())

export const getEventByCode = (eventCode: string): Promise<Event> =>
  getData<Event>(`events/by-code/${eventCode}`, withAuthenticationHeader())

export const createEvent = (event: NewEvent): Promise<Event> =>
  postData<Event>('events', withAuthenticationHeader(), event)

export const loginToEvent = async (eventCode: string, attendeeCode: string): Promise<AttendeeResponse> => {
  const result = await postData<AttendeeResponse>(
    `events/by-code/${eventCode}/${attendeeCode}`,
    withAuthenticationHeader(),
    {}
  )
  return result
}

export const updateEvent = (event: Event) => putData<Event>(`events/${event.id}`, withAuthenticationHeader(), event)

export const deleteEvent = (id: string): Promise<Response> => deleteData(`events/${id}`, withAuthenticationHeader())

export const batchEnterAttendees = (eventCode: string, attendeeCodes: string[]) =>
  postData<AttendeeResponse[]>(`events/batch-enter/${eventCode}`, withAuthenticationHeader(), {
    attendeeCodes: attendeeCodes
  })

export interface GlobalEventSummary {
  total: Distribution
  departments: Distribution[]
}

export interface Distribution {
  name: string
  youths: number
  youthLeaders: number
  children: number
  childLeaders: number
  helpers: number
}

export const globalEventSummary = () => getData<GlobalEventSummary>('events/global/summary', withAuthenticationHeader())

export const checkinDepartmentToEvent = (event: Event, departmentId: number) => {
  return postData<AttendeeResponse[]>(
    `events/add-department/${event.code}/${departmentId}`,
    withAuthenticationHeader(),
    {}
  )
}

export const getEventByType = (type: EventType) => getData<Event>(`events/by-type/${type}`, withAuthenticationHeader())
