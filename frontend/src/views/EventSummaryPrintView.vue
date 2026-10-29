<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { type Distribution, type GlobalEventSummary, globalEventSummary } from '@/services/event'
import LmContainer from '../components/LmContainer.vue'
import { type Department, getDepartments } from '@/services/department'
import { groupByEvacuationGroup, type SummaryDepartment } from '@/services/evacuationGroups'
import { hasLKKarlsruheRole } from '@/services/authentication'
import LmDepartmentSummaryShort from '@/components/LmDepartmentSummaryShort.vue'
import CheckedInSummary from '@/components/LmCheckedInSummary.vue'

const departmentSummary = ref<SummaryDepartment | null>(null)

const sections = computed(() => groupByEvacuationGroup(departmentSummary.value?.departments ?? []))

onMounted(() => {
  Promise.all([globalEventSummary(), getDepartments({ onlyWithAttendees: true })]).then(
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
        :department-distribution="departmentSummary.total"
        :paused="false"
        :name="departmentSummary.total.name"
      />
      Pausierte Feuerwehren sind nicht in der Gesamtanzahl enthalten. Feuerwehren ohne Evakuierungsgruppe werden nicht
      angezeigt.
      <section v-for="section in sections" :key="section.group.id" class="evacuation-group">
        <h2 class="evacuation-group__title">
          <span class="evacuation-group__color" :style="{ background: section.group.color }"></span>
          Evakuierungsgruppe: {{ section.group.name }}
        </h2>
        <div v-for="dep in section.departments" :key="dep.id">
          <LmDepartmentSummaryShort :department-distribution="dep" />
        </div>
      </section>
    </div>
  </LmContainer>
</template>

<style lang="scss">
.evacuation-group__title {
  display: flex;
  align-items: center;
  gap: 12px;
  margin: 24px 12px 0;
}

.evacuation-group__color {
  display: inline-block;
  width: 24px;
  height: 24px;
  border: 1px solid #000;
  border-radius: 3px;
  print-color-adjust: exact;
  -webkit-print-color-adjust: exact;
}

@media print {
  .evacuation-group + .evacuation-group {
    break-before: page;
    page-break-before: always;
  }
}
</style>
