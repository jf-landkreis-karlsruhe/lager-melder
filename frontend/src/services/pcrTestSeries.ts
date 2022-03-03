import { getData, postData, deleteData, putData } from "../helper/fetch";
import { withAuthenticationHeader } from "./authentication";

export interface PcrTestSeriesResponse {
  id: string;
  name: string;
  start: string;
  end: string;
  testCodes: string[];
}

export interface NewPcrTestSeries
  extends Omit<PcrTestSeriesResponse, "start" | "end" | "id"> {
  start: Date;
  end: Date;
}

export interface PcrTestSeries
  extends Omit<PcrTestSeriesResponse, "start" | "end"> {
  start: Date;
  end: Date;
}

export const getPcrPoolSeries = (id: string): Promise<PcrTestSeries> => {
  return getData<PcrTestSeriesResponse>(
    `pcr-test-series/${id}`,
    withAuthenticationHeader()
  ).then(convertDates);
};

export const getAllPcrPoolSeries = (): Promise<PcrTestSeries[]> => {
  return getData<PcrTestSeriesResponse[]>(
    `pcr-test-series/`,
    withAuthenticationHeader()
  ).then((pcrTestSeries) => pcrTestSeries.map(convertDates));
};

export const createPcrPoolSeries = (
  newPcrPoolSeries: NewPcrTestSeries
): Promise<PcrTestSeries> => {
  return postData<PcrTestSeriesResponse>(
    `pcr-test-series`,
    withAuthenticationHeader(),
    newPcrPoolSeries
  ).then(convertDates);
};

export const updatePcrPoolSeries = (
  pcrPoolSeries: PcrTestSeries
): Promise<PcrTestSeries> => {
  return putData<PcrTestSeriesResponse>(
    `pcr-test-series/${pcrPoolSeries.id}`,
    withAuthenticationHeader(),
    pcrPoolSeries
  ).then(convertDates);
};

export const deletePcrPoolSeries = (id: string): Promise<Response> => {
  return deleteData(`pcr-test-series/${id}`, withAuthenticationHeader());
};

const convertDates = (pcrTestSeries: PcrTestSeriesResponse): PcrTestSeries => {
  return {
    ...pcrTestSeries,
    start: new Date(pcrTestSeries.start),
    end: new Date(pcrTestSeries.end),
  };
};
