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

export const createEvents = (event: NewEvent) =>
  postData<NewEvent>("events", withAuthenticationHeader(), event);

export const loginToEvent = (eventCode: string, attendeeCode: string) =>
  postData<{}>(
    `events/${eventCode}/${attendeeCode}`,
    withAuthenticationHeader(),
    {}
  );

export const updateEvent = (event: NewEvent, id: string) =>
  putData<NewEvent>(`events/${id}`, withAuthenticationHeader(), event);

export const deleteEvent = (id: string) =>
  deleteData(`events/${id}`, withAuthenticationHeader());
