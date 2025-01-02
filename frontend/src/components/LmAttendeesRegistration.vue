<script setup lang="ts">
import { computed, onBeforeMount, onMounted, ref } from 'vue'
import {
  type Attendee,
  AttendeeRole,
  type Attendees,
  defaultAttendees,
  deleteAttendee as deleteAttendeeService,
  getAttendeeDefault,
  getAttendeesForDepartment,
  getAttendeeTypeByRole,
  updateAttendee as updateAttendeeService
} from '../services/attendee'
import { type Department, DepartmentFeatures, getDepartmentsForSelecting } from '../services/department'
import { filterByDepartmentAndSearch, filterEnteredAttendees } from '../helper/filterHelper'
import RegistrationInformation from './LmRegistrationInformation.vue'
import { getRegistrationEnd } from '@/services/settings'
import LmRegistrationEndBanner from '@/components/LmRegistrationEndBanner.vue'
import LmAttendeeExpandableWithHeader from './AttendeeExpansionPanel/LmAttendeeExpandableWithHeader.vue'
import type LmAttendeeExpansionPanel from './AttendeeExpansionPanel/LmAttendeeExpansionPanel.vue'
import { getTShirtSizes } from '@/services/tShirtSizes'
import type { DepartmentSelect, TShirtSizeSelect } from '@/components/AttendeeExpansionPanel/helperTypes'
import { type EventDays, getEventDays } from '@/services/eventDays'

const props = defineProps<{
  department: Department
}>()

// Variables
const attendeeRoleYouth: AttendeeRole = AttendeeRole.YOUTH
const attendeeRoleYouthLeader: AttendeeRole = AttendeeRole.YOUTH_LEADER
const attendeeRoleChild: AttendeeRole = AttendeeRole.CHILD
const attendeeRoleChildLeader: AttendeeRole = AttendeeRole.CHILD_LEADER
const attendeeRoleZKid: AttendeeRole = AttendeeRole.Z_KID
const attendeeRoleHelper: AttendeeRole = AttendeeRole.HELPER

let attendeesRegistrationEnd: Date | null = null
let attendeesCanBeEdited: boolean = false
let childGroupRegistrationEnd: Date | null = null
let childGroupCanBeEdited: boolean = true

// REFS
const attendees = ref<Attendees>(defaultAttendees)
const filterInput = ref<string>('')
const tShirtSizes = ref<TShirtSizeSelect[]>([])
const eventDays = ref<EventDays[]>([])
const departments = ref<DepartmentSelect[]>([])

// COMPUTED

const youthAttendeeList = computed<Attendee[]>(() => {
  if (!props.department || !props.department.id) {
    return []
  }
  return filterByDepartmentAndSearch(attendees.value.youths, props.department.id, filterInput.value).map(
    (attendee) => ({ ...attendee, tShirtSizeError: false, helperDaysError: false })
  )
})

const youthLeaderAttendeeList = computed<Attendee[]>(() => {
  if (!props.department || !props.department.id) {
    return []
  }
  return filterByDepartmentAndSearch(attendees.value.youthLeaders, props.department.id, filterInput.value)
})

const childAttendeeList = computed<Attendee[]>(() => {
  if (!props.department || !props.department.id) {
    return []
  }
  return filterByDepartmentAndSearch(attendees.value.children, props.department.id, filterInput.value)
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
  return filterByDepartmentAndSearch(attendees.value.helpers, props.department.id, filterInput.value)
})

const childLeaderAttendeeList = computed<Attendee[]>(() => {
  if (!props.department || !props.department.id) {
    return []
  }
  return filterByDepartmentAndSearch(attendees.value.childLeaders, props.department.id, filterInput.value)
})

const enteredAttendeesCount = computed<number>(() => {
  return (
    attendees.value.youths.filter(filterEnteredAttendees).length +
    attendees.value.youthLeaders.filter(filterEnteredAttendees).length
  )
})

const totalAttendeeCount = computed<number>(() => {
  return attendees.value.youths.length + attendees.value.youthLeaders.length
})

const saveNewAttendee = (newAttendee: Attendee, type: keyof Attendees) => {
  attendees.value = { ...attendees.value, [type]: [...attendees.value[type], newAttendee] }
}

const handleUpdateAttendee = async (att: Attendee, ownRef: InstanceType<typeof LmAttendeeExpansionPanel>) => {
  const attendeeWithAllProps: Attendee = {
    ...getAttendeeDefault(att.role, att.departmentId),
    ...att
  }
  // replace udpated attendee in local list
  attendees.value = {
    ...attendees.value,
    [getAttendeeTypeByRole(att.role)]: attendees.value[getAttendeeTypeByRole(att.role)].map((a) =>
      a.id === att.id ? attendeeWithAllProps : a
    )
  }
  // update attendee in database
  await updateAttendeeService(attendeeWithAllProps)
  // close expansion panel with manual click as other ways didn't work
  ownRef.$el.querySelector('button')?.click()
}

const deleteAttendee = async (att: Attendee) => {
  await deleteAttendeeService(att.id)
  const attendeeType = getAttendeeTypeByRole(att.role)
  attendees.value = { ...attendees.value, [attendeeType]: attendees.value[attendeeType].filter((a) => a.id !== att.id) }
}

onBeforeMount(async () => {
  const response = await getRegistrationEnd()
  attendeesRegistrationEnd = response.registrationEnd
  attendeesCanBeEdited = response.attendeesCanBeEdited
  childGroupRegistrationEnd = response.childGroupRegistrationEnd
  childGroupCanBeEdited = response.childGroupsCanBeEdited
})

