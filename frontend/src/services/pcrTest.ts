import { getData, postData, deleteData } from "../helper/fetch";
import { withAuthenticationHeader } from "./authentication";
import { toast } from "@/plugins/toastification";
import { ErrorMessage, ErrorResponse } from "./errorConstants";

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
  ).catch((e: ErrorResponse) => {
    throw e;
  });
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
  )
    .then((attendeeRes) => {
      toast.success(
        `${attendeeRes.attendeeFirstName} ${attendeeRes.attendeeLastName} wurde erfolgreich hinzugefügt.`
      );
      return attendeeRes;
    })
    .catch((e: ErrorResponse) => {
      toast.error(
        e.messages[0].message ??
          "Teilnehmer konnte nicht dem PCR Test zugeordnet werden."
      );
      throw e;
    });
};

export const removeAttendeeFromPool = (
  poolCode: string,
  attendeeCode: string
): Promise<Response | undefined> => {
  return deleteData(
    `pcr-tests/by-code/${poolCode}/${attendeeCode}`,
    withAuthenticationHeader()
  ).catch((e: ErrorResponse) => {
    toast.error(
      e.messages[0].message ??
        "Teilnehmer konnte nicht von PCR Test entfernt werden."
    );
    throw e;
  });
};
