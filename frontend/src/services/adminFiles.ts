import { fetchData } from "../helper/fetch";
import { withAuthenticationHeader } from "./authentication";
import { FileReponse } from "./filesHelper";

export const getBatches = (): Promise<FileReponse> => {
  return fetchData(`admin-files/batches`, {
    headers: {
      ...withAuthenticationHeader()
    }
  })
    .then(r => r.blob())
    .then(blob => ({
      data: blob,
      fileName: `lagerausweise.pdf`
    }));
};

export const getEventCodes = (): Promise<FileReponse> => {
  return fetchData(
    `admin-files/events?frontendBaseUrl=${encodeURI(window.location.hostname)}`, {
    headers: {
      ...withAuthenticationHeader()
    }
  })
    .then(r => r.blob())
    .then(blob => ({
      data: blob,
      fileName: `events.pdf`
    }));
}
