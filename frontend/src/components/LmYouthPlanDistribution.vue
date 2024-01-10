<script setup lang="ts">
import { ref } from 'vue'
import { getYouthPlanDistribution } from '@/services/youthPlanAttendees'
import type { YouthPlanDistribution } from '@/services/youthPlanAttendees'
import { hasAdministrationRole } from '../services/authentication'

const error = ref<boolean>(false)
const loading = ref<boolean>(false)
const dialogOpen = ref<boolean>(false)
const youthPlanDistribution = ref<YouthPlanDistribution>({
  youthCount: 0,
  leaderCount: 0
})

const closeModal = () => {
  dialogOpen.value = false
}

const openModal = () => {
  loading.value = true
  getYouthPlanDistribution()
    .then((newYouthPlanDistribution) => {
      youthPlanDistribution.value = newYouthPlanDistribution
      loading.value = false
    })
    .catch(() => (error.value = true))
}
</script>

<template>
  <v-dialog v-model="dialogOpen" persistent max-width="500">
    <template v-slot:activator="{ props }" v-if="hasAdministrationRole()">
      <v-btn rounded color="primary" dark v-bind="props" @click="openModal" class="mb-2">
        Verteilung Pädagogischer Betreuer anzeigen
      </v-btn>
    </template>
    <v-card>
      <v-card-title class="headline"> Verteilung Pädagogischer Betreuer </v-card-title>
      <v-card-text v-if="!loading">
        <dl>
          <dt>Teilnehmer</dt>
          <dd>{{ youthPlanDistribution.youthCount }}</dd>
          <dt>Betreuer</dt>
          <dd>{{ youthPlanDistribution.leaderCount }}</dd>
        </dl>
      </v-card-text>
      <v-card-text v-if="loading">
        <v-progress-circular indeterminate :size="24" />
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn rounded @click="closeModal"> Schließen </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<style scoped></style>
