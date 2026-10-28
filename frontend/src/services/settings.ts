import { endOfDay, startOfDay } from '@/helper/displayDate'
import { withAuthenticationHeader } from './authentication'
import { getData, putData } from '../helper/fetch'

export interface Settings {
  id: string
  registrationEnd: string
  childGroupsRegistrationEnd: string
  helpersRegistrationEnd: string
  hostCity: string
  eventStart: string
  eventEnd: string
  eventName: string
  eventAddress: string
  organizer: string
  organisationAddress: string // multiline
  moneyPerYouthLoader: string
  startDownloadRegistrationFiles: string
  numberOfDuties: number
}

// registration dates are days ("2026-07-05"), as needed by the date inputs
export const getSettings = () => getData<Settings>(`settings`, withAuthenticationHeader())

export interface RegistrationEnd<T = Date> {
  registrationEnd: T
  attendeesCanBeEdited: boolean
  childGroupsRegistrationEnd: T
  childGroupsCanBeEdited: boolean
  helpersRegistrationEnd: T
  helpersCanBeEdited: boolean
  eventEnd: T
}

export const getRegistrationEnd = () => {
  return getData<RegistrationEnd<string>>(`settings/registration-end`, withAuthenticationHeader()).then((settings) => {
    return {
      ...settings,
      registrationEnd: endOfDay(settings.registrationEnd),
      childGroupRegistrationEnd: endOfDay(settings.childGroupsRegistrationEnd),
      helpersRegistrationEnd: endOfDay(settings.helpersRegistrationEnd),
      eventEnd: new Date(settings.eventEnd)
    }
  })
}

export const updateSettings = (settings: Settings) =>
  putData<Settings>(`settings`, withAuthenticationHeader(), settings)

export interface StartDownloadRegistrationFiles<T = Date> {
  startDownloadRegistrationFiles: T
  registrationFilesCanBeDownloaded: boolean
}

export const getStartDownloadRegistrationFiles = () => {
  return getData<StartDownloadRegistrationFiles<string>>(
    `settings/start-download-registration-files`,
    withAuthenticationHeader()
  ).then((settings) => {
    return {
      ...settings,
      startDownloadRegistrationFiles: startOfDay(settings.startDownloadRegistrationFiles)
    }
  })
}
