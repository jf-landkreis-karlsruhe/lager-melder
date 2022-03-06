import { ErrorResponse } from "@/services/errorConstants";
import { BASE_URL } from "../assets/config";

export const getData = <T>(relativeUrl: string, headers: HeadersInit) => {
  return fetchData(relativeUrl, {
    headers: {
      "Content-Type": "application/json",
      ...headers,
    },
  }).then((res) => res.json() as Promise<T>);
};

export const postData = <T>(
  relativeUrl: string,
  headers: HeadersInit,
  body: object
) => {
  return fetchData(relativeUrl, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      ...headers,
    },
    body: JSON.stringify(body),
  }).then((res) => res.json() as Promise<T>);
};

export const putData = <T>(
  relativeUrl: string,
  headers: HeadersInit,
  body: object
) => {
  return fetchData(relativeUrl, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
      ...headers,
    },
    body: JSON.stringify(body),
  }).then((res) => res.json() as Promise<T>);
};

export const deleteData = (relativeUrl: string, headers: HeadersInit) => {
  return fetchData(relativeUrl, {
    method: "DELETE",
    headers: {
      "Content-Type": "application/json",
      ...headers,
    },
  });
};

export const fetchData = (relativeUrl: string, config: RequestInit) => {
  return fetch(`${BASE_URL}/${relativeUrl}`, config)
    .then((res) => {
      if (!res.ok) {
        throw res;
      }
      return res;
    })
    .catch(async (err: Response) => {
      const errJson = (await err.json()) as ErrorResponse;
      throw errJson;
    });
};
