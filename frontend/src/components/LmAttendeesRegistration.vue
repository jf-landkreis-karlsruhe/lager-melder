<script setup lang="ts">
import { computed, onBeforeMount, onMounted, ref } from 'vue'
import { type Attendee, AttendeeRole, getAttendeesForDepartment } from '../services/attendee'
import type { Department } from '../services/department'
import {
  filterEnteredAttendees,
  youthAttendees,
  youthLeaderAttendees
} from '../helper/filterHelper'
import AttendeesTable from './LmAttendeesTable.vue'
import TentsPreregistration from './LmRegistrationInformation.vue'
import { getRegistrationEnd } from '@/services/settings'
import { dateTimeLocalized } from '@/helper/displayDate'

const props = defineProps<{
  department: Department
}>()

const attendees = ref<Attendee[]>([] as Attendee[])
const filterInput = ref<string>('')
const attendeeRoleYouth = ref<AttendeeRole>(AttendeeRole.YOUTH)
const attendeeRoleYouthLeader = ref<AttendeeRole>(AttendeeRole.YOUTH_LEADER)
const totalAttendeeCount = ref<number>(0)

let now = new Date()
let registrationEnd: Date | null = null
let registrationEndLocalized: string = ''
let attendeesCanBeEdited: boolean = false

const youthAttendeeList = computed<Attendee[]>(() => {
  if (!props.department || !props.department.id) {
    return []
  }
  return youthAttendees(props.department.id, attendees.value, filterInput.value)
})

const youthLeaderAttendeeList = computed<Attendee[]>(() => {
  if (!props.department || !props.department.id) {
    return []
  }
  return youthLeaderAttendees(props.department.id, attendees.value, filterInput.value)
})

const enteredAttendeesCount = computed<number>(() => {
  return attendees.value.filter(filterEnteredAttendees).length
})

const registrationEndDiff = computed<
  | {
      days: number
      minutes: number
      hours: number
      seconds: number
    }
  | undefined
>(() => {
  if (!registrationEnd) {
    return undefined
  }
  const diff = registrationEnd.getTime() - now.getTime()
  if (diff < 0) return undefined

  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))
  const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60))
  const seconds = Math.floor((diff % (1000 * 60)) / 1000)
  return { days, hours, minutes, seconds }
})

const attendeesChanged = (change: number) => {
  totalAttendeeCount.value += change
}

onBeforeMount(async () => {
  const response = await getRegistrationEnd()
  registrationEnd = response.registrationEnd
  registrationEndLocalized = dateTimeLocalized(response.registrationEnd)
  attendeesCanBeEdited = response.attendeesCanBeEdited

  setInterval(() => {
    now = new Date()
  }, 1000)
})

onMounted(() => {
  getAttendeesForDepartment(props.department.id).then((attendeeList) => {
    attendees.value = attendeeList
    totalAttendeeCount.value = attendeeList.length
  })
})
</script>

<template>
  <v-container>
    <div>
      <v-alert color="warning" type="warning" border="top">
        <div style="color: #333333">
          <b>
            Anmeldeschluss {{ registrationEndDiff ? 'ist' : 'war' }} am:
            {{ registrationEndLocalized }} Uhr
          </b>
          <br />
          <div v-if="registrationEndDiff">
            Das sind noch {{ registrationEndDiff.days }}
            {{ registrationEndDiff.days === 1 ? 'Tag' : 'Tage' }}
            {{ registrationEndDiff.hours }}
            {{ registrationEndDiff.hours === 1 ? 'Stunde' : 'Stunden' }}
            {{ registrationEndDiff.minutes }}
            {{ registrationEndDiff.minutes === 1 ? 'Minute' : 'Minuten' }}
            <!-- {{ registrationEndDiff.seconds }}
            {{ registrationEndDiff.seconds === 1 ? "Sekunde" : "Sekunden" }} -->
          </div>
          <div v-else>Anmeldeschluss ist bereits erreicht!</div>
        </div>
      </v-alert>

      <div class="d-flex align-baseline">
        <h1 class="mr-4">Teilnehmer {{ department.name }}</h1>
        <div>
          Anzahl Teilnehmer: {{ totalAttendeeCount }} (Anwesend: {{ enteredAttendeesCount }})
        </div>
      </div>
      <v-row>
        <v-col cols="4">
          <v-text-field
            variant="underlined"
            prepend-icon="mdi-magnify"
            v-model="filterInput"
            label="Teilnehmerfilter"
          />
        </v-col>
      </v-row>
    </div>
    <AttendeesTable
      headlineText="Jugendliche"
      :attendees="youthAttendeeList"
      :departmentId="department.id"
      :role="attendeeRoleYouth"
      :attendeesChanged="attendeesChanged"
      :disabled="!attendeesCanBeEdited"
    />
    <AttendeesTable
      headlineText="Jugendleiter"
      :attendees="youthLeaderAttendeeList"
      :role="attendeeRoleYouthLeader"
      :departmentId="department.id"
      :attendeesChanged="attendeesChanged"
      :disabled="!attendeesCanBeEdited"
    />

    <div v-if="department && department.id">
      <TentsPreregistration
        :departmentId="department.id"
        :department-phone-number="department.phoneNumber"
      />
    </div>
  </v-container>
</template>

<style scoped>
.departmentCount {
  color: rgba(0, 0, 0, 0.6);
}
</style>
