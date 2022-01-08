import { withAuthenticationHeader } from "./authentication";
import { putData, getData } from "../helper/fetch";

export interface Settings {
  id: string;
  registrationEnd: string;
}

export const getSettings = () =>
  getData<Settings>(`settings`, withAuthenticationHeader()).then(settings => ({
    ...settings,
    registrationEnd: new Date(settings.registrationEnd).toISOString().split("T")[0]
  }));

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