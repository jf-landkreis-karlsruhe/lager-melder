import { fetchData } from '../helper/fetch'
import { withAuthenticationHeader } from './authentication'
import type { FileReponse } from './filesHelper'

export const getBatches = (): Promise<FileReponse> => {
  return fetchData(`planning-files/batches`, {
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

export const getBatchesOrderedByCreationDate = (): Promise<FileReponse> => {
  return fetchData(`planning-files/batches-ordered-by-creation-date`, {
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
    `planning-files/events?frontendBaseUrl=${encodeURI(`${window.location.origin}/events`)}`,
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
  return fetchData(`planning-files/additionalInformation`, {
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
  return fetchData(`planning-files/food`, {
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

export const getTentMarkingPDF = (): Promise<FileReponse> => {
  return fetchData(`planning-files/tentMarkings`, {
    headers: {
      ...withAuthenticationHeader()
    }
  })
    .then((r) => r.blob())
    .then((blob) => ({
      data: blob,
      fileName: `zeltmarkierungen.pdf`
    }))
}

export const getTShirtPDF = (): Promise<FileReponse> => {
  return fetchData(`planning-files/t-shirts`, {
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
  return fetchData(`planning-files/overviewForDepartment`, {
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

export const getContactOverview = (): Promise<FileReponse> => {
  return fetchData(`planning-files/contactOverview`, {
    headers: {
      ...withAuthenticationHeader()
    }
  })
    .then((r) => r.blob())
    .then((blob) => ({
      data: blob,
      fileName: 'kontaktdatenÜbersicht.pdf'
    }))
}

export const getdMissingJuleika = (): Promise<FileReponse> => {
  return fetchData(`planning-files/missing-juleika`, {
    headers: {
      ...withAuthenticationHeader()
    }
  })
    .then((r) => r.blob())
    .then((blob) => ({
      data: blob,
      fileName: 'fehlendeJuleika.pdf'
    }))
}

export const getTentsAndDutiesCsv = (): Promise<FileReponse> => {
  return fetchData(`planning-files/tents-and-duties-csv`, {
    headers: {
      ...withAuthenticationHeader()
    }
  })
    .then((r) => r.blob())
    .then((blob) => ({
      data: blob,
      fileName: 'zelt-und-dienste.csv'
    }))
}