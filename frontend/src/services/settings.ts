import { withAuthenticationHeader } from "./authentication";
import { putData, getData } from "../helper/fetch";

export interface Settings {
  id: string;
  registrationEnd: string;
  hostCity: string;
  eventStart: string;
  eventEnd: string;
  eventName: string;
  eventAddress: string;
  organizer: string;
  organisationAddress: string; // multiline
  moneyPerYouthLoader: string;
  startDownloadRegistrationFiles: string;
}

export const getSettings = () =>
  getData<Settings>(`settings`, withAuthenticationHeader()).then(
    (settings) => ({
      ...settings,
      registrationEnd: toDate(settings.registrationEnd),
      startDownloadRegistrationFiles: toDate(
        settings.startDownloadRegistrationFiles
      ),
    })
  );

export interface RegistrationEnd<T = Date> {
  registrationEnd: T;
  attendeesCanBeEdited: boolean;
  childGroupRegistrationEnd: T;
  childGroupsCanBeEdited: boolean;
}

export const getRegistrationEnd = () => {
  return getData<RegistrationEnd<string>>(
    `settings/registration-end`,
    withAuthenticationHeader()
  ).then((settings) => {
    return {
      ...settings,
      registrationEnd: new Date(settings.registrationEnd),
      childGroupRegistrationEnd: new Date(settings.childGroupRegistrationEnd),
    };
  });
};

export const updateSettings = (settings: Settings) => {
  const registrationEndDate = new Date(settings.registrationEnd);
  registrationEndDate.setHours(23);
  registrationEndDate.setMinutes(59);
  registrationEndDate.setSeconds(59);
  registrationEndDate.toISOString();
  return putData<Settings>(`settings`, withAuthenticationHeader(), {
    ...settings,
    registrationEnd: toInstantString(settings.registrationEnd, false),
    startDownloadRegistrationFiles: toInstantString(
      settings.startDownloadRegistrationFiles,
      true
    ),
  });
};

export interface StartDownloadRegistrationFiles<T = Date> {
  startDownloadRegistrationFiles: T;
  registrationFilesCanBeDownloaded: boolean;
}

export const getStartDownloadRegistrationFiles = () => {
  return getData<StartDownloadRegistrationFiles<string>>(
    `settings/start-download-registration-files`,
    withAuthenticationHeader()
  ).then((settings) => {
    return {
      ...settings,
      startDownloadRegistrationFiles: new Date(
        settings.startDownloadRegistrationFiles
      ),
    };
  });
};

const toInstantString = (dateString: string, toStartOfDay: boolean) => {
  const date = new Date(dateString);
  date.setHours(toStartOfDay ? 5 : 23);
  date.setMinutes(toStartOfDay ? 0 : 59);
  date.setSeconds(toStartOfDay ? 1 : 59);
  return date.toISOString();
};

const toDate = (dateString: string) => {
  return new Date(dateString).toISOString().split("T")[0];
};
