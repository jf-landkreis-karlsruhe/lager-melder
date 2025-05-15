import { fetchData, getData } from '../helper/fetch'
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
      fileName: `Betreuer-Landesjugendplan-${group}-${departmentName}.pdf`
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
      fileName: `Teilnehmer-Jugendamt-${group}-${departmentName}.pdf`
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
      fileName: `Teilnehmer-Landesjugendplan-${group}-${departmentName}.pdf`
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
      fileName: `Teilnehmerliste-Kommandant-${departmentName}.pdf`
    }))
}

export const getSubsidy = (departmentId: number) =>
  getData<Subsidy>(`departments/${departmentId}/subsidy`, withAuthenticationHeader())

export interface Subsidy {
  id: number
  participants: SubsidyDistribution
  childGroup: SubsidyDistribution
}

interface SubsidyDistribution {
  stateYouthPlanLeaders: number
  stateYouthPlanParticipants: number
  karlsruheLeaders: number
  karlsruheParticipants: number
}

export enum Group {
  PARTICIPANT = 'teilnehmer',
  CHILD_GROUP = 'kindergruppe'
}
