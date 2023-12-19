<script setup lang="ts">
import type { Distribution } from '@/services/event'

const props = defineProps<{
  departmentDistribution: Distribution
}>()
</script>

<template>
  <v-container>
    <div>
      <h2>{{ props.departmentDistribution.name }}</h2>
      <div class="d-flex wrap">
        <div class="d-flex column">
          <div class="number-container checked-in">
            <h3>{{ props.departmentDistribution.checkedInYouth }} ⛺</h3>
            <h5>anwesende Jugendliche</h5>
          </div>
          <div class="number-container checked-in">
            <h3>{{ props.departmentDistribution.checkedInYouthLeader }} ⛺</h3>
            <h5>anwesende Jugendleiter</h5>
          </div>
        </div>

        <div class="d-flex">
          <div class="number-container checked-out">
            <h3>{{ props.departmentDistribution.checkedOutYouth }} 🏠</h3>
            <h5>abwesende Jugendliche</h5>
          </div>

          <div class="number-container checked-out">
            <h3>{{ props.departmentDistribution.checkedOutYouthLeader }} 🏠</h3>
            <h5>abwesende Jugendliche</h5>
          </div>
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

.checked-in {
  background-color: #00ff0030;
}
.checked-out {
  background-color: #ff000030;
}
</style>
