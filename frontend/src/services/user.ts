import { Roles, withAuthenticationHeader } from './authentication'
import type { Department } from './department'
import { putData, getData, postData } from '../helper/fetch'

export interface User {
  id: number
  username: string
  password?: string
  role?: Roles
  department: Department
}

export const getMe = () => getData<User>(`users/me`, withAuthenticationHeader())

export const changePassword = (user: User) =>
  putData<User>(`users/${user.id}/password`, withAuthenticationHeader(), user)

export const sendRegistrationMail = (userId: number) =>
  postData<User>(`users/${userId}/sendRegistrationEmail`, withAuthenticationHeader(), {})

export const userForDepartment = (departmentId: number) =>
  getData<User>(`users/department/${departmentId}`, withAuthenticationHeader())

export const updateRole = (userId: number, role: Roles) =>
  putData<User>(`users/${userId}/role`, withAuthenticationHeader(), {
    role
  })

export const createUser = (departmentId: number, username: string) =>
  postData<User>(`users`, withAuthenticationHeader(), {
    departmentId,
    username,
    role: Roles.USER
  })

export interface DepartmentWithUserRequest {
  username: string
  departmentName: string
  leaderName: string
  leaderEMail: string
}
export interface DepartmentWithUser extends DepartmentWithUserRequest {
  departmentId: number
  userId: number
}

export const registerNewDepartmentAndUser = (departmentWithUser: DepartmentWithUserRequest) => {
  return postData<DepartmentWithUser>(`register`, withAuthenticationHeader(), departmentWithUser)
}
