<script setup lang="ts">
import {
  getAttendeesCommunal,
  getAttendeesKarlsruhe,
  getStateYouthPlanAttendees,
  getStateYouthPlanLeader,
  Group
} from '../services/registrationFiles'
import { showFile } from '../services/filesHelper'

const props = defineProps<{
  departmentId: number
  departmentName: string
  group: Group
}>()

const downloadStateYouthPlanLeader = () => {
  getStateYouthPlanLeader(props.departmentId, props.departmentName, props.group).then((fileData) =>
    showFile(fileData.data, fileData.fileName)
  )
}

const downloadAttendeesKarlsruhe = () => {
  getAttendeesKarlsruhe(props.departmentId, props.departmentName, props.group).then((fileData) =>
    showFile(fileData.data, fileData.fileName)
  )
}

const downloadStateYouthPlanAttendees = () => {
  getStateYouthPlanAttendees(props.departmentId, props.departmentName, props.group).then((fileData) =>
    showFile(fileData.data, fileData.fileName)
  )
}

const downloadAttendeesCommunal = () => {
  getAttendeesCommunal(props.departmentId, props.departmentName).then((fileData) =>
    showFile(fileData.data, fileData.fileName)
  )
}
</script>

<template>
  <div>
    <h3 v-if="group === Group.PARTICIPANT">Für Teilnehmer</h3>
    <h3 v-if="group === Group.CHILD_GROUP">Für Kindergruppen</h3>
    <ul>
      <li v-if="group === Group.PARTICIPANT">
        <button class="underline" @click="downloadAttendeesCommunal">Anmeldung</button>
        mit Unterschrift des Kommandanten
      </li>
      <li>
        <button class="underline" @click="downloadStateYouthPlanAttendees">Teilnehmerlisten</button>
        für den Landesjugendplan
      </li>
      <li>
        <button class="underline" @click="downloadStateYouthPlanLeader">Pädagogische Betreuer</button>
        für den Landesjugendplan
      </li>
      <li>
        <button class="underline" @click="downloadAttendeesKarlsruhe">Teilnehmerliste</button>
        für den Zuschuss des Landkreis Karlsruhe
      </li>
    </ul>
  </div>
</template>

<style scoped></style>
