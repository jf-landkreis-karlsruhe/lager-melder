<script setup lang="ts">
import { ref, onMounted } from 'vue'
import CheckedInSummary from '@/components/LmCheckedInSummary.vue'
import { type Distribution, globalEventSummary } from '@/services/event'
import LmContainer from '../components/LmContainer.vue'
import LmEvacuationConfiguration from '@/components/LmEvacuationConfiguration.vue'
import { type Department, getDepartments } from '@/services/department'
import { type EvacuationGroup, getEvacuationGroup } from '@/services/evacuationGroups'

const departmentSummary = ref<SummaryDepartment | null>(null)
const evacuationGroups = ref<EvacuationGroup[]>([])

interface SummaryDepartment {
  total: Distribution
  departments: { id: number; department: Department; distribution: Distribution }[]
}

onMounted(() => {
  getEvacuationGroup().then((evacGroup) => {
    evacuationGroups.value = evacGroup
  })
  evacuationGroups.value = [
    { id: '1', name: 'blau', color: '#0000FF' },
    {
      id: '2',
      name: 'rot',
      color: '#FF0000'
    },
    { id: '3', name: 'grün', color: '#00FF00' },
    { id: '4', name: 'gelb', color: '#FFFF00' }
  ]
  Promise.all([globalEventSummary() as Promise<any>, getDepartments()]).then(([summary, deps]) => {
    departmentSummary.value = {
      total: summary.total,
      departments: deps
        .map((dep) => ({
          id: dep.id,
          department: dep,
          distribution: summary.departments.find((d) => d.name === dep.name) || {
            youths: 0,
            youthLeaders: 0,
            zKids: 0,
            children: 0,
            childLeaders: 0
          }
        }))
        .sort((a, b) => {
          const evacuationA = a.department.evacuationGroup?.name
          const evacuationB = b.department.evacuationGroup?.name
          if (evacuationA !== evacuationB) {
            return evacuationA
              ? evacuationA.localeCompare(evacuationB)
              : evacuationB.localeCompare(evacuationA)
          }
          return a.department.name.localeCompare(b.department.name)
        })
    }
  })
})
</script>

<template>
  <LmContainer>
    <h1>Anwesende</h1>
    <div v-if="departmentSummary !== null">
      <CheckedInSummary :departmentDistribution="departmentSummary.total" />
      Pausierte Feuerwehren sind nicht in der Gesamtanzahl enthalten.
      <div v-for="departmentSummary in departmentSummary.departments" :key="departmentSummary.id">
        <CheckedInSummary :departmentDistribution="departmentSummary.distribution" />
        <LmEvacuationConfiguration
          :department="departmentSummary.department"
          :distribution="departmentSummary.distribution"
          :evacuation-groups="evacuationGroups"
        />
      </div>
    </div>
  </LmContainer>
</template>
