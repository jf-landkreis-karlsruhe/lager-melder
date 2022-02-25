import { getData, postData, deleteData, putData } from "../helper/fetch";
import { withAuthenticationHeader } from "./authentication";

export interface PcrTestSeriesResponse {
  name: string;
  start: string;
  end: string;
  testCodes: string[];
}

export interface PcrTestSeries
  extends Omit<PcrTestSeriesResponse, "start" | "end"> {
  id: string;
  start: Date;
  end: Date;
}

export const getPcrPoolSeries = (id: string): Promise<PcrTestSeries> => {
  return getData(`pcr-test-series/${id}`, withAuthenticationHeader());
};

export const getAllPcrPoolSeries = (): Promise<PcrTestSeries[]> => {
  return getData(`pcr-test-series/`, withAuthenticationHeader());
};

export const createPcrPoolSeries = (
  newPcrPoolSeries: PcrTestSeriesResponse
): Promise<PcrTestSeries> => {
  return postData<PcrTestSeries>(
    `pcr-test-series`,
    withAuthenticationHeader(),
    newPcrPoolSeries
  );
};

export const updatePcrPoolSeries = (
  pcrPoolSeries: PcrTestSeries
): Promise<PcrTestSeries> => {
  return putData<PcrTestSeries>(
    `pcr-test-series/${pcrPoolSeries.id}`,
    withAuthenticationHeader(),
    pcrPoolSeries
  );
};

export const deletePcrPoolSeries = (id: string): Promise<Response> => {
  return deleteData(`pcr-test-series/${id}`, withAuthenticationHeader());
};
