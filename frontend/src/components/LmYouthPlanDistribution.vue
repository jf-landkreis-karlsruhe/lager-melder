<script setup lang="ts">
import { ref } from 'vue'
import type { YouthPlanDistribution } from '@/services/youthPlanAttendees'
import { getYouthPlanDistribution } from '@/services/youthPlanAttendees'
import { hasSpecializedFieldDirectorRole } from '../services/authentication'
import { showErrorToast } from '@/helper/fetch'
import { useToast } from 'vue-toastification'

const error = ref<boolean>(false)
const loading = ref<boolean>(false)
const dialogOpen = ref<boolean>(false)
const youthPlanDistribution = ref<YouthPlanDistribution>({
  youthCount: 0,
  leaderCount: 0
})

const toast = useToast()

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
    .catch((e) => {
      showErrorToast(toast, e, 'Fehler beim Laden der Verteilung')
      error.value = true
    })
}
</script>

<template>
  <v-dialog v-model="dialogOpen" persistent max-width="500">
    <template v-slot:activator="{ props }" v-if="hasSpecializedFieldDirectorRole()">
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
