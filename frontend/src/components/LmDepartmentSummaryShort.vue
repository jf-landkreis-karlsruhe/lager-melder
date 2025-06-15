<script setup lang="ts">
import type { DepartmentDistribution } from '@/services/evacuationGroups'

const props = defineProps<{
  departmentDistribution: DepartmentDistribution
  hideTents?: boolean
}>()
</script>

<template>
  <v-container class="pb-0 no-break">
    <div>
      <h2 class="mt-0 mb-0">
        <span v-if="departmentDistribution.department.paused">⏸️ </span>
        {{ departmentDistribution.department.name }}
      </h2>
      <div class="d-flex flex-wrap">
        <div class="number-container">
          <h3>{{ props.departmentDistribution.distribution.youths }}</h3>
          <h5>Jugendliche</h5>
        </div>
        <div class="number-container">
          <h3>{{ props.departmentDistribution.distribution.youthLeaders }}</h3>
          <h5>Jugendleiter</h5>
        </div>
        <div class="number-container">
          <h3>{{ props.departmentDistribution.distribution.helpers }}</h3>
          <h5>Helfer</h5>
        </div>
        <div class="number-container">
          <h3>{{ props.departmentDistribution.distribution.children }}</h3>
          <h5>Kinder</h5>
        </div>
        <div class="number-container">
          <h3>{{ props.departmentDistribution.distribution.childLeaders }}</h3>
          <h5>Kindergruppenleiter</h5>
        </div>
      </div>
    </div>

    <div
      :style="
        'background: ' +
        departmentDistribution.department.evacuationGroup?.color +
        '33; padding: 9px 9px 0;border-radius:3px'
      "
      v-if="!props.hideTents"
    >
      <h4>Zelte</h4>
      <div class="d-flex flex-wrap justify-space-between align-baseline">
        <div class="d-flex flex-wrap flex-grow-1">
          <v-chip class="mx-2 my-2" v-for="tent in props.departmentDistribution.department.tentMarkings" size="x-large">
            {{ tent.name }}
          </v-chip>
          <p
            v-if="
              !props.departmentDistribution.department.tentMarkings ||
              props.departmentDistribution.department.tentMarkings.length === 0
            "
          >
            -
          </p>
        </div>
      </div>
    </div>
  </v-container>
</template>

<style scoped>
.number-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 0 15px;
}
@media print {
  .no-break {
    page-break-inside: avoid;
    break-inside: avoid; /* für neuere Browser */
  }
}
</style>
