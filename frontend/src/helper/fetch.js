import { BASE_URL } from '../assets/config'

export const getData = (relativeUrl, headers) => {
    return fetchData(relativeUrl, {
        headers: {
          'Content-Type': 'application/json',
          ...headers
        },
    }).then(res => res.json())
}

export const postData =  (relativeUrl, headers, body) =>
    postDataRaw(relativeUrl, headers, body)
    .then(res => res.json())

export const postDataRaw = (relativeUrl, headers, body) => {
    return fetchData(relativeUrl, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          ...headers
        },
        body: JSON.stringify(body),
    })
}

export const putData = (relativeUrl, headers, body) => {
    return fetchData(relativeUrl, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          ...headers
        },
        body: JSON.stringify(body),
    }).then(res => res.json())
}

export const delteData = (relativeUrl, headers) => {
    return fetchData(relativeUrl, {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json',
          ...headers
        }
    })
}

const fetchData = (relativeUrl, config) => {
    return fetch(`${BASE_URL}/${relativeUrl}`, config)
        .then(res => {
            if (!res.ok) {
                throw res
            }
            return res
        })
}