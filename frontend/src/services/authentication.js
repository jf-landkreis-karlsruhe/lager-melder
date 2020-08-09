import {
    postData
} from '../helper/fetch';

const TOKEN_STORAGE = 'access_token'
export const login = (username, password) => {
    return postData('login', {}, {
            username,
            password
        })
        .then((response) => decodeJWT(response.Authorization))
        .then(jwt => {
            saveJWT(jwt)
            return jwt
        })
}

export const logout = () => {
    localStorage.removeItem(TOKEN_STORAGE);
}

export const isLoggedIn = () => !!getToken()


export const getToken = () => {
    const jwtString = localStorage.getItem(TOKEN_STORAGE);
    if (!jwtString) {
        return undefined
    }
    const jwt = JSON.parse(jwtString)
    if (!jwt || jwt.exp > new Date().getTime()) {
        return undefined
    }
    return jwt
}

const decodeJWT = (jwt) => {
    const base64Url = jwt.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(atob(base64)
        .split('')
        .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join('')
    );
    return JSON.parse(jsonPayload);
}

const saveJWT = (jwt) => {
    localStorage.setItem(TOKEN_STORAGE, JSON.stringify(jwt));
}