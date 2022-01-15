import { getData, postData, putData, deleteData } from "../helper/fetch";
import { withAuthenticationHeader, getTokenData } from "./authentication";

export interface NewEvent {
  name: string;
}

export interface Event {
  name: string;
  id: string;
  code: string;
}

export const getEvents = (): Promise<Event[]> =>
  getData<Event[]>("events", withAuthenticationHeader());

export const getEventByCode = (eventCode: string): Promise<Event> =>
  getData<Event>(`events/by-code/${eventCode}`, withAuthenticationHeader());

export const createEvents = (event: NewEvent): Promise<Event> =>
  postData<Event>("events", withAuthenticationHeader(), event);

export const loginToEvent = (
  eventCode: string,
  attendeeCode: string
): Promise<{}> =>
  postData<{}>(
    `events/by-code/${eventCode}/${attendeeCode}`,
    withAuthenticationHeader(),
    {}
  );

export const updateEvent = (event: Event) =>
  putData<Event>(`events/${event.id}`, withAuthenticationHeader(), event);

export const deleteEvent = (id: string): Promise<Response> =>
  deleteData(`events/${id}`, withAuthenticationHeader());
