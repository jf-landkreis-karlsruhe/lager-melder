<script setup lang="ts">
import { ref, watch } from 'vue'
import { type Attendee, AttendeeStatus, getZeltagerIcon } from '@/services/attendee'
import { useToast } from 'vue-toastification'
import { type AttendeeGroup, type AttendeeWithSelected } from '@/components/batch/batchHelper'
import { batchEnterAttendees } from '@/services/event'

const toast = useToast()
const attendeeGroups = ref<AttendeeGroup<AttendeeWithSelected[]>[]>([])
const allSelected = ref<boolean>(false)
const hasAttendees = ref<boolean>(true)

const props = defineProps<{
  headline: string
  attendeeGroups: AttendeeGroup<Attendee[]>[]
  enterCode: string
  leaveCode: string
}>()

watch(
  () => props.attendeeGroups,
  () => {
    attendeeGroups.value = props.attendeeGroups.map((ag) => ({
      headline: ag.headline,
      attendees: ag.attendees.map((a) => ({ ...a, selected: false }) as AttendeeWithSelected)
    }))
    hasAttendees.value = attendeeGroups.value.some((ag) => ag.attendees.length > 0)
  },
  { deep: true }
)

const selectAllAttendees = () => {
  const nextState = attendeeGroups.value.some((ag) => ag.attendees.some((youth) => !youth.selected))
  attendeeGroups.value.flatMap((ag) => ag.attendees).forEach((attendee) => (attendee.selected = nextState))
  allSelected.value = nextState
}

const enter = () => {
  const selectedAttendees = attendeeGroups.value.flatMap((ag) => ag.attendees).filter((a) => a.selected)
  const attendeeCodes = selectedAttendees.map((a) => a.code)
  batchEnterAttendees(props.enterCode, attendeeCodes)
    .then(() => {
      selectedAttendees.forEach((a) => (a.status = AttendeeStatus.ENTERED))
      toast.success(`${attendeeCodes.length} Teilnehmer erfolgreich betreten`)
    })
    .catch(() => {
      toast.error('Fehler beim Betreten der Teilnehmer')
    })
}
const leave = () => {
  const selectedAttendees = attendeeGroups.value.flatMap((ag) => ag.attendees).filter((a) => a.selected)
  const attendeeCodes = selectedAttendees.map((a) => a.code)
  batchEnterAttendees(props.leaveCode, attendeeCodes)
    .then(() => {
      selectedAttendees.forEach((a) => (a.status = AttendeeStatus.LEFT))
      toast.success(`${attendeeCodes.length} Teilnehmer erfolgreich verlassen`)
    })
    .catch(() => {
      toast.error('Fehler beim Verlassen der Teilnehmer')
    })
}
</script>

<template>
  <v-card class="pa-6">
    <form v-on:submit.prevent="enter">
      <h2>{{ props.headline }}</h2>
      <v-checkbox-btn v-if="hasAttendees" v-model="allSelected" @change="selectAllAttendees()" label="Alle auswählen" />
      <div v-for="attendeeGroup in attendeeGroups">
        <h3>{{ attendeeGroup.headline }}</h3>
        <v-checkbox-btn
          v-for="attendee in attendeeGroup.attendees"
          v-model="attendee.selected"
          :label="getZeltagerIcon(attendee) + attendee.firstName + ' ' + attendee.lastName"
        />
        <div v-if="attendeeGroup.attendees.length === 0">-</div>
      </div>

      <div class="w-100 d-flex justify-space-between">
        <v-btn class="mt-6" type="submit">⛺ Eintreten</v-btn>
        <v-btn class="mt-6" @click.prevent="leave">🏠 Verlassen</v-btn>
      </div>
    </form>
  </v-card>
</template>

<style scoped lang="scss"></style>
