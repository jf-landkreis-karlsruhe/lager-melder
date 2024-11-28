<script setup lang="ts">
import { computed, onBeforeMount, onMounted, ref } from 'vue'
import {
  type Attendee,
  AttendeeRole,
  type Attendees,
  createAttendee,
  defaultAttendees,
  deleteAttendee as deleteAttendeeService,
  Food,
  getAttendeeDefault,
  getAttendeesForDepartment,
  updateAttendee
} from '../services/attendee'
import { type Department, DepartmentFeatures } from '../services/department'
import { filterByDepartmentAndSearch, filterEnteredAttendees } from '../helper/filterHelper'
import AttendeesTable from './LmAttendeesTable.vue'
import RegistrationInformation from './LmRegistrationInformation.vue'
import { getRegistrationEnd } from '@/services/settings'
import LmRegistrationEndBanner from '@/components/LmRegistrationEndBanner.vue'
import LmAttendeeExpansionPanel from './AttendeeExpansionPanel/LmAttendeeExpansionPanel.vue'
import LmAttendeeAddForm from './AttendeeExpansionPanel/LmAttendeeAddForm.vue'

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
const isAddNewFormModalVisible = ref<boolean>(false)
const newAttendee = ref<Attendee | undefined>(undefined)

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

const addNewAttendee = (role: AttendeeRole) => {
  if (!props.department || !props.department.id) {
    return
  }
  newAttendee.value = getAttendeeDefault(role, props.department.id)
  isAddNewFormModalVisible.value = true
}

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

const saveAttendee = (att: Attendee) => {
  console.debug('🔥 save attende', att)

  if (newAttendee.value && att.id === newAttendee.value.id) {
    createAttendee(att).then((newAtt) => {
      if (!newAttendee.value) return
      const attendeeTypeToRoleMap: Record<AttendeeRole, keyof Attendees> = {
        [AttendeeRole.YOUTH]: 'youths',
        [AttendeeRole.YOUTH_LEADER]: 'youthLeaders',
        [AttendeeRole.CHILD]: 'children',
        [AttendeeRole.CHILD_LEADER]: 'childLeaders',
        [AttendeeRole.Z_KID]: 'zKids',
        [AttendeeRole.HELPER]: 'helpers'
      }
      const attendeeType = attendeeTypeToRoleMap[newAttendee.value.role]
      attendees.value[attendeeType].push(newAttendee.value)
    })
    return
  }
}

// const deleteAttendee = (att: Attendee) => {
//   deletingAttendees.value.push(att.id)
//   deleteAttendeeService(att.id).then(() => {
//     removeAttendeeIdFromList(att.id, deletingAttendees.value)
//     deletedAttendeeIds.value.push(att.id)
//   })
// }

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

        <div class="d-flex justify-space-between align-center">
          <h2>Teilnehmer</h2>

          <label v-if="attendeesCanBeEdited">
            <span class="mr-2" style="cursor: pointer">Teilnehmer hinzufügen</span>
            <v-btn @click="addNewAttendee(attendeeRoleYouth)" color="primary" class="ma-0 mb-1" icon size="x-small">
              <v-icon>mdi-plus</v-icon>
            </v-btn>
          </label>
        </div>
      </div>

      <v-expansion-panels class="mb-4">
        <LmAttendeeExpansionPanel
          v-for="attendee in youthAttendeeList"
          :key="attendee.id"
          :attendee="attendee"
        ></LmAttendeeExpansionPanel>
      </v-expansion-panels>

      <AttendeesTable
        headlineText="Jugendliche"
        formName="youth"
        :attendees="youthAttendeeList"
        :departmentId="department.id"
        :role="attendeeRoleYouth"
        :disabled="!attendeesCanBeEdited"
      />
      <AttendeesTable
        headlineText="Jugendleiter"
        formName="youthLeader"
        :attendees="youthLeaderAttendeeList"
        :role="attendeeRoleYouthLeader"
        :departmentId="department.id"
        :disabled="!attendeesCanBeEdited"
      />

      <div v-if="department && department.id">
        <RegistrationInformation :departmentId="department.id" :department-phone-number="department.phoneNumber" />
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
        :disabled="!childGroupCanBeEdited"
      />
      <AttendeesTable
        headlineText="Kindergruppenleiter"
        formName="childLeader"
        :attendees="childLeaderAttendeeList"
        :role="attendeeRoleChildLeader"
        :departmentId="department.id"
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
        :disabled="!childGroupCanBeEdited"
      />
    </div>
  </v-container>

  <v-dialog v-model="isAddNewFormModalVisible" max-width="900">
    <v-card class="px-6 py-6">
      <LmAttendeeAddForm
        v-if="newAttendee"
        :department="department"
        :attendee="newAttendee"
        :show-cancel="true"
        @save="saveAttendee"
        @cancel="isAddNewFormModalVisible = false"
      />
    </v-card>
  </v-dialog>
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
