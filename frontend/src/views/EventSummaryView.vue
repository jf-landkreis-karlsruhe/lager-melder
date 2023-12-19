<script setup lang="ts">
import { ref } from 'vue'
import CheckedInSummary from '@/components/LmCheckedInSummary.vue'
import { globalEventSummary } from '@/services/event'
import type { GlobalEventSummary } from '@/services/event'
import { onMounted } from 'vue'

const eventSummary = ref<GlobalEventSummary | null>(null)

onMounted(() => {
  globalEventSummary().then((summary) => {
    eventSummary.value = summary
  })
})
</script>

<template>
  <v-container>
    <h1>Anwesende</h1>
    <div v-if="eventSummary !== null">
      <CheckedInSummary :departmentDistribution="eventSummary.total" />
      <div v-for="departmentSummary in eventSummary.departments" :key="departmentSummary.name">
        <CheckedInSummary :departmentDistribution="departmentSummary" />
      </div>
    </div>
  </v-container>
</template>
