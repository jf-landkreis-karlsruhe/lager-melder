import { postData } from '../helper/fetch'
import { withAuthenticationHeader } from './authentication'

export interface NewEvent {
  name: string
}

export enum SentTo {
  ALL_DEPARTMENTS = 'ALL_DEPARTMENTS',
  DEPARTMENTS_WITH_ATTENDEES = 'DEPARTMENTS_WITH_ATTENDEES',
  DEPARTMENTS_WITHOUT_ATTENDEES = 'DEPARTMENTS_WITHOUT_ATTENDEES'
}

export const sentToReadable = (sentTo: SentTo): string => {
  switch (sentTo) {
    case SentTo.ALL_DEPARTMENTS:
      return 'Alle Feuerwehren'
    case SentTo.DEPARTMENTS_WITHOUT_ATTENDEES:
      return 'Feuerwehren ohne Teilnehmern'
    case SentTo.DEPARTMENTS_WITH_ATTENDEES:
      return 'Feuerwehren mit Teilnehmern'
    default:
      return 'Übersetzung SentTo fehlerhaft'
  }
}

export interface SentMailRequest {
  sendTo: SentTo
}

export interface SentMailResponse {
  sendMails: number
}

export const sendReminderMail = (data: SentMailRequest): Promise<SentMailResponse> =>
  postData<SentMailResponse>('mail/reminder', withAuthenticationHeader(), data)

export const sendRegistrationFinishedMail = (data: SentMailRequest): Promise<SentMailResponse> =>
  postData<SentMailResponse>('mail/registration-finished', withAuthenticationHeader(), data)
