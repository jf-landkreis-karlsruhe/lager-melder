import { getData, postData, deleteData } from "../helper/fetch";
import { withAuthenticationHeader } from "./authentication";
import { toast } from "@/plugins/toastification";

export interface PcrTestResponse {
  id: number;
  code: string;
  start: string;
  end: string;
  testedAttendees: PcrAttendee[];
}

export interface PcrTest extends Omit<PcrTestResponse, "start" | "end"> {
  start: Date;
  end: Date;
}

export interface PcrAttendee {
  attendeeCode: string;
  testCode: string;
  departmentName: string;
  attendeeFirstName: string;
  attendeeLastName: string;
}

export const getPcrPool = async (
  testCode: string
): Promise<PcrTest | undefined> => {
  const data = await getData<PcrTestResponse>(
    `pcr-tests/by-code/${testCode}`,
    withAuthenticationHeader()
  );
  if (!data) return undefined;
  const pcrTest = {
    ...data,
    start: new Date(data.start),
    end: new Date(data.end),
  };
  return pcrTest;
};

export const addAttendeeToPcrPool = (
  testCode: string,
  attendeeCode: string
): Promise<PcrAttendee | undefined> => {
  return postData<PcrAttendee>(
    `pcr-tests/by-code/${testCode}/${attendeeCode}`,
    withAuthenticationHeader(),
    {}
  ).then((attendeeRes) => {
    toast.success(
      `${attendeeRes.attendeeFirstName} ${attendeeRes.attendeeLastName} wurde erfolgreich zum Test hinzugefügt.`
    );
    return attendeeRes;
  });
};

export const removeAttendeeFromPool = (
  poolCode: string,
  attendeeCode: string
): Promise<Response | undefined> => {
  return deleteData(
    `pcr-tests/by-code/${poolCode}/${attendeeCode}`,
    withAuthenticationHeader()
  );
};
