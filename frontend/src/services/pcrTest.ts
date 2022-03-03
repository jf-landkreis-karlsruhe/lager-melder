import { getData, postData, deleteData } from "../helper/fetch";
import { withAuthenticationHeader } from "./authentication";

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

export const getPcrPool = async (testCode: string): Promise<PcrTest> => {
  const data = await getData<PcrTestResponse>(
    `pcr-tests/by-code/${testCode}`,
    withAuthenticationHeader()
  );
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
): Promise<PcrAttendee> => {
  return postData<PcrAttendee>(
    `pcr-tests/by-code/${testCode}/${attendeeCode}`,
    withAuthenticationHeader(),
    {}
  );
};

export const removeAttendeeFromPool = (
  poolCode: string,
  attendeeCode: string
): Promise<Response> => {
  return deleteData(
    `pcr-tests/by-code/${poolCode}/${attendeeCode}`,
    withAuthenticationHeader()
  );
};
