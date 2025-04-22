<script setup lang="ts">
import type { Distribution } from '@/services/event'

const props = defineProps<{
  departmentDistribution: Distribution
  name: string
}>()
</script>

<template>
  <v-container>
    <div>
      <h2 class="mt-8">
        <span v-if="props.departmentDistribution.paused">⏸️ </span>
        {{ props.name }}
      </h2>
      <div class="d-flex flex-wrap">
        <div class="number-container">
          <h3>{{ props.departmentDistribution.youths }}</h3>
          <h5>Jugendliche</h5>
        </div>
        <div class="number-container">
          <h3>{{ props.departmentDistribution.youthLeaders }}</h3>
          <h5>Jugendleiter</h5>
        </div>
        <div class="number-container">
          <h3>{{ props.departmentDistribution.zKids }}</h3>
          <h5>ZKids</h5>
        </div>
        <div class="number-container">
          <h3>{{ props.departmentDistribution.children }}</h3>
          <h5>Kinder</h5>
        </div>
        <div class="number-container">
          <h3>{{ props.departmentDistribution.childLeaders }}</h3>
          <h5>Kindergruppenleiter</h5>
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
  padding: 15px;
}
</style>
