import { getData, postData, putData, deleteData } from "../helper/fetch";
import { withAuthenticationHeader } from "./authentication";

export const getDepartments = () =>
  getData<Department[]>("departments", withAuthenticationHeader());

export const getDepartment = (id: string) =>
  getData<Department>(`department/${id}`, withAuthenticationHeader());

export const updateDepartment = (department: Department) =>
  putData<Department>(`department/${department.id}`, withAuthenticationHeader(), department);

export interface Department {
  id: string;
  name: string;
  leader: string;
  leaderName: string;
  leaderEMail: string;
}