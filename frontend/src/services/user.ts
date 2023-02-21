import { Roles, withAuthenticationHeader } from "./authentication";
import { Department } from "./department";
import { putData, getData, postData } from "../helper/fetch";

export interface User {
  id: string;
  username: string;
  password?: string;
  role?: Roles;
  department: Department;
}

export const getMe = () =>
  getData<User>(`users/me`, withAuthenticationHeader());

export const changePassword = (user: User) =>
  putData<User>(`users/${user.id}/password`, withAuthenticationHeader(), user);

export const sendRegistrationMail = (userId: string) =>
  postData<User>(
    `users/${userId}/sendRegistrationEmail`,
    withAuthenticationHeader(),
    {}
  );

export const userForDepartment = (departmentId: string) =>
  getData<User>(`users/department/${departmentId}`, withAuthenticationHeader());

export const updateRole = (userId: string, role: Roles) =>
  putData<User>(`users/${userId}/role`, withAuthenticationHeader(), {
    role,
  });

export const createUser = (departmentId: string, username: string) =>
  postData<User>(`users`, withAuthenticationHeader(), {
    departmentId,
    username,
    role: Roles.USER,
  });

export interface DepartmentWithUserRequest {
  username: string;
  departmentName: string;
  leaderName: string;
  leaderEMail: string;
}
export interface DepartmentWithUser extends DepartmentWithUserRequest {
  departmentId: string;
  userId: string;
}

export const registerNewDepartmentAndUser = (
  departmentWithUser: DepartmentWithUserRequest
) => {
  return postData<DepartmentWithUser>(
    `register`,
    withAuthenticationHeader(),
    departmentWithUser
  );
};
