import { postData } from "../helper/fetch";

const TOKEN_STORAGE = "access_token";
export interface JWT {
  sub: string;
  role: string;
  departmentId: number;
  exp: number;
}

export const AuthenticationChangedEvent = "authenticationChanged";

export const login = (username: string, password: string) => {
  return postData<AuthorizationResponse>(
    "login",
    {},
    {
      username,
      password
    }
  )
    .then(res => res.Authorization)
    .then((jwt: any) => {
      saveJWT(jwt);
      return jwt;
    })
    .then(response => {
      const loggoutEvent = new CustomEvent(AuthenticationChangedEvent);
      window && window.dispatchEvent(loggoutEvent);
      return decodeJWT(response);
    });
};

export const logout = () => {
  localStorage.removeItem(TOKEN_STORAGE);
  const loggoutEvent = new CustomEvent(AuthenticationChangedEvent);
  window && window.dispatchEvent(loggoutEvent);
};

export const isLoggedIn = () => !!getToken();

export const getToken = () => {
  const jwtString = localStorage.getItem(TOKEN_STORAGE);
  if (!jwtString) {
    return undefined;
  }
  const jwt = JSON.parse(jwtString);
  if (!jwt) {
    return undefined;
  }
  const jwtData = decodeJWT(jwt);
  if (jwtData.exp * 1000 < new Date().getTime()) {
    return undefined;
  }
  return jwt;
};

export const getTokenData = () => {
  const token = getToken();
  return token && decodeJWT(token);
};

const decodeJWT = (jwt: string): JWT => {
  const base64Url = jwt.split(".")[1];
  const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
  const jsonPayload = decodeURIComponent(
    atob(base64)
      .split("")
      .map(c => "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2))
      .join("")
  );
  return JSON.parse(jsonPayload);
};

const saveJWT = (jwt: JWT) => {
  localStorage.setItem(TOKEN_STORAGE, JSON.stringify(jwt));
};

export const withAuthenticationHeader = () => ({
  Authorization: getToken()
});

export interface AuthorizationResponse {
  Authorization: string;
}
