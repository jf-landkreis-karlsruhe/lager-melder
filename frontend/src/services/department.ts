import { getData, putData } from '../helper/fetch'
import { withAuthenticationHeader, getTokenData } from './authentication'

export const getDepartments = (query?: { onlyWithAttendees?: boolean }) =>
  getData<Department[]>(
    `departments${query?.onlyWithAttendees ? '?onlyWithAttendees=true' : ''}`,
    withAuthenticationHeader()
  )

export const getDepartment = (id: number) =>
  getData<Department>(`departments/${id}`, withAuthenticationHeader())

export const getMyDepartment = () => {
  const departmentId = getTokenData().departmentId
  return getDepartment(departmentId)
}

export const updateDepartment = (department: Department) =>
  putData<Department>(`departments/${department.id}`, withAuthenticationHeader(), department)

export interface Department {
  id: number
  name: string
  leaderName: string
  leaderEMail: string
}
