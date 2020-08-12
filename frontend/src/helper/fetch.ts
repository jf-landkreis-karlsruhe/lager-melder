import { BASE_URL } from "../assets/config";

export const getData = (relativeUrl: string, headers: HeadersInit) => {
  return fetchData(relativeUrl, {
    headers: {
      "Content-Type": "application/json",
      ...headers
    }
  }).then(res => res.json());
};

export const postData = (
  relativeUrl: string,
  headers: HeadersInit,
  body: object
) => postDataRaw(relativeUrl, headers, body).then(res => res.json());

export const postDataRaw = (
  relativeUrl: string,
  headers: HeadersInit,
  body: object
) => {
  return fetchData(relativeUrl, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      ...headers
    },
    body: JSON.stringify(body)
  });
};

export const putData = (
  relativeUrl: string,
  headers: HeadersInit,
  body: object
) => {
  return fetchData(relativeUrl, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      ...headers
    },
    body: JSON.stringify(body)
  }).then(res => res.json());
};

export const delteData = (relativeUrl: string, headers: HeadersInit) => {
  return fetchData(relativeUrl, {
    method: "DELETE",
    headers: {
      "Content-Type": "application/json",
      ...headers
    }
  });
};

const fetchData = (relativeUrl: string, config: RequestInit) => {
  return fetch(`${BASE_URL}/${relativeUrl}`, config).then(res => {
    if (!res.ok) {
      throw res;
    }
    return res;
  });
};
