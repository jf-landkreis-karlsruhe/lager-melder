import { fetchData } from '../helper/fetch'
import { withAuthenticationHeader } from './authentication'
import type { FileReponse } from './filesHelper'

export const getBatches = (): Promise<FileReponse> => {
  return fetchData(`admin-files/batches`, {
    headers: {
      ...withAuthenticationHeader()
    }
  })
    .then((r) => r.blob())
    .then((blob) => ({
      data: blob,
      fileName: `lagerausweise.pdf`
    }))
}

export const getEventCodes = (): Promise<FileReponse> => {
  return fetchData(
    `admin-files/events?frontendBaseUrl=${encodeURI(`${window.location.origin}/events`)}`,
    {
      headers: {
        ...withAuthenticationHeader()
      }
    }
  )
    .then((r) => r.blob())
    .then((blob) => ({
      data: blob,
      fileName: `events.pdf`
    }))
}

export const getAdditionalInformationPDF = (): Promise<FileReponse> => {
  return fetchData(`admin-files/additionalInformation`, {
    headers: {
      ...withAuthenticationHeader()
    }
  })
    .then((r) => r.blob())
    .then((blob) => ({
      data: blob,
      fileName: `anmerkungen.pdf`
    }))
}

export const getFoodPDF = (): Promise<FileReponse> => {
  return fetchData(`admin-files/food`, {
    headers: {
      ...withAuthenticationHeader()
    }
  })
    .then((r) => r.blob())
    .then((blob) => ({
      data: blob,
      fileName: `essen.pdf`
    }))
}

export const getTShirtPDF = (): Promise<FileReponse> => {
  return fetchData(`admin-files/t-shirts`, {
    headers: {
      ...withAuthenticationHeader()
    }
  })
    .then((r) => r.blob())
    .then((blob) => ({
      data: blob,
      fileName: `tshirts.pdf`
    }))
}

export const getDepartmentOverview = (): Promise<FileReponse> => {
  return fetchData(`admin-files/overviewForDepartment`, {
    headers: {
      ...withAuthenticationHeader()
    }
  })
    .then((r) => r.blob())
    .then((blob) => ({
      data: blob,
      fileName: 'übersichtÜberFeuerwehr.pdf'
    }))
}
