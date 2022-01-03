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

export const getEvents = () =>
  getData<Event[]>("events", withAuthenticationHeader());

export const createEvent = (event: NewEvent) =>
  postData<Event>("events", withAuthenticationHeader(), event);

export const loginToEvent = (eventCode: string, attendeeCode: string) =>
  postData<{}>(
    `events/by-code/${eventCode}/${attendeeCode}`,
    withAuthenticationHeader(),
    {}
  );

export const updateEvent = (event: Event) =>
  putData<Event>(`events/${event.id}`, withAuthenticationHeader(), event);

export const deleteEvent = (id: string) =>
  deleteData(`events/${id}`, withAuthenticationHeader());
