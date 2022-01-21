import { fetchData } from "../helper/fetch";
import { withAuthenticationHeader } from "./authentication";
import { FileReponse } from "./filesHelper";

export const getYouthPlan = (
  departmentId: string,
  departmentName: string
): Promise<FileReponse> => {
  return fetchData(`registrationFiles/youthPlan/${departmentId}`, {
    headers: {
      ...withAuthenticationHeader(),
    },
  })
    .then((r) => r.blob())
    .then((blob) => ({
      data: blob,
      fileName: `paedagogischeBetreuer-${departmentName}.pdf`,
    }));
};

export const getAttendeesKarlsruhe = (
  departmentId: string,
  departmentName: string
): Promise<FileReponse> => {
  return fetchData(`registrationFiles/attendeesKarlsruhe/${departmentId}`, {
    headers: {
      ...withAuthenticationHeader(),
    },
  })
    .then((r) => r.blob())
    .then((blob) => ({
      data: blob,
      fileName: `teilnehmerlisteKarlsruhe-${departmentName}.pdf`,
    }));
};

export const getAttendeesBW = (
  departmentId: string,
  departmentName: string
): Promise<FileReponse> => {
  return fetchData(`registrationFiles/attendeesBW/${departmentId}`, {
    headers: {
      ...withAuthenticationHeader(),
    },
  })
    .then((r) => r.blob())
    .then((blob) => ({
      data: blob,
      fileName: `teilnehmerlisteBW-${departmentName}.pdf`,
    }));
};

export const getAttendeesCommunal = (
  departmentId: string,
  departmentName: string
): Promise<FileReponse> => {
  return fetchData(`registrationFiles/attendeesCommunal/${departmentId}`, {
    headers: {
      ...withAuthenticationHeader(),
    },
  })
    .then((r) => r.blob())
    .then((blob) => ({
      data: blob,
      fileName: `teilnehmerlisteKommandant-${departmentName}.pdf`,
    }));
};
