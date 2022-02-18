import { getData, postData, deleteData } from "../helper/fetch";
import { withAuthenticationHeader } from "./authentication";

export interface PcrTest {
  id: number;
  code: string;
  start: Date;
  end: Date;
  testedAttendees: PcrAttendee[];
}

export interface PcrAttendee {
  attendeeCode: string;
  testCode: string;
  departmentName: string;
  attendeeFirstName: string;
  attendeeLastName: string;
}

export const getPcrPool = (testCode: string): Promise<PcrTest> => {
  return getData(`pcr-tests/by-code/${testCode}`, withAuthenticationHeader());
};

export const addAttendeeToPcrPool = (
  testCode: string,
  attendeeCode: string
): Promise<PcrAttendee> => {
  // const attendeeResMock = {
  //   attendeeCode,
  //   testCode,
  //   attendeeFirstName: "dan",
  //   attendeeLastName: "theman",
  //   departmentName: "ettlingen",
  // };
  // return new Promise((res) => res(attendeeResMock));
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
    `pcr-tests/${poolCode}/${attendeeCode}`,
    withAuthenticationHeader()
  );
};
