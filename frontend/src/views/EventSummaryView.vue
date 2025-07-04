<script setup lang="ts">
import { onMounted, ref } from 'vue'
import CheckedInSummary from '@/components/LmCheckedInSummary.vue'
import { type Distribution, type GlobalEventSummary, globalEventSummary } from '@/services/event'
import LmContainer from '../components/LmContainer.vue'
import LmEvacuationConfiguration from '@/components/LmEvacuationConfiguration.vue'
import { type Department, getDepartments, updatePauseDepartment } from '@/services/department'
import {
  type EvacuationGroup,
  getEvacuationGroup,
  sortDepartmentByEvacuationGroup,
  type SummaryDepartment
} from '@/services/evacuationGroups'
import { hasLKKarlsruheRole } from '@/services/authentication'
import { useToast } from 'vue-toastification'

const departmentSummary = ref<SummaryDepartment | null>(null)
const evacuationGroups = ref<EvacuationGroup[]>([])

const toast = useToast()

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

const updatePauseDepartmentInternal = (department: Department) => {
  updatePauseDepartment(department.id, !department.paused).then(() => {
    if (!departmentSummary.value) {
      return
    }

    const departmentWrappers = departmentSummary.value.departments.map((depWrapper) =>
      depWrapper.id === department.id
        ? {
            ...depWrapper,
            department: { ...depWrapper.department, paused: !department.paused }
          }
        : depWrapper
    )
    const newTotal = departmentWrappers
      .filter((dep) => !dep.department.paused)
      .reduce(
        (acc, dep) => ({
          name: acc.name,
          youths: acc.youths + dep.distribution.youths,
          youthLeaders: acc.youthLeaders + dep.distribution.youthLeaders,
          helpers: acc.helpers + dep.distribution.helpers,
          children: acc.children + dep.distribution.children,
          childLeaders: acc.childLeaders + dep.distribution.childLeaders
        }),
        emptySummary
      )

    departmentSummary.value = {
      ...departmentSummary.value,
      total: {
        ...departmentSummary.value.total,
        youths: newTotal.youths,
        youthLeaders: newTotal.youthLeaders,
        helpers: newTotal.helpers,
        children: newTotal.children,
        childLeaders: newTotal.childLeaders
      },
      departments: departmentWrappers
    }

    toast.success(` ${department.name} erfolgreich ${department.paused ? 'abgemeldet' : 'zurückgemeldet'}`)
  })
}
</script>

<template>
  <LmContainer v-if="hasLKKarlsruheRole()">
    <h1>Anwesende</h1>
    <router-link to="/anwesend-print"> Druckansicht </router-link>
    <div v-if="departmentSummary !== null">
      <CheckedInSummary
        :departmentDistribution="departmentSummary.total"
        :paused="false"
        :name="departmentSummary.total.name"
      />
      Pausierte Feuerwehren sind nicht in der Gesamtanzahl enthalten.
      <div v-for="dep in departmentSummary.departments" :key="dep.id">
        <CheckedInSummary
          :departmentDistribution="dep.distribution"
          :paused="dep.department.paused"
          :name="dep.department.name"
        />
        <LmEvacuationConfiguration
          :department="dep.department"
          :distribution="dep.distribution"
          :evacuation-groups="evacuationGroups"
        />
        <div class="d-flex justify-space-between align-center flex-grow-1 flex-wrap mt-4">
          <v-btn @click="updatePauseDepartmentInternal(dep.department)" class="checkin" rounded>
            <span v-if="dep.department.paused">Zurückmelden</span>
            <span v-if="!dep.department.paused">Anwesenheit pausieren</span>
          </v-btn>
          <router-link :to="'/feuerwehr-betreten/' + dep.department.id"> Teilnehmer einchecken </router-link>
        </div>
      </div>
    </div>
  </LmContainer>
</template>
