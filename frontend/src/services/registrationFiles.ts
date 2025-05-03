import { fetchData } from '../helper/fetch'
import { withAuthenticationHeader } from './authentication'
import type { FileReponse } from './filesHelper'

export const getStateYouthPlanLeader = (
  departmentId: number,
  departmentName: string,
  group: Group
): Promise<FileReponse> => {
  return fetchData(`registrationFiles/stateYouthPlanLeader/${departmentId}?group=${group}`, {
    headers: {
      ...withAuthenticationHeader()
    }
  })
    .then((r) => r.blob())
    .then((blob) => ({
      data: blob,
      fileName: `betreuerBW-${group}-${departmentName}.pdf`
    }))
}

export const getAttendeesKarlsruhe = (departmentId: number, departmentName: string, group: Group): Promise<FileReponse> => {
  return fetchData(`registrationFiles/attendeesKarlsruhe/${departmentId}?group=${group}`, {
    headers: {
      ...withAuthenticationHeader()
    }
  })
    .then((r) => r.blob())
    .then((blob) => ({
      data: blob,
      fileName: `teilnehmerlisteKarlsruhe-${group}-${departmentName}.pdf`
    }))
}

export const getStateYouthPlanAttendees = (
  departmentId: number,
  departmentName: string,
  group: Group
): Promise<FileReponse> => {
  return fetchData(`registrationFiles/stateYouthPlanAttendees/${departmentId}?group=${group}`, {
    headers: {
      ...withAuthenticationHeader()
    }
  })
    .then((r) => r.blob())
    .then((blob) => ({
      data: blob,
      fileName: `teilnehmerlisteBW-${group}-${departmentName}.pdf`
    }))
}

export const getAttendeesCommunal = (
  departmentId: number,
  departmentName: string
): Promise<FileReponse> => {
  return fetchData(`registrationFiles/attendeesCommunal/${departmentId}`, {
    headers: {
      ...withAuthenticationHeader()
    }
  })
    .then((r) => r.blob())
    .then((blob) => ({
      data: blob,
      fileName: `teilnehmerlisteKommandant-${departmentName}.pdf`
    }))
}

export enum Group {
  PARTICIPANT = 'teilnehmer',
  CHILD_GROUP = 'kindergruppe'
}