onMounted(async () => {
  getAttendeesForDepartment(props.department.id).then((attendeeList) => {
    attendees.value = attendeeList
  })
  tShirtSizes.value = (await getTShirtSizes()).map(
    (shirtSize) =>
      ({
        title: shirtSize,
        props: { prependIcon: 'mdi-tshirt-crew-outline' }
      }) as TShirtSizeSelect
  )
  eventDays.value = await getEventDays()

  getDepartmentsForSelecting().then((data) => {
    departments.value = [
      ...departments.value,
      ...data.map((department) => ({ value: department.id, title: department.name }))
    ]
  })
})
</script>

<template>
  <v-container>
    <div v-if="department && department.features.includes(DepartmentFeatures.YOUTH_GROUPS)">
      <div>
        <div class="align-baseline">
          <h1 class="mb-0">Teilnehmer {{ department.name }}</h1>
          <div>Anzahl Teilnehmer: {{ totalAttendeeCount }} (Anwesend: {{ enteredAttendeesCount }})</div>
        </div>
        <LmRegistrationEndBanner v-if="attendeesRegistrationEnd" :registrationEnd="attendeesRegistrationEnd" />

        <LmAttendeeExpandableWithHeader
          :department="props.department"
          header-label="Jugendliche"
          :attendee-list="youthAttendeeList"
          :role="attendeeRoleYouth"
          :attendeesCanBeEdited="attendeesCanBeEdited"
          :t-shirt-sizes="tShirtSizes"
          :departments="departments"
          :event-days="eventDays"
          @save-new="saveNewAttendee"
          @update="handleUpdateAttendee"
          @delete="deleteAttendee"
        />
      </div>

      <LmAttendeeExpandableWithHeader
        header-label="Jugendleiter"
        :department="props.department"
        :attendee-list="youthLeaderAttendeeList"
        :role="attendeeRoleYouthLeader"
        :attendeesCanBeEdited="attendeesCanBeEdited"
        :t-shirt-sizes="tShirtSizes"
        :departments="departments"
        :event-days="eventDays"
        @save-new="saveNewAttendee"
        @update="handleUpdateAttendee"
        @delete="deleteAttendee"
      />

      <div v-if="department && department.id">
        <RegistrationInformation :departmentId="department.id" :department-phone-number="department.phoneNumber" />
      </div>
    </div>

    <div v-if="department && department.features.includes(DepartmentFeatures.CHILD_GROUPS)">
      <h2>Kindergruppentag</h2>
      <LmRegistrationEndBanner :registrationEnd="childGroupRegistrationEnd" />

      <LmAttendeeExpandableWithHeader
        header-label="Kinder"
        :department="props.department"
        :attendee-list="childAttendeeList"
        :role="attendeeRoleChild"
        :attendeesCanBeEdited="attendeesCanBeEdited"
        :t-shirt-sizes="tShirtSizes"
        :departments="departments"
        :event-days="eventDays"
        @save-new="saveNewAttendee"
        @update="handleUpdateAttendee"
        @delete="deleteAttendee"
      />

      <LmAttendeeExpandableWithHeader
        header-label="Kindergruppenleiter"
        :department="props.department"
        :attendee-list="childLeaderAttendeeList"
        :role="attendeeRoleChildLeader"
        :attendeesCanBeEdited="attendeesCanBeEdited"
        :t-shirt-sizes="tShirtSizes"
        :departments="departments"
        :event-days="eventDays"
        @save-new="saveNewAttendee"
        @update="handleUpdateAttendee"
        @delete="deleteAttendee"
      />
    </div>

    <div v-if="department && department.features.includes(DepartmentFeatures.ZKIDS)">
      <h2>Z Kids</h2>
      <LmRegistrationEndBanner :registrationEnd="attendeesRegistrationEnd" />

      <LmAttendeeExpandableWithHeader
        header-label="zKids"
        :department="props.department"
        :attendee-list="zKidsAttendeeList"
        :role="attendeeRoleZKid"
        :attendeesCanBeEdited="attendeesCanBeEdited"
        :t-shirt-sizes="tShirtSizes"
        :departments="departments"
        :event-days="eventDays"
        @save-new="saveNewAttendee"
        @update="handleUpdateAttendee"
        @delete="deleteAttendee"
      />
    </div>

    <div v-if="department && department.features.includes(DepartmentFeatures.HELPER)">
      <h2>Helfer</h2>
      <LmRegistrationEndBanner :registrationEnd="childGroupRegistrationEnd" />

      <LmAttendeeExpandableWithHeader
        header-label="Helfer"
        :department="props.department"
        :attendee-list="helpersAttendeeList"
        :role="attendeeRoleHelper"
        :attendeesCanBeEdited="childGroupCanBeEdited"
        :t-shirt-sizes="tShirtSizes"
        :departments="departments"
        :event-days="eventDays"
        @save-new="saveNewAttendee"
        @update="handleUpdateAttendee"
        @delete="deleteAttendee"
      />
    </div>
  </v-container>
</template>

<style lang="scss">
// Remove default vuetify filter from v-icon
.v-icon {
  filter: none;
}
</style>

<style scoped lang="scss">
.shirt-and-food {
  .shirt {
    gap: 0.1rem;

    .shirt__size {
      font-size: 0.8rem !important;
    }
  }
}
</style>
