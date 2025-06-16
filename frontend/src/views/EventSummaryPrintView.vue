<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { type Distribution, type GlobalEventSummary, globalEventSummary } from '@/services/event'
import LmContainer from '../components/LmContainer.vue'
import { type Department, getDepartments } from '@/services/department'
import {
  type EvacuationGroup,
  getEvacuationGroup,
  sortDepartmentByEvacuationGroup,
  type SummaryDepartment
} from '@/services/evacuationGroups'
import { hasLKKarlsruheRole } from '@/services/authentication'
import LmDepartmentSummaryShort from '@/components/LmDepartmentSummaryShort.vue'
import CheckedInSummary from '@/components/LmCheckedInSummary.vue'

const departmentSummary = ref<SummaryDepartment | null>(null)
const evacuationGroups = ref<EvacuationGroup[]>([])

onMounted(() => {
  getEvacuationGroup().then((evacGroup) => {
    evacuationGroups.value = evacGroup
  })
  Promise.all([globalEventSummary() as Promise<any>, getDepartments({ onlyWithAttendees: true })]).then(
    ([summary, deps]: [GlobalEventSummary, Department[]]) => {
      departmentSummary.value = {
        total: summary.total,
        departments: deps
          .map((dep) => ({
            id: dep.id,
            department: dep,
            distribution: createSummary(dep, summary.departments)
          }))
          .filter(
            (dep) =>
              dep.distribution.helpers != 0 ||
              dep.distribution.children != 0 ||
              dep.distribution.youths != 0 ||
              dep.distribution.youthLeaders != 0 ||
              dep.distribution.childLeaders != 0
          )
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
  children: 0,
  childLeaders: 0,
  helpers: 0,
  name: 'empty'
}
</script>

<template>
  <LmContainer v-if="hasLKKarlsruheRole()">
    <div v-if="departmentSummary !== null">
      <CheckedInSummary
        :departmentDistribution="departmentSummary.total"
        :paused="false"
        :name="departmentSummary.total.name"
      />
      Pausierte Feuerwehren sind nicht in der Gesamtanzahl enthalten.
      <div v-for="dep in departmentSummary.departments" :key="dep.id">
        <LmDepartmentSummaryShort :department-distribution="dep" />
      </div>
    </div>
  </LmContainer>
</template>
