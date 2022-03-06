import { toast } from "@/plugins/toastification";
import { getData, postData, deleteData, putData } from "../helper/fetch";
import { withAuthenticationHeader } from "./authentication";
import { ErrorResponse } from "./errorConstants";

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
  )
    .then(convertDates)
    .catch((e: ErrorResponse) => {
      toast.error(e.messages[0].message);
      throw e;
    });
};

export const getAllPcrPoolSeries = (): Promise<PcrTestSeries[]> => {
  return getData<PcrTestSeriesResponse[]>(
    `pcr-test-series/`,
    withAuthenticationHeader()
  )
    .then((pcrTestSeries) => pcrTestSeries.map(convertDates))
    .catch((e: ErrorResponse) => {
      toast.error(e.messages[0].message);
      throw e;
    });
};

export const createPcrPoolSeries = (
  newPcrPoolSeries: NewPcrTestSeries
): Promise<PcrTestSeries> => {
  return postData<PcrTestSeriesResponse>(
    `pcr-test-series`,
    withAuthenticationHeader(),
    newPcrPoolSeries
  )
    .then(convertDates)
    .catch((e: ErrorResponse) => {
      toast.error(e.messages[0].message);
      throw e;
    });
};

export const updatePcrPoolSeries = async (
  pcrPoolSeries: PcrTestSeries
): Promise<PcrTestSeries | undefined> => {
  const data = await putData<PcrTestSeriesResponse>(
    `pcr-test-series/${pcrPoolSeries.id}`,
    withAuthenticationHeader(),
    pcrPoolSeries
  )
    .then(convertDates)
    .catch((e: ErrorResponse) => {
      toast.error(
        e.messages[0].message ?? "PCR Serie konnte nicht aktualisiert werden."
      );
      throw e;
    });
  if (!data) return undefined;
  return data;
};

export const deletePcrPoolSeries = (id: string): Promise<Response> => {
  return deleteData(`pcr-test-series/${id}`, withAuthenticationHeader()).catch(
    (e: ErrorResponse) => {
      toast.error(e.messages[0].message);
      throw e;
    }
  );
};

const convertDates = (pcrTestSeries: PcrTestSeriesResponse): PcrTestSeries => {
  return {
    ...pcrTestSeries,
    start: new Date(pcrTestSeries.start),
    end: new Date(pcrTestSeries.end),
  };
};
