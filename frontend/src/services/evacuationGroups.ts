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

export interface EvacuationGroupSection {
  group: EvacuationGroup
  departments: DepartmentDistribution[]
}

export function groupByEvacuationGroup(departments: DepartmentDistribution[]): EvacuationGroupSection[] {
  return departments
    .filter((dep) => dep.department.evacuationGroup)
    .sort(sortDepartmentByEvacuationGroup)
    .reduce<EvacuationGroupSection[]>((sections, dep) => {
      const group = dep.department.evacuationGroup!
      const last = sections[sections.length - 1]
      if (last?.group.id === group.id) {
        return [...sections.slice(0, -1), { ...last, departments: [...last.departments, dep] }]
      }
      return [...sections, { group, departments: [dep] }]
    }, [])
}
