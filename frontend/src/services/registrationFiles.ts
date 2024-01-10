import { fetchData } from '../helper/fetch'
import { withAuthenticationHeader } from './authentication'
import type { FileReponse } from './filesHelper'

export const getStateYouthPlanLeader = (
  departmentId: number,
  departmentName: string
): Promise<FileReponse> => {
  return fetchData(`registrationFiles/stateYouthPlanLeader/${departmentId}`, {
    headers: {
      ...withAuthenticationHeader()
    }
  })
    .then((r) => r.blob())
    .then((blob) => ({
      data: blob,
      fileName: `betreuerBW-${departmentName}.pdf`
    }))
}

export const getAttendeesKarlsruhe = (
  departmentId: number,
  departmentName: string
): Promise<FileReponse> => {
  return fetchData(`registrationFiles/attendeesKarlsruhe/${departmentId}`, {
    headers: {
      ...withAuthenticationHeader()
    }
  })
    .then((r) => r.blob())
    .then((blob) => ({
      data: blob,
      fileName: `teilnehmerlisteKarlsruhe-${departmentName}.pdf`
    }))
}

export const getStateYouthPlanAttendees = (
  departmentId: number,
  departmentName: string
): Promise<FileReponse> => {
  return fetchData(`registrationFiles/stateYouthPlanAttendees/${departmentId}`, {
    headers: {
      ...withAuthenticationHeader()
    }
  })
    .then((r) => r.blob())
    .then((blob) => ({
      data: blob,
      fileName: `teilnehmerlisteBW-${departmentName}.pdf`
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
