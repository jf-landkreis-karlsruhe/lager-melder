<script setup lang="ts">
import { computed, onBeforeMount, onMounted, ref } from 'vue'
import {
  type Attendee,
  AttendeeRole,
  type Attendees,
  defaultAttendees,
  getAttendeesForDepartment
} from '../services/attendee'
import { type Department, DepartmentFeatures, getDepartment } from '../services/department'
import { filterByDepartmentAndSearch, filterEnteredAttendees } from '../helper/filterHelper'
import AttendeesTable from './LmAttendeesTable.vue'
import RegistrationInformation from './LmRegistrationInformation.vue'
import { getRegistrationEnd } from '@/services/settings'
import LmRegistrationEndBanner from '@/components/LmRegistrationEndBanner.vue'
import { dateAsText } from '../helper/displayText'

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

const attendeesChanged = (change: number) => {
  totalAttendeeCount.value += change
}

const getDepartmentName = async (departmentId: number) => {
  const department = await getDepartment(departmentId)
  return department.name
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
        <div class="align-baseline">
          <h1 class="mb-0">Teilnehmer {{ department.name }}</h1>
          <div>Anzahl Teilnehmer: {{ totalAttendeeCount }} (Anwesend: {{ enteredAttendeesCount }})</div>
        </div>

        <h2>Teilnehmer</h2>
        <LmRegistrationEndBanner v-if="attendeesRegistrationEnd" :registrationEnd="attendeesRegistrationEnd" />
      </div>

      <v-expansion-panels class="mb-4">
        <v-expansion-panel v-for="attendee in youthAttendeeList" :key="attendee.id">
          <v-expansion-panel-title expand-icon="mdi-menu-down">
            <div class="d-flex justify-space-between align-center flex-1-1-100">
              <div class="d-flex flex-column ga-2" style="flex: 3">
                <span>{{ attendee.firstName }} {{ ' ' }} {{ attendee.lastName }}</span>
                <span>{{ dateAsText(attendee.birthday) }}</span>
              </div>
              <div class="shirt-and-food d-flex" style="flex: 2">
                <div class="shirt d-flex flex-column justify-center align-center mr-4">
                  <v-icon>mdi-tshirt-crew-outline</v-icon>
                  <div class="shirt__size">{{ attendee.tShirtSize }}</div>
                </div>

                <v-icon class="mr-3">mdi-food-drumstick-outline</v-icon>
              </div>

              <div v-if="attendee.juleikaNumber" class="d-flex align-center" style="flex: 3">
                <v-icon class="mr-1">mdi-card-account-details-outline</v-icon>
                <div class="d-flex flex-column ga-2">
                  <span>{{ attendee.juleikaNumber }}</span>
                  <span>{{ dateAsText(attendee.juleikaExpireDate) }}</span>
                </div>
              </div>
              <div v-else-if="attendee.departmentId" class="d-flex align-center" style="flex: 3">
                <v-icon class="mr-1">mdi-account-group-outline</v-icon>
                <span>{{ attendee.departmentId }}</span>
              </div>
              <div v-else-if="(attendee.helperDays?.length ?? 0) > 0" class="d-flex align-center" style="flex: 3">
                <v-icon class="mr-1">mdi-handshake-outline</v-icon>
                <div class="d-flex flex-column ga-2">
                  <span v-for="day in attendee.helperDays" :key="day">{{ day }}</span>
                </div>
              </div>

              <div v-if="attendee.additionalInformation" class="description mr-2" style="flex: 4">
                <i>
                  <v-icon class="mr-1">mdi-information-outline</v-icon>
                  {{ attendee.additionalInformation }}
                </i>
              </div>
            </div>
          </v-expansion-panel-title>
          <v-expansion-panel-text>
            <form>
              <div class="d-flex flex-row ga-4 justify-space-between align-start mt-4">
                <!-- First column -->
                <div class="d-flex flex-column" style="flex: 6">
                  <div class="d-flex align-center ga-4">
                    <v-text-field label="Vorname" variant="outlined" density="comfortable"></v-text-field>
                    <v-text-field label="Nachname" variant="outlined" density="comfortable"></v-text-field>
                  </div>

                  <v-select
                    :items="[
                      { name: 'S', props: { prependIcon: 'mdi-tshirt-crew-outline' } },
                      { name: 'M', props: { prependIcon: 'mdi-tshirt-crew-outline' } },
                      { name: 'TODO', props: { prependIcon: 'mdi-tshirt-crew-outline' } }
                    ]"
                    density="comfortable"
                    variant="outlined"
                    item-title="name"
                    label="T-Shirt-Größe"
                  >
                    <template v-slot:selection="{ item }">
                      <v-icon class="mr-4">mdi-tshirt-crew-outline</v-icon>{{ item.title }}
                    </template>
                    <template v-slot:item="{ props }">
                      <v-list-item v-bind="props"></v-list-item>
                    </template>
                  </v-select>

                  <v-select
                    :items="[
                      { name: 'Fleisch', props: { prependIcon: 'mdi-food-drumstick-outline' } },
                      { name: 'Vegetarisch', props: { prependIcon: 'mdi-cheese' } },
                      { name: 'TODO', props: { prependIcon: 'mdi-food-drumstick-outline' } }
                    ]"
                    density="comfortable"
                    variant="outlined"
                    item-title="name"
                    label="Essen"
                  >
                    <template v-slot:selection="{ item }">
                      <v-icon class="mr-4">mdi-food-drumstick-outline</v-icon>{{ item.title }}
                    </template>
                    <template v-slot:item="{ props }">
                      <v-list-item v-bind="props"></v-list-item>
                    </template>
                  </v-select>
                </div>

                <!-- Second column -->
                <div class="d-flex flex-column" style="flex: 6">
                  <v-select
                    :items="[
                      { name: 'Samstag vorher' },
                      { name: 'Montag' },
                      { name: 'Dienstag' },
                      { name: 'Mittwoch' },
                      { name: 'Donnerstag' },
                      { name: 'Freitag' },
                      { name: 'Samstag' },
                      { name: 'Sonntag' }
                    ]"
                    density="comfortable"
                    variant="outlined"
                    multiple
                    chips
                    item-title="name"
                    label="Helfertage"
                  >
                  </v-select>

                  <v-textarea
                    label="Kommentar"
                    variant="outlined"
                    row-height="14"
                    rows="3.6"
                    auto-grow
                    clearable
                  ></v-textarea>

                  <div class="d-flex ga-4">
                    <v-defaults-provider :defaults="{ VIcon: { color: 'error' } }">
                      <v-btn style="flex: 1" prepend-icon="mdi-trash-can-outline" variant="outlined"> Löschen </v-btn>
                    </v-defaults-provider>
                    <v-defaults-provider :defaults="{ VIcon: { color: 'info' } }">
                      <v-btn style="flex: 1" color="primary" prepend-icon="mdi-check" variant="flat" type="submit">
                        Speichern
                      </v-btn>
                    </v-defaults-provider>
                  </div>
                </div>
              </div>
            </form>
          </v-expansion-panel-text>
        </v-expansion-panel>
      </v-expansion-panels>

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
.shirt-and-food {
  .shirt {
    gap: 0.1rem;

    .shirt__size {
      font-size: 0.8rem !important;
    }
  }
}
</style>
