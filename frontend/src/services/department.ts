import { getData, putData } from '../helper/fetch'
import { getTokenData, withAuthenticationHeader } from './authentication'
import type { Tents } from '@/services/tents'

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
  phoneNumber: string
  shortName: string
  features: DepartmentFeatures[]
  headDepartmentName: string
}

export enum DepartmentFeatures {
  "CHILD_GROUPS"= "CHILD_GROUPS",
  "YOUTH_GROUPS" = "YOUTH_GROUPS",
  "ZKIDS" = "ZKIDS",
  "HELPER" = "HELPER",
}

export const updateRegistration = (registration: RegistrationData) =>
  putData<Tents>(
    `departments/registration/${registration.departmentId}`,
    withAuthenticationHeader(),
    registration
  )

export interface RegistrationData {
  departmentId: number
  tents: Tents
  departmentPhoneNumber: string
}
