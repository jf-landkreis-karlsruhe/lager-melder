<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { getSubsidy, type Subsidy } from '@/services/registrationFiles'

const props = defineProps<{
  departmentId: number
}>()

const subsidy = ref<Subsidy>({} as Subsidy)

onMounted(async () => {
  subsidy.value = await getSubsidy(props.departmentId)
})
</script>

<template>
  <v-card class="pa-6">
    <h2>Zuschüsse</h2>
    <p>
      <b>Landesjugendplan:</b> {{ subsidy.stateYouthPlanParticipants }} Teilnehmer,
      {{ subsidy.stateYouthPlanLeaders }} Betreuer,
      {{ subsidy.stateYouthPlanParticipants + subsidy.stateYouthPlanLeaders }} Gesamt
    </p>
    <p>
      <b>Jugendamt:</b> {{ subsidy.karlsruheParticipants }} Teilnehmer, {{ subsidy.karlsruheLeaders }} Betreuer,
      {{ subsidy.karlsruheParticipants + subsidy.karlsruheLeaders }} Gesamt
    </p>
  </v-card>
</template>

<style scoped lang="scss"></style>
