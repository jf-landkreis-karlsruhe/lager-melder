import { getData, postData, putData, deleteData } from "../helper/fetch";
import { withAuthenticationHeader, getTokenData } from "./authentication";

export interface PcrAttendee {
  id: string;
  attendeeFirstName: string;
  attendeeLastName: string;
  pcrPoolId: string;
  time: string;
}

export const addAttendeeToPcrPool = (
  poolId: string,
  attendeeCode: string
): Promise<PcrAttendee> => {
  // todo: replace mock with fetch request
  const attendeeResMock = {
    id: attendeeCode,
    attendeeFirstName: "dan",
    attendeeLastName: "theman",
    pcrPoolId: poolId,
    time: "2022-01-22",
  };
  return new Promise((res) => res(attendeeResMock));

  // return postData<PcrAttendee>(
  //   `pcr-tests/${poolId}/${attendeeCode}`,
  //   withAuthenticationHeader(),
  //   {}
  // );
};

export const removeAttendeeFromPool = (
  poolId: string,
  attendeeCode: string
): Promise<PcrAttendee> => {
  const attendeeResMock = {
    id: attendeeCode,
    attendeeFirstName: "dan",
    attendeeLastName: "theman",
    pcrPoolId: poolId,
    time: "2022-01-22",
  };
  return new Promise((res) => res(attendeeResMock));
  // TODO: use fetch
  // deleteData(`pcr-tests/${poolId}/${attendeeCode}`, withAuthenticationHeader());
};
