import { describe, expect, it } from 'vitest'
import { type Department } from '@/services/department'
import { type DepartmentDistribution, type EvacuationGroup, groupByEvacuationGroup } from '../evacuationGroups'

const red: EvacuationGroup = { id: 'red', name: 'rot', color: '#de2424' }
const blue: EvacuationGroup = { id: 'blue', name: 'blau', color: '#4a8fdc' }

const departmentDistribution = (
  id: number,
  name: string,
  evacuationGroup?: EvacuationGroup
): DepartmentDistribution => ({
  id,
  department: { id, name, tentMarkings: [], evacuationGroup } as unknown as Department,
  distribution: { name, youths: 1, youthLeaders: 0, children: 0, childLeaders: 0, helpers: 0 }
})

describe('groupByEvacuationGroup', () => {
  it('groups departments by evacuation group, sorted by group name and department name', () => {
    const sections = groupByEvacuationGroup([
      departmentDistribution(1, 'Malsch', red),
      departmentDistribution(2, 'Ettlingen', blue),
      departmentDistribution(3, 'Bruchsal', red),
      departmentDistribution(4, 'Achern', blue)
    ])

    expect(sections.map((section) => section.group.name)).toEqual(['blau', 'rot'])
    expect(sections.map((section) => section.departments.map((dep) => dep.department.name))).toEqual([
      ['Achern', 'Ettlingen'],
      ['Bruchsal', 'Malsch']
    ])
  })

  it('leaves out departments without evacuation group', () => {
    const sections = groupByEvacuationGroup([
      departmentDistribution(1, 'Malsch', red),
      departmentDistribution(2, 'Ettlingen')
    ])

    expect(sections).toHaveLength(1)
    expect(sections[0]!.departments.map((dep) => dep.department.name)).toEqual(['Malsch'])
  })

  it('returns no sections for no departments', () => {
    expect(groupByEvacuationGroup([])).toEqual([])
  })
})
