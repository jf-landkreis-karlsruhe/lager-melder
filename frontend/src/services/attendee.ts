import { getData, postData, putData, deleteData } from "../helper/fetch";
import { withAuthenticationHeader, getTokenData } from "./authentication";

export const getAttendees = () =>
  getData<Attendee[]>("attendees", withAuthenticationHeader());

export const getAttendeesForMyDepartment = () => {
  const departmentId = getTokenData().departmentId;
  return getAttendeesForDepartment(departmentId);
};

export const getAttendeesForDepartment = (departmentId: number) =>
  getData<Attendee[]>(
    `departments/${departmentId}/attendees`,
    withAuthenticationHeader()
  );

export const getAttendee = (id: number) =>
  getData<Attendee>(`attendees/${id}`, withAuthenticationHeader());

export const createAttendee = (attendee: NewAttendee) =>
  postData<Attendee>("attendees", withAuthenticationHeader(), attendee);

export const updateAttendee = (attendee: Attendee) =>
  putData<Attendee>(
    `attendees/${attendee.id}`,
    withAuthenticationHeader(),
    attendee
  );

export const deleteAttendee = (id: string) =>
  deleteData(`attendees/${id}`, withAuthenticationHeader());

export enum AttendeeRole {
  YOUTH = "YOUTH",
  YOUTH_LEADER = "YOUTH_LEADER",
}

export enum Food {
  MEAT = "MEAT",
  NONE = "NONE",
  ALLERGY = "ALLERGY",
  VEGETARIAN = "VEGETARIAN",
  VEGAN = "VEGAN",
  MUSLIM = "MUSLIM",
}

export enum TShirtSize {
  S104110 = "104/110",
  S116128 = "116/128",
  S140152 = "140/152",
  S164 = "164",
  S176 = "176",
  S = "S",
  M = "M",
  L = "L",
  XL = "XL",
  XXL = "XXL",
  X3L = "3XL",
  X4L = "4XL",
  X5L = "5XL",
}

export enum AttendeeStatus {
  ENTERED = "ENTERED",
  LEFT = "LEFT",
}

export interface NewAttendee {
  firstName: string;
  lastName: string;
  departmentId: string;
  birthday: string;
  food: Food;
  tShirtSize: TShirtSize;
  additionalInformation: string;
  role: AttendeeRole;
}

export interface Attendee extends NewAttendee {
  id: string;
  status: AttendeeStatus | null;
}
