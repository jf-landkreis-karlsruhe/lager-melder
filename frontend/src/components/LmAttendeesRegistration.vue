<script setup lang="ts">
import { computed, onBeforeMount, onMounted, ref } from 'vue'
import {
  type Attendee,
  AttendeeRole,
  type Attendees,
  defaultAttendees,
  getAttendeesForDepartment
} from '../services/attendee'
import { type Department, DepartmentFeatures } from '../services/department'
import { filterByDepartmentAndSearch, filterEnteredAttendees } from '../helper/filterHelper'
import AttendeesTable from './LmAttendeesTable.vue'
import RegistrationInformation from './LmRegistrationInformation.vue'
import { getRegistrationEnd } from '@/services/settings'
import LmRegistrationEndBanner from '@/components/LmRegistrationEndBanner.vue'

const props = defineProps<{
  department: Department
}>()

const attendees = ref<Attendees>(defaultAttendees)
const filterInput = ref<string>('')
const attendeeRoleYouth = ref<AttendeeRole>(AttendeeRole.YOUTH)
const attendeeRoleYouthLeader = ref<AttendeeRole>(AttendeeRole.YOUTH_LEADER)
const attendeeRoleChild = ref<AttendeeRole>(AttendeeRole.CHILD)
const attendeeRoleChildLeader = ref<AttendeeRole>(AttendeeRole.CHILD_LEADER)
const attendeeRoleZKid = ref<AttendeeRole>(AttendeeRole.Z_KID)
const attendeeRoleHelper = ref<AttendeeRole>(AttendeeRole.HELPER)
const totalAttendeeCount = ref<number>(0)

let attendeesRegistrationEnd: Date | null = null
let attendeesCanBeEdited: boolean = false
let childGroupRegistrationEnd: Date | null = null
let childGroupCanBeEdited: boolean = true

const youthAttendeeList = computed<Attendee[]>(() => {
  if (!props.department || !props.department.id) {
    return []
  }
  return filterByDepartmentAndSearch(attendees.value.youths, props.department.id, filterInput.value)
})

const youthLeaderAttendeeList = computed<Attendee[]>(() => {
  if (!props.department || !props.department.id) {
    return []
  }
  return filterByDepartmentAndSearch(
    attendees.value.youthLeaders,
    props.department.id,
    filterInput.value
  )
})

const childAttendeeList = computed<Attendee[]>(() => {
  if (!props.department || !props.department.id) {
    return []
  }
  return filterByDepartmentAndSearch(
    attendees.value.children,
    props.department.id,
    filterInput.value
  )
})

const zKidsAttendeeList = computed<Attendee[]>(() => {
  if (!props.department || !props.department.id) {
    return []
  }
  return filterByDepartmentAndSearch(attendees.value.zKids, props.department.id, filterInput.value)
})

const helpersAttendeeList = computed<Attendee[]>(() => {
  if (!props.department || !props.department.id) {
    return []
  }
  return filterByDepartmentAndSearch(
    attendees.value.helpers,
    props.department.id,
    filterInput.value
  )
})

const childLeaderAttendeeList = computed<Attendee[]>(() => {
  if (!props.department || !props.department.id) {
    return []
  }
  return filterByDepartmentAndSearch(
    attendees.value.childLeaders,
    props.department.id,
    filterInput.value
  )
})

const enteredAttendeesCount = computed<number>(() => {
  return (
    attendees.value.youths.filter(filterEnteredAttendees).length +
    attendees.value.youthLeaders.filter(filterEnteredAttendees).length
  )
})

const attendeesChanged = (change: number) => {
  totalAttendeeCount.value += change
}

onBeforeMount(async () => {
  const response = await getRegistrationEnd()
  attendeesRegistrationEnd = response.registrationEnd
  attendeesCanBeEdited = response.attendeesCanBeEdited
  childGroupRegistrationEnd = response.childGroupRegistrationEnd
  childGroupCanBeEdited = response.childGroupsCanBeEdited
})

onMounted(() => {
  getAttendeesForDepartment(props.department.id).then((attendeeList) => {
    attendees.value = attendeeList
    totalAttendeeCount.value = attendeeList.youths.length + attendeeList.youthLeaders.length
  })
})
</script>

<template>
  <v-container>
    <div v-if="department && department.features.includes(DepartmentFeatures.YOUTH_GROUPS)">
      <div>
        <div class="d-flex align-baseline">
          <h1 class="mr-4">Teilnehmer {{ department.name }}</h1>
          <div>
            Anzahl Teilnehmer: {{ totalAttendeeCount }} (Anwesend: {{ enteredAttendeesCount }})
          </div>
        </div>
        <h2>Teilnehmer</h2>
        <LmRegistrationEndBanner :registrationEnd="attendeesRegistrationEnd" />
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
        formName="youth"
        :attendees="youthAttendeeList"
        :departmentId="department.id"
        :role="attendeeRoleYouth"
        :attendeesChanged="attendeesChanged"
        :disabled="!attendeesCanBeEdited"
      />
      <AttendeesTable
        headlineText="Jugendleiter"
        formName="youthLeader"
        :attendees="youthLeaderAttendeeList"
        :role="attendeeRoleYouthLeader"
        :departmentId="department.id"
        :attendeesChanged="attendeesChanged"
        :disabled="!attendeesCanBeEdited"
      />

      <div v-if="department && department.id">
        <RegistrationInformation
          :departmentId="department.id"
          :department-phone-number="department.phoneNumber"
        />
      </div>
    </div>

    <div v-if="department && department.features.includes(DepartmentFeatures.CHILD_GROUPS)">
      <h2>Kindergruppentag</h2>
      <LmRegistrationEndBanner :registrationEnd="childGroupRegistrationEnd" />
      <AttendeesTable
        headlineText="Kinder"
        formName="child"
        :attendees="childAttendeeList"
        :departmentId="department.id"
        :role="attendeeRoleChild"
        :attendeesChanged="attendeesChanged"
        :disabled="!childGroupCanBeEdited"
      />
      <AttendeesTable
        headlineText="Kindergruppenleiter"
        formName="childLeader"
        :attendees="childLeaderAttendeeList"
        :role="attendeeRoleChildLeader"
        :departmentId="department.id"
        :attendeesChanged="attendeesChanged"
        :disabled="!childGroupCanBeEdited"
      />
    </div>

    <div v-if="department && department.features.includes(DepartmentFeatures.ZKIDS)">
      <h2>Z Kids</h2>
      <LmRegistrationEndBanner :registrationEnd="attendeesRegistrationEnd" />
      <AttendeesTable
        headlineText=""
        formName="zKids"
        :attendees="zKidsAttendeeList"
        :departmentId="department.id"
        :role="attendeeRoleZKid"
        :attendeesChanged="attendeesChanged"
        :disabled="!attendeesCanBeEdited"
      />
    </div>

    <div v-if="department && department.features.includes(DepartmentFeatures.HELPER)">
      <h2>Helfer</h2>
      <LmRegistrationEndBanner :registrationEnd="childGroupRegistrationEnd" />
      <AttendeesTable
        headlineText=""
        formName="helper"
        :attendees="helpersAttendeeList"
        :departmentId="department.id"
        :role="attendeeRoleHelper"
        :attendeesChanged="attendeesChanged"
        :disabled="!childGroupCanBeEdited"
      />
    </div>
  </v-container>
</template>

<style scoped>
.departmentCount {
  color: rgba(0, 0, 0, 0.6);
}
</style>
