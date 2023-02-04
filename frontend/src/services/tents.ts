import { getData, putData } from "../helper/fetch"
import { withAuthenticationHeader, getTokenData } from "./authentication"

export const getTents = () =>
  getData<Tents[]>("tents/", withAuthenticationHeader())

export const getTentsForMyDepartment = () => {
  const departmentId = getTokenData().departmentId
  return getTentsForDepartment(departmentId)
}

export const getTentsForDepartment = (departmentId: number) =>
  getData<Tents>(
    `departments/${departmentId}/tents`,
    withAuthenticationHeader()
  )

export const updateTents = (tents: Tents) =>
  putData<Tents>(
    `tents/department/${tents.departmentId}`,
    withAuthenticationHeader(),
    tents
  )

export interface Tents {
  id: number
  departmentId: number
  sg200: number
  sg20: number
  sg30: number
  sg40: number
  sg50: number
}
