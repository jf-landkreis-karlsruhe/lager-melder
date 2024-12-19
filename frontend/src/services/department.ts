import { getData, putData } from '../helper/fetch'
import { getTokenData, withAuthenticationHeader } from './authentication'
import type { Tents } from '@/services/tents'
import type { EvacuationGroup } from '@/services/evacuationGroups'

export const getDepartments = (query?: { onlyWithAttendees?: boolean }) =>
  getData<Department[]>(
    `departments${query?.onlyWithAttendees ? '?onlyWithAttendees=true' : ''}`,
    withAuthenticationHeader()
  )

export const getDepartmentsForSelecting = () =>
    getData<DepartmentShort[]>(
        `departments/for-selecting`,
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

export const updatePauseDepartment = (departmentId: number, pause: boolean) =>
  putData<Department>(`departments/${departmentId}/change-pausing`, withAuthenticationHeader(), {
    paused: pause
  })

export const updateTentMarkings = (
  departmentId: number,
  evacuationGroupId: string,
  tentMarkings: TentMarking[]
) =>
  putData<Department>(
    `departments/${departmentId}/evacuation-groups/${evacuationGroupId}/tent-markings`,
    withAuthenticationHeader(),
    tentMarkings
  )

export interface Department {
  id: number
  name: string
  leaderName: string
  leaderEMail: string
  phoneNumber: string
  shortName: string
  features: DepartmentFeatures[]
  headDepartmentName: string
  paused: boolean
  tentMarkings: TentMarking[]
  evacuationGroup?: EvacuationGroup
}

export interface DepartmentShort {
  id: number
  name: string
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

interface TentMarking {
  id: string
  name: string
}
