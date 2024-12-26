<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { type Attendee, getZeltagerIcon } from '@/services/attendee'
import { useToast } from 'vue-toastification'
import { type AttendeeWithSelected, selectAllNext } from '@/components/batch/batchHelper'
import { batchEnterAttendees } from '@/services/event'

const toast = useToast()
const youths = ref<AttendeeWithSelected[]>([])
const youthLeaders = ref<AttendeeWithSelected[]>([])
const allSelected = ref<boolean>(false)

const props = defineProps<{
  youths: Attendee[]
  youthLeader: Attendee[]
  departmentId: number
  enterCode: string
  leaveCode: string
}>()

onMounted(() => {
  youths.value = props.youths.map((y) => ({ ...y, selected: false }) as AttendeeWithSelected)
  youthLeaders.value = props.youthLeader.map(
    (yL) => ({ ...yL, selected: false }) as AttendeeWithSelected
  )
})

const selectAllYouths = () => {
  const nextState = selectAllNext([].concat(youths.value, youthLeaders.value))
  for (let i = 0; i < youths.value.length; i++) {
    youths.value[i].selected = nextState
  }
  for (let i = 0; i < youthLeaders.value.length; i++) {
    youthLeaders.value[i].selected = nextState
  }
  allSelected.value = nextState
}

const enter = () => {
  const attendeeCodes = []
    .concat(youthLeaders.value, youths.value)
    .filter((a) => a.selected)
    .map((a) => a.code)
  batchEnterAttendees(props.enterCode, attendeeCodes)
    .then(() => {
      toast.success(`${attendeeCodes.length} Teilnehmer erfolgreich betreten`)
    })
    .catch(() => {
      toast.error('Fehler beim Betreten der Teilnehmer')
    })
}
const leave = () => {
  const attendeeCodes = []
    .concat(youthLeaders.value, youths.value)
    .filter((a) => a.selected)
    .map((a) => a.code)
  batchEnterAttendees(props.leaveCode, attendeeCodes)
    .then(() => {
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
      <h2>Übernachtungsgäste</h2>
      <v-checkbox-btn
        v-model="allSelected"
        @change="selectAllYouths()"
        :label="`Alle Jugendliche und Jugendleiter auswählen`"
      />
      <h3>Jugendliche</h3>
      <v-checkbox-btn
        v-for="attendee in youths"
        v-model="attendee.selected"
        :label="getZeltagerIcon(attendee) + attendee.firstName + ' ' + attendee.lastName"
      />
      <h3>Jugendleiter</h3>
      <v-checkbox-btn
        v-for="attendee in youthLeaders"
        v-model="attendee.selected"
        :label="attendee.firstName + ' ' + attendee.lastName"
      />

      <div class="w-100 d-flex justify-space-between">
        <v-btn class="mt-6" type="submit">⛺ Eintreten</v-btn>
        <v-btn class="mt-6" @click.prevent="leave">🏠 Verlassen</v-btn>
      </div>
    </form>
  </v-card>
</template>

<style scoped lang="scss">
.event-root {
  margin-bottom: 8rem;
  position: relative;
}
</style>
