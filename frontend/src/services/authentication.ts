import { getData, postData, putData } from '../helper/fetch'

const TOKEN_STORAGE = 'access_token'

export enum Roles {
  UNAUTHORIZED = 'UNAUTHORIZED',
  USER = 'USER',
  LK_KARLSRUHE = 'LK_KARLSRUHE',
  SPECIALIZED_FIELD_DIRECTOR = 'SPECIALIZED_FIELD_DIRECTOR',
  ADMIN = 'ADMIN'
}

export function rolesText(role?: Roles): string {
  switch (role) {
    case Roles.ADMIN:
      return 'Admin'
    case Roles.USER:
      return 'Benutzer'
    case Roles.LK_KARLSRUHE:
      return 'LK Karlsruhe'
    case Roles.SPECIALIZED_FIELD_DIRECTOR:
      return 'Fachgebietsleiter'
    case Roles.UNAUTHORIZED:
      return 'Keine'
    default:
      return ''
  }
}

export interface JWT {
  sub: string
  role: Roles
  departmentId: number
  exp: number
}

export const AuthenticationChangedEvent = 'authenticationChanged'

export const login = async (username: string, password: string) => {
  return postData<AuthorizationResponse>(
    'login',
    {},
    {
      username,
      password
    }
  )
    .then((res) => res.Authorization)
    .then((jwt: any) => {
      saveJWT(jwt)
      return jwt
    })
    .then((response) => {
      const loginEvent = new CustomEvent(AuthenticationChangedEvent)
      window && window.dispatchEvent(loginEvent)
      return decodeJWT(response)
    })
}

export const renewToken = async () => {
  return getData<AuthorizationResponse>('authorization/renew-token', withAuthenticationHeader())
    .then((res) => res.Authorization)
    .then((jwt: any) => {
      saveJWT(jwt)
      return jwt
    })
    .then((response) => {
      const loginEvent = new CustomEvent(AuthenticationChangedEvent)
      window && window.dispatchEvent(loginEvent)
      return decodeJWT(response)
    })
}

export const forgotPassword = async (username: string) => {
  return putData<{}>(
    'users/forgotPasswordToken',
    {},
    {
      username,
      linkAddress: window.location.origin + '/passwort-zuruecksetzen'
    }
  )
}

export const resetPasswordWithToken = async (token: string, password: string) => {
  return putData<{}>(
    'users/resetPasswordWithToken',
    {},
    {
      token,
      password
    }
  )
}

export const logout = () => {
  localStorage.removeItem(TOKEN_STORAGE)
  const loggoutEvent = new CustomEvent(AuthenticationChangedEvent)
  window && window.dispatchEvent(loggoutEvent)
}

export const isLoggedIn = () => !!getToken()

export const getToken = () => {
  const jwtString = localStorage.getItem(TOKEN_STORAGE)
  if (!jwtString) {
    return undefined
  }
  const jwt = JSON.parse(jwtString)
  if (!jwt) {
    return undefined
  }
  const jwtData = decodeJWT(jwt)
  if (jwtData.exp * 1000 < new Date().getTime()) {
    return undefined
  }
  return jwt
}

export const hasLKKarlsruheRole = () =>
  [Roles.LK_KARLSRUHE, Roles.SPECIALIZED_FIELD_DIRECTOR, Roles.ADMIN].includes(getTokenData().role)
export const hasSpecializedFieldDirectorRole = () =>
  [Roles.ADMIN, Roles.SPECIALIZED_FIELD_DIRECTOR].includes(getTokenData().role)

export const getTokenData = () => {
  const token = getToken()
  return token && decodeJWT(token)
}

const decodeJWT = (jwt: string): JWT => {
  const base64Url = jwt.split('.')[1]
  const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
  const jsonPayload = decodeURIComponent(
    atob(base64)
      .split('')
      .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
      .join('')
  )
  return JSON.parse(jsonPayload)
}

const saveJWT = (jwt: JWT) => {
  localStorage.setItem(TOKEN_STORAGE, JSON.stringify(jwt))
}

export const withAuthenticationHeader = () => ({
  Authorization: getToken()
})

export interface AuthorizationResponse {
  Authorization: string
}
