import { afterEach, describe, expect, it, vi } from 'vitest'
import { fetchData, getErrorMessage } from '../fetch'

const jsonResponse = (status: number, body: unknown) =>
  new Response(JSON.stringify(body), { status, headers: { 'Content-Type': 'application/json' } })

describe('fetchData', () => {
  afterEach(() => {
    vi.unstubAllGlobals()
  })

  it('throws the response when the status is not ok', async () => {
    const response = jsonResponse(404, {})
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue(response))

    await expect(fetchData('attendees', {})).rejects.toBe(response)
  })

  it('returns the response when the status is ok', async () => {
    const response = jsonResponse(200, { id: 1 })
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue(response))

    await expect(fetchData('attendees', {})).resolves.toBe(response)
  })
})

describe('getErrorMessage', () => {
  it('uses the messages of a backend error response', async () => {
    const error = jsonResponse(400, {
      key: 'UNIQUE_ERROR',
      messages: [{ message: 'erste' }, { message: 'zweite' }]
    })

    expect(await getErrorMessage(error)).toEqual({ key: 'UNIQUE_ERROR', message: 'erste\nzweite' })
  })

  it('explains a failed login', async () => {
    const error = jsonResponse(401, { path: '/api/login', status: 401 })

    expect((await getErrorMessage(error)).message).toBe('Benutzername oder Passwort sind falsch')
  })

  it('uses the default message for unknown errors', async () => {
    const error = new Response('not json', { status: 500 })

    expect(await getErrorMessage(error, 'Speichern fehlgeschlagen')).toEqual({
      key: undefined,
      message: 'Speichern fehlgeschlagen'
    })
  })
})
