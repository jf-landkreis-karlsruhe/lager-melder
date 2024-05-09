import {createToastInterface, type ToastInterface} from 'vue-toastification'
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
}

export const getErrorMessage = async (err: Response, defaultMessage?: string): Promise<{
  key: string | undefined,
  message: string
}> => {
  const errObj = await err.json()
  if (isValidatedErrorResponse(errObj)) {
    return {
      key: errObj.key,
      message: errObj.messages.map(m => m.message).join('\n')
    }
  }
  if (errObj.path.endsWith('login') && errObj.status === 401) {
    return {
      key: errObj.key,
      message: 'Benutzername oder Passwort sind falsch'
    }
  }
  if (defaultMessage) {
    return {
      key: undefined,
      message: defaultMessage
    }
  }
  const httpRes: Response = errObj as Response
  return {
    key: undefined,
    message: `Es ist leider etwas schief gegangen. (Code: ${httpRes.status})`
  }
}

export const showErrorToast = async (toast: ToastInterface, error: Response, defaultMessage?: string) => {
  const err = await getErrorMessage(error, defaultMessage)
  toast.error(err.message)
}

const isValidatedErrorResponse = (err: Record<string, any>): err is ErrorResponse => {
  return err.key && err.messages
}
