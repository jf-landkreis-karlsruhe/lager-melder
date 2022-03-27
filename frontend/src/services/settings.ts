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
}

export const getSettings = () =>
  getData<Settings>(`settings`, withAuthenticationHeader()).then(
    (settings) => ({
      ...settings,
      registrationEnd: new Date(settings.registrationEnd)
        .toISOString()
        .split("T")[0],
    })
  );

export const getRegistrationEnd = () => {
  return getData<{ registrationEnd: string }>(
    `settings/registration-end`,
    withAuthenticationHeader()
  ).then((settings) => {
    return {
      registrationEnd: new Date(settings.registrationEnd).toISOString(),
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
    registrationEnd: registrationEndDate,
  });
};
