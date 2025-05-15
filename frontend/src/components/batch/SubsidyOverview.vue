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
  <v-card class="pa-6" v-if="subsidy && subsidy.participants">
    <h2>Zuschüsse</h2>
    <p>
      <b>Jugendgruppe</b><br />
      Landesjugendplan: {{ subsidy.participants.stateYouthPlanParticipants }} Teilnehmer,
      {{ subsidy.participants.stateYouthPlanLeaders }} Betreuer <br />
      Jugendamt: {{ subsidy.participants.karlsruheParticipants }} Teilnehmer,
      {{ subsidy.participants.karlsruheLeaders }} Betreuer
    </p>
    <p>
      <b>Kindergruppe</b> <br />
      Landesjugendplan: {{ subsidy.childGroup.stateYouthPlanParticipants }} Teilnehmer,
      {{ subsidy.childGroup.stateYouthPlanLeaders }} Betreuer <br />
      Jugendamt: {{ subsidy.childGroup.karlsruheParticipants }} Teilnehmer,
      {{ subsidy.childGroup.karlsruheLeaders }} Betreuer
    </p>
  </v-card>
</template>

<style scoped lang="scss"></style>
