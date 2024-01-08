import { createToastInterface } from 'vue-toastification'
import { BASE_URL } from '../assets/config'
import type { ErrorResponse } from '@/services/errorConstants'

const toast = createToastInterface()

export const getData = async <T>(relativeUrl: string, headers: HeadersInit) => {
  return fetchData(relativeUrl, {
    headers: {
      'Content-Type': 'application/json',
      ...headers
    }
  }).then((res) => res.json() as Promise<T>)
}

export const postData = async <T>(relativeUrl: string, headers: HeadersInit, body: object) => {
  return fetchData(relativeUrl, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      ...headers
    },
    body: JSON.stringify(body)
  }).then((res) => res.json() as Promise<T>)
}

export const putData = async <T>(relativeUrl: string, headers: HeadersInit, body: object) => {
  return fetchData(relativeUrl, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
      ...headers
    },
    body: JSON.stringify(body)
  }).then((res) => res.json() as Promise<T>)
}

export const deleteData = (relativeUrl: string, headers: HeadersInit) => {
  return fetchData(relativeUrl, {
    method: 'DELETE',
    headers: {
      'Content-Type': 'application/json',
      ...headers
    }
  })
}

export const fetchData = async (relativeUrl: string, config: RequestInit): Promise<Response> => {
  return fetch(`${BASE_URL}/${relativeUrl}`, config)
    .then((res) => {
      if (!res.ok) {
        throw res
      }
      return res
    })
    .catch(async (err: Response) => {
      const errObj = await err.json()
      if (isValidatedErrorResponse(errObj)) {
        toast.error(errObj.messages[0].message)
      } else if (errObj.path.endsWith('login') && errObj.status === 401) {
        toast.error('Benutzername oder Passwort sind falsch')
      } else {
        const httpRes: Response = errObj as Response
        const commonErrMsg = `Es ist leider etwas schief gegangen. (Code: ${httpRes.status})`
        toast.error(commonErrMsg)
      }
      throw errObj
    })
}

const isValidatedErrorResponse = (err: Record<string, any>): err is ErrorResponse => {
  return err.key && err.messages
}
