<script setup lang="ts">
import { ref, onMounted } from 'vue'
import CheckedInSummary from '@/components/LmCheckedInSummary.vue'
import { type Distribution, type GlobalEventSummary, globalEventSummary } from '@/services/event'
import LmContainer from '../components/LmContainer.vue'
import LmEvacuationConfiguration from '@/components/LmEvacuationConfiguration.vue'
import { type Department, getDepartments } from '@/services/department'
import { type EvacuationGroup, getEvacuationGroup } from '@/services/evacuationGroups'

const departmentSummary = ref<SummaryDepartment | null>(null)
const evacuationGroups = ref<EvacuationGroup[]>([])

interface SummaryDepartment {
  total: Distribution
  departments: DepartmentDistribution[]
}

interface DepartmentDistribution {
  id: number
  department: Department
  distribution: Distribution
}

onMounted(() => {
  getEvacuationGroup().then((evacGroup) => {
    evacuationGroups.value = evacGroup
  })
  Promise.all([globalEventSummary() as Promise<any>, getDepartments()]).then(
    ([summary, deps]: [GlobalEventSummary, Department[]]) => {
      departmentSummary.value = {
        total: summary.total,
        departments: deps
          .map((dep) => ({
            id: dep.id,
            department: dep,
            distribution: createSummary(dep, summary.departments)
          }))
          .sort(sortDepartmentByEvacuationGroup)
      }
    }
  )
})

function createSummary(department: Department, distributions: Distribution[]): Distribution {
  return distributions.find((d) => d.name === department.name) || emptySummary
}

const emptySummary: Distribution = {
  youths: 0,
  youthLeaders: 0,
  zKids: 0,
  children: 0,
  childLeaders: 0,
  paused: false,
  name: 'empty'
}

function sortDepartmentByEvacuationGroup(a: DepartmentDistribution, b: DepartmentDistribution) {
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
</script>

<template>
  <LmContainer>
    <h1>Anwesende</h1>
    <div v-if="departmentSummary !== null">
      <CheckedInSummary
        :departmentDistribution="departmentSummary.total"
        :name="departmentSummary.total.name"
      />
      Pausierte Feuerwehren sind nicht in der Gesamtanzahl enthalten.
      <div v-for="departmentSummary in departmentSummary.departments" :key="departmentSummary.id">
        <CheckedInSummary
          :departmentDistribution="departmentSummary.distribution"
          :name="departmentSummary.department.name"
        />
        <LmEvacuationConfiguration
          :department="departmentSummary.department"
          :distribution="departmentSummary.distribution"
          :evacuation-groups="evacuationGroups"
        />
      </div>
    </div>
  </LmContainer>
</template>
