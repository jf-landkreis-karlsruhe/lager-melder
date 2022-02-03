import { getData, postData, deleteData, putData } from "../helper/fetch";
import { withAuthenticationHeader } from "./authentication";

export interface PcrTestSeriesRequest {
  name: string;
  start: Date;
  end: Date;
  testCodes: string[];
}

export interface PcrTestSeries extends PcrTestSeriesRequest {
  id: string;
}

export const getPcrPoolSeries = (id: string): Promise<PcrTestSeries> => {
  return getData(`pcr-tests-series/${id}`, withAuthenticationHeader());
};

export const getAllPcrPoolSeries = (): Promise<PcrTestSeries[]> => {
  return getData(`pcr-tests-series/`, withAuthenticationHeader());
};

export const createPcrPoolSeries = (
  newPcrPoolSeries: PcrTestSeriesRequest
): Promise<PcrTestSeries> => {
  return postData<PcrTestSeries>(
    `pcr-tests-series`,
    withAuthenticationHeader(),
    newPcrPoolSeries
  );
};

export const updatePcrPoolSeries = (
  pcrPoolSeries: PcrTestSeries
): Promise<PcrTestSeries> => {
  return putData<PcrTestSeries>(
    `pcr-tests-series/${pcrPoolSeries.id}`,
    withAuthenticationHeader(),
    pcrPoolSeries
  );
};

export const deletePcrPoolSeries = (id: string): Promise<Response> => {
  return deleteData(`pcr-tests-series/${id}}`, withAuthenticationHeader());
};
