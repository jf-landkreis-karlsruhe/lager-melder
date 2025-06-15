import { getData } from '@/helper/fetch'
import { withAuthenticationHeader } from './authentication'
import type { Department } from '@/services/department'
import type { Distribution } from '@/services/event'

export interface EvacuationGroup {
  id: string
  name: string
  color: string
}

export const getEvacuationGroup = (): Promise<EvacuationGroup[]> =>
  getData<EvacuationGroup[]>('evacuation-groups', withAuthenticationHeader())

export interface DepartmentDistribution {
  id: number
  department: Department
  distribution: Distribution
}

export interface SummaryDepartment {
  total: Distribution
  departments: DepartmentDistribution[]
}

export function sortDepartmentByEvacuationGroup(a: DepartmentDistribution, b: DepartmentDistribution) {
  const evacuationA = a.department.evacuationGroup?.name
  const evacuationB = b.department.evacuationGroup?.name
  if (evacuationA === undefined && evacuationB === undefined) {
    return a.department.name.localeCompare(b.department.name)
  }
  if (evacuationA === evacuationB) {
    return a.department.name.localeCompare(b.department.name)
  }
  if (evacuationA === undefined) {
    return 1
  }
  if (evacuationB === undefined) {
    return -1
  }
  return evacuationA.localeCompare(evacuationB)
}
