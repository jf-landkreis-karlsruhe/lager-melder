import { fetchData, getData } from "../helper/fetch";
import { withAuthenticationHeader } from "./authentication";
import { FileReponse } from "./filesHelper";

export const getStateYouthPlanLeader = (
  departmentId: string,
  departmentName: string
): Promise<FileReponse> => {
  return fetchData(`registrationFiles/stateYouthPlanLeader/${departmentId}`, {
    headers: {
      ...withAuthenticationHeader(),
    },
  })
    .then((r) => r.blob())
    .then((blob) => ({
      data: blob,
      fileName: `betreuerBW-${departmentName}.pdf`,
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

export const getStateYouthPlanAttendees = (
  departmentId: string,
  departmentName: string
): Promise<FileReponse> => {
  return fetchData(`registrationFiles/stateYouthPlanAttendees/${departmentId}`, {
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

export interface YouthPlanDistribution {
  youthCount: number;
  leaderCount: number;
}
export const getYouthPlanDistribution = (): Promise<YouthPlanDistribution> => {
  return getData<YouthPlanDistribution>(`registrationFiles/youthPlanDistribution`, withAuthenticationHeader());
};
